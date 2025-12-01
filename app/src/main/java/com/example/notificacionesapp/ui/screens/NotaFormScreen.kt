package com.example.notificacionesapp.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.entities.Nota
import com.example.notificacionesapp.notification.AlarmReceiver
import com.example.notificacionesapp.ui.components.MultimediaSection
import com.example.notificacionesapp.ui.components.PrioridadChip
import com.example.notificacionesapp.viewmodel.NotaViewModel
import kotlinx.coroutines.launch
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaFormScreen(
    notaViewModel: NotaViewModel,
    navController: NavController,
    notaId: Int?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var completado by remember { mutableStateOf(false) }
    var prioridad by remember { mutableStateOf("Media") }
    var imagenUri by remember { mutableStateOf<String?>(null) }
    var audioUri by remember { mutableStateOf<String?>(null) }
    var videoUri by remember { mutableStateOf<String?>(null) }
    var categoria by remember { mutableStateOf("General") }
    var etiquetas by remember { mutableStateOf("") }

    LaunchedEffect(notaId) {
        if (notaId != null && notaId != 0) {
            notaViewModel.obtenerNotaPorId(notaId).collect { nota ->
                nota?.let {
                    titulo = it.titulo
                    descripcion = it.descripcion
                    hora = it.hora
                    completado = it.completado
                    prioridad = it.prioridad
                    imagenUri = it.imagenUri
                    audioUri = it.audioUri
                    videoUri = it.videoUri
                    categoria = it.categoria
                    etiquetas = it.etiquetas
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (notaId == null) "Nueva Nota" else "Editar Nota"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (titulo.isNotBlank() && hora.isNotBlank()) {
                        val nota = Nota(
                            id = notaId ?: 0,
                            titulo = titulo,
                            descripcion = descripcion,
                            hora = hora,
                            completado = completado,
                            prioridad = prioridad,
                            categoria = categoria,
                            etiquetas = etiquetas,
                            imagenUri = imagenUri,
                            audioUri = audioUri,
                            videoUri = videoUri
                        )

                        scope.launch {
                            if (notaId == null) {
                                notaViewModel.insertar(nota)
                                programarAlarma(context, nota)
                                Toast.makeText(context, "Nota creada exitosamente", Toast.LENGTH_SHORT).show()
                            } else {
                                cancelarAlarma(context, notaId)
                                notaViewModel.actualizar(nota)
                                programarAlarma(context, nota)
                                Toast.makeText(context, "Nota actualizada exitosamente", Toast.LENGTH_SHORT).show()
                            }
                            navController.popBackStack()
                        }
                    } else {
                        Toast.makeText(context, "Título y hora son obligatorios", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Guardar"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información Básica",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    hora = "%02d:%02d".format(hour, minute)
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Seleccionar Hora *")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hora seleccionada: $hora",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Estado y Prioridad",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = completado,
                            onCheckedChange = { completado = it }
                        )
                        Text("Completado")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Prioridad:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        PrioridadChip(
                            texto = "Alta",
                            seleccionada = prioridad == "Alta",
                            onClick = { prioridad = "Alta" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PrioridadChip(
                            texto = "Media",
                            seleccionada = prioridad == "Media",
                            onClick = { prioridad = "Media" }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PrioridadChip(
                            texto = "Baja",
                            seleccionada = prioridad == "Baja",
                            onClick = { prioridad = "Baja" }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MultimediaSection(
                imageUri = imagenUri,
                audioUri = audioUri,
                videoUri = videoUri,
                onImageSelected = { uri ->
                    imagenUri = uri?.toString()
                },
                onAudioRecorded = { uri ->
                    audioUri = uri.toString()
                },
                onVideoSelected = { uri ->
                    videoUri = uri?.toString()
                },
                onRemoveImage = { imagenUri = null },
                onRemoveAudio = { audioUri = null },
                onRemoveVideo = { videoUri = null },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "* Campos obligatorios: Título y Hora",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun PrioridadChip(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (seleccionada) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.surface
    val contentColor = if (seleccionada) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurface

    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
        shadowElevation = if (seleccionada) 4.dp else 0.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun programarAlarma(context: Context, nota: Nota) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("titulo", nota.titulo)
        putExtra("mensaje", nota.descripcion.ifEmpty { "Recordatorio de nota" })
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        nota.id,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val partesHora = nota.hora.split(":")
    if (partesHora.size == 2) {
        val hora = partesHora[0].toInt()
        val minuto = partesHora[1].toInt()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}

private fun cancelarAlarma(context: Context, notaId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, AlarmReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notaId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}