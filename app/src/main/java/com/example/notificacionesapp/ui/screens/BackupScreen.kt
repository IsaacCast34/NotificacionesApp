package com.example.notificacionesapp.ui.screens


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.backup.BackupManager
import com.example.notificacionesapp.ui.components.LoadingScreen
import com.example.notificacionesapp.viewmodel.NotaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    navController: NavController,
    notaViewModel: NotaViewModel
) {
    val context = LocalContext.current
    val backupManager = remember { BackupManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val notesState by notaViewModel.todasLasNotas.collectAsState(initial = emptyList())

    var exportMessage by remember { mutableStateOf<String?>(null) }
    var importMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            isLoading = true
            coroutineScope.launch {
                // 2. USA LA VARIABLE 'notesState' QUE YA TIENE EL VALOR ACTUAL
                val success = backupManager.exportNotes(notesState, uri)
                exportMessage = if (success) {
                    "Backup exportado exitosamente"
                } else {
                    "Error al exportar backup"
                }
                isLoading = false
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            isLoading = true
            coroutineScope.launch {
                val importedNotes = backupManager.importNotes(uri)
                if (importedNotes.isNotEmpty()) {
                    importedNotes.forEach { nota ->
                        notaViewModel.insertar(nota.copy(id = 0))
                    }
                    importMessage = "${importedNotes.size} notas importadas exitosamente"
                } else {
                    importMessage = "Error al importar backup o archivo vacío"
                }
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup y Restauración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (isLoading) {
                LoadingScreen("Procesando...")
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Exportar Backup",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Guarda una copia de seguridad de todas tus notas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                exportLauncher.launch(backupManager.generateBackupFileName())
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_backup),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exportar Backup")
                        }
                        exportMessage?.let { message ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.contains("éxito")) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Importar Backup",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Restaura tus notas desde un archivo de backup",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                importLauncher.launch("application/json")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_restore),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Importar Backup")
                        }
                        importMessage?.let { message ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.contains("éxito")) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}