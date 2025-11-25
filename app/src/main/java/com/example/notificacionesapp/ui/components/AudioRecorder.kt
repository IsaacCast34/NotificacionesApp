package com.example.notificacionesapp.ui.components


import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import com.example.notificacionesapp.RequestPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.res.painterResource

@Composable
fun AudioRecorder(
    onAudioRecorded: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    fun createAudioFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        return File.createTempFile(
            "AUDIO_${timeStamp}_",
            ".3gp",
            storageDir
        ).apply {
            createNewFile()
        }
    }
    RequestPermissions(
        permissions = listOf(Manifest.permission.RECORD_AUDIO),
        onPermissionsGranted = { /* Listo para grabar */ }
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (!isRecording) {
                    // Iniciar grabación
                    audioFile = createAudioFile(context)
                    audioFile?.let { file ->
                        try {
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    // Detener grabación
                    mediaRecorder?.apply {
                        stop()
                        release()
                    }
                    mediaRecorder = null
                    isRecording = false

                    audioFile?.let { file ->
                        onAudioRecorded(Uri.fromFile(file))
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isRecording) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_stop),
                    contentDescription = stringResource(R.string.detener_grabacion)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.mic),
                    contentDescription = stringResource(R.string.iniciar_grabacion)
                )
            }


            if (isRecording) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.grabando),
                color = MaterialTheme.colorScheme.error
            )
        }
    }}}
