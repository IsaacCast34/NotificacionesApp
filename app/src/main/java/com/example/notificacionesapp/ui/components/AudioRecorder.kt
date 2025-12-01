package com.example.notificacionesapp.ui.components



import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AudioRecorder(
    onAudioRecorded: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    // Función para crear archivo de audio
    fun createAudioFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".3gp",
            storageDir
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Indicador de estado
        if (isRecording) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Grabando...")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botón de grabación
        Button(
            onClick = {
                if (!isRecording) {
                    // INICIAR GRABACIÓN
                    try {
                        audioFile = createAudioFile(context)
                        audioFile?.let { file ->
                            MediaRecorder().apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                                setOutputFile(file.absolutePath)
                                prepare()
                                start()

                                mediaRecorder = this
                                isRecording = true
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        mediaRecorder?.apply {
                            stop()
                            release()
                        }
                        mediaRecorder = null
                        isRecording = false

                        audioFile?.let { file ->
                            onAudioRecorded(Uri.fromFile(file))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(
                    id = if (isRecording) R.drawable.ic_stop
                    else R.drawable.ic_mic
                ),
                contentDescription = if (isRecording) "Detener grabación"
                else "Iniciar grabación"
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
        if (!isRecording) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toca para grabar audio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}