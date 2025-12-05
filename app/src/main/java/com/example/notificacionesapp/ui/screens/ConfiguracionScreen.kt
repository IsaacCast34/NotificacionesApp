package com.example.notificacionesapp.ui.screens


import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.LocalizationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Obtener el idioma actual
    val currentLanguage = remember { LocalizationManager.getSavedLanguage(context) }
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cambiar Idioma",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selección de idioma
                    Column(
                        modifier = Modifier.selectableGroup()
                    ) {
                        // Español
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedLanguage == "es",
                                    onClick = {
                                        selectedLanguage = "es"
                                        scope.launch {
                                            LocalizationManager.setLocale(context, "es")
                                            // Reiniciar actividad
                                            (context as? android.app.Activity)?.recreate()
                                        }
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == "es",
                                onClick = {
                                    selectedLanguage = "es"
                                    scope.launch {
                                        LocalizationManager.setLocale(context, "es")
                                        (context as? android.app.Activity)?.recreate()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Español",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Inglés
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedLanguage == "en",
                                    onClick = {
                                        selectedLanguage = "en"
                                        scope.launch {
                                            LocalizationManager.setLocale(context, "en")
                                            (context as? android.app.Activity)?.recreate()
                                        }
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLanguage == "en",
                                onClick = {
                                    selectedLanguage = "en"
                                    scope.launch {
                                        LocalizationManager.setLocale(context, "en")
                                        (context as? android.app.Activity)?.recreate()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "English",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Información
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_info),
                                contentDescription = "Información",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "La app se reiniciará para aplicar el idioma",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}