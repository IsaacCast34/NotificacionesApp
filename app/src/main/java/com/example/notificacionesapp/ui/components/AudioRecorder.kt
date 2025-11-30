package com.example.notificacionesapp.ui.components



import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            Card(
                backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colors.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.grabando),
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.body2
                    )
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
                        // Podrías mostrar un Toast aquí
                    }
                } else {
                    // DETENER GRABACIÓN
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
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isRecording) MaterialTheme.colors.error
                else MaterialTheme.colors.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isRecording)
                    stringResource(R.string.detener_grabacion)
                else
                    stringResource(R.string.iniciar_grabacion)
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
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}