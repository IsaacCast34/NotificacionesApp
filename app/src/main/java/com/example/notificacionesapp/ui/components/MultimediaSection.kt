package com.example.notificacionesapp.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MultimediaSection(
    imageUri: String?,
    audioUri: String?,
    videoUri: String?,
    onImageSelected: (Uri?) -> Unit,
    onAudioRecorded: (Uri) -> Unit,
    onVideoSelected: (Uri?) -> Unit,
    onRemoveImage: () -> Unit,
    onRemoveAudio: () -> Unit,
    onRemoveVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Multimedia",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Imagen
            Text("Imagen", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            ImageSelector(
                onImageSelected = onImageSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            ImagePreview(
                imageUri = imageUri,
                onRemoveImage = onRemoveImage
            )

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Audio
            Text("Audio", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            AudioRecorder(
                onAudioRecorded = onAudioRecorded,
                modifier = Modifier.fillMaxWidth()
            )

            if (!audioUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                AudioPlayer(
                    audioUri = audioUri,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onRemoveAudio) {
                    Text("Eliminar audio")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Sección de Video
            Text("Video", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            VideoSelector(
                onVideoSelected = onVideoSelected,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            VideoPlayer(
                videoUri = videoUri,
                onRemoveVideo = onRemoveVideo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}