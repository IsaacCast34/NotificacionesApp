package com.example.notificacionesapp.ui.components


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import com.example.notificacionesapp.RequestPermissions
import com.example.notificacionesapp.getMediaPermissions

@Composable
fun ImageSelector(
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State to control when to request permissions
    var launchPermissionsRequest by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    // --- FIX START ---
    // Conditionally launch the Composable based on the state
    if (launchPermissionsRequest) {
        RequestPermissions(
            permissions = getMediaPermissions(),
            onPermissionsGranted = {
                showImagePicker = true
                // Reset the state to false so it doesn't relaunch on recomposition
                launchPermissionsRequest = false
            },
            // Optional: Handle the case where permissions are denied
            onPermissionsDenied = {
                launchPermissionsRequest = false
                // You could show a snackbar or toast here to inform the user
            }
        )
    }
    // --- FIX END ---

    Button(
        onClick = {
            // Instead of calling the Composable directly, update the state.
            // This will trigger a recomposition, and the `if` block above will execute.
            launchPermissionsRequest = true
        },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_photo),
            contentDescription = stringResource(R.string.seleccionar_imagen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource(R.string.seleccionar_imagen))
    }

    if (showImagePicker) {
        AlertDialog(
            onDismissRequest = { showImagePicker = false },
            title = { Text(stringResource(R.string.seleccionar_imagen)) },
            text = { Text(stringResource(R.string.elegir_metodo)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImagePicker = false
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            galleryPicker.launch("image/*")
                        }
                    }
                ) {
                    Text(stringResource(R.string.galeria))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showImagePicker = false }
                ) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}