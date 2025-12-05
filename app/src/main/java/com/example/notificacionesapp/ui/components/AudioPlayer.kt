package com.example.notificacionesapp.ui.components

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import kotlinx.coroutines.delay
import java.io.IOException

@Composable
fun AudioPlayer(
    audioUri: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    LaunchedEffect(isPlaying, currentPosition) {
        if (isPlaying) {
            while (isPlaying) {
                delay(500)
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        currentPosition = player.currentPosition
                    } else {
                        isPlaying = false
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    if (!audioUri.isNullOrEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Audio grabado")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isPlaying) "Reproduciendo..." else "Listo para reproducir",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Botones de control
                    Row {
                        Button(
                            onClick = {
                                if (!isPlaying) {
                                    try {
                                        MediaPlayer().apply {
                                            setDataSource(context, Uri.parse(audioUri))
                                            setOnPreparedListener { player ->
                                                duration = player.duration
                                                player.start()
                                                isPlaying = true
                                            }
                                            setOnCompletionListener {
                                                isPlaying = false
                                                currentPosition = 0
                                            }
                                            prepareAsync()
                                            mediaPlayer = this
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    // Detener
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                    isPlaying = false
                                    currentPosition = 0
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPlaying) R.drawable.ic_stop
                                    else R.drawable.ic_play
                                ),
                                contentDescription = if (isPlaying) "Detener" else "Reproducir"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isPlaying) "Detener" else "Reproducir")
                        }
                    }

                    if (isPlaying && duration > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = currentPosition.toFloat() / duration.toFloat(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${currentPosition / 1000}s / ${duration / 1000}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}


