package com.example.notificacionesapp.ui.components


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R

@Composable
fun ImageSelector(
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Launcher simple y directo - enfoque CodeLab
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Button(
        onClick = {
            // Lanzar directamente el selector de imágenes
            imagePicker.launch("image/*")
        },
        modifier = modifier

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_photo),
            contentDescription = "Seleccionar imagen"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.seleccionar_imagen))
    }
}