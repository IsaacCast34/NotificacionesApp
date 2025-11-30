package com.example.notificacionesapp.ui.components


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R

@Composable
fun VideoSelector(
    onVideoSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onVideoSelected(uri)
    }

    Button(
        onClick = {
            videoPicker.launch("video/*")
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primaryVariant
        )
    ) {
        Icon(
            imageVector = Icons.Default.Videocam,
            contentDescription = stringResource(R.string.seleccionar_video)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.seleccionar_video))
    }
}