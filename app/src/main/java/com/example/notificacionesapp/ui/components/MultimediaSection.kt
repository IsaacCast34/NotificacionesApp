package com.example.notificacionesapp.ui.components


import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R

@Composable
fun MultimediaSection(
    imageUri: String?,
    audioUri: String?,
    onImageSelected: (Uri?) -> Unit,
    onAudioRecorded: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onRemoveAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.multimedia),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            AudioRecorder(
                onAudioRecorded = onAudioRecorded,
                modifier = Modifier.fillMaxWidth()
            )

            if (!audioUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.audio_grabado),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}