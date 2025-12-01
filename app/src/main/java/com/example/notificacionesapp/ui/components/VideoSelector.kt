package com.example.notificacionesapp.ui.components


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
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
        modifier = modifier
    ) {
        Icon(
                painter = painterResource(id = R.drawable.ic_videocam),
                contentDescription = "Seleccionar video"
        )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seleccionar video")
    }
}