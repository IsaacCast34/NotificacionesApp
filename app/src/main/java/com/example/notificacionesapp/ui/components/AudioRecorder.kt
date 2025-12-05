package com.example.notificacionesapp.ui.components



import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.notificacionesapp.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AudioRecorder(
    onAudioRecorded: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Estado para controlar permisos
    var hasRecordPermission by remember { mutableStateOf(false) }

    // Launcher para solicitar permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasRecordPermission = isGranted
        if (!isGranted) {
            Log.e("AudioRecorder", "Permiso de micrófono DENEGADO")
        }
    }

    // Verificar permiso al iniciar
    LaunchedEffect(Unit) {
        hasRecordPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasRecordPermission) {
            // Solicitar permiso automáticamente
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Mostrar mensaje si no tiene permiso
    if (!hasRecordPermission) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permiso de micrófono requerido",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    ) {
                        Text("Solicitar permiso")
                    }
                }
            }
        }
        return
    }
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Función para crear archivo de audio
    fun createAudioFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".mp3",
            storageDir
        )
    }

    fun startRecording() {
        try {
            Log.d("AudioRecorder", "Iniciando grabación...")
            errorMessage = null
            audioFile = createAudioFile(context)
            audioFile?.let { file ->
                MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(file.absolutePath)

                    try {
                        prepare()
                        start()
                        mediaRecorder = this
                        isRecording = true
                        Log.d("AudioRecorder", "Grabación iniciada: ${file.absolutePath}")
                        errorMessage = null
                    } catch (e: IllegalStateException) {
                        errorMessage = "Error al preparar grabador: ${e.message}"
                        Log.e("AudioRecorder", "IllegalStateException: ${e.message}")
                        release()
                    } catch (e: IOException) {
                        errorMessage = "Error de E/S: ${e.message}"
                        Log.e("AudioRecorder", "IOException: ${e.message}")
                        release()
                    } catch (e: Exception) {
                        errorMessage = "Error inesperado: ${e.message}"
                        Log.e("AudioRecorder", "Exception: ${e.message}")
                        release()
                    }
                }
            } ?: run {
                errorMessage = "No se pudo crear archivo de audio"
                Log.e("AudioRecorder", "No se pudo crear archivo")
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            Log.e("AudioRecorder", "Error general: ${e.message}")
            e.printStackTrace()
        }
    }

    // Función para detener grabación
    fun stopRecording() {
        try {
            Log.d("AudioRecorder", "Deteniendo grabación...")
            mediaRecorder?.apply {
                try {
                    stop()
                    Log.d("AudioRecorder", "Grabación detenida")
                } catch (e: IllegalStateException) {
                    // Puede pasar si se detiene demasiado rápido
                    errorMessage = "Grabación muy corta o ya detenida"
                    Log.e("AudioRecorder", "IllegalStateException al detener: ${e.message}")
                }
                release()
            }
            mediaRecorder = null
            isRecording = false

            audioFile?.let { file ->
                if (file.exists() && file.length() > 0) {
                    val fileSize = file.length() / 1024 // KB
                    Log.d("AudioRecorder", "Archivo creado: ${file.absolutePath}, tamaño: ${fileSize}KB")
                    onAudioRecorded(Uri.fromFile(file))
                    errorMessage = null
                } else {
                    errorMessage = "Archivo de audio vacío o no creado"
                    Log.e("AudioRecorder", "Archivo vacío o no existe")
                    file.delete() // Eliminar archivo vacío
                }
            }
            audioFile = null
        } catch (e: Exception) {
            errorMessage = "Error al detener: ${e.message}"
            Log.e("AudioRecorder", "Error al detener: ${e.message}")
            e.printStackTrace()
        }
    }

    // Limpiar recursos cuando el componente se destruye
    DisposableEffect(Unit) {
        onDispose {
            Log.d("AudioRecorder", "Limpiando recursos...")
            if (isRecording) {
                stopRecording()
            }
            mediaRecorder?.release()
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Mostrar mensajes de error si existen
        errorMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error),
                        contentDescription = "Error",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Indicador de estado
        if (isRecording) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Indicador de grabación animado
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.error,
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Grabando...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Mostrar tamaño si está disponible
                    audioFile?.let { file ->
                        if (file.exists()) {
                            val sizeKB = file.length() / 1024
                            Text(
                                text = "(${sizeKB} KB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón de grabación
        Button(
            onClick = {
                Log.d("AudioRecorder", "Botón presionado. isRecording: $isRecording")

                if (!isRecording) {
                    Log.d("AudioRecorder", "Iniciando grabación...")
                    startRecording()
                } else {
                    Log.d("AudioRecorder", "Deteniendo grabación...")
                    stopRecording()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = if (isRecording) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            } else {
                ButtonDefaults.buttonColors()
            }
        ) {
            Icon(
                painter = painterResource(
                    id = if (isRecording) R.drawable.ic_stop
                    else R.drawable.ic_mic
                ),
                contentDescription = if (isRecording) "Detener" else "Grabar"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRecording)
                    stringResource(R.string.detener_grabacion)
                else
                    stringResource(R.string.iniciar_grabacion)
            )
        }

        // Información adicional
        if (!isRecording && errorMessage == null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca el botón para grabar audio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "El audio se guardará en formato MP3",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}