package com.example.notificacionesapp


import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissions(
    permissions: List<String>,
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit = {}
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val allGranted = permissionsMap.values.all { it }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            onPermissionsDenied()
        }
    }

    LaunchedEffect(Unit) {
        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            onPermissionsGranted()
        } else {
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }
}

fun getMediaPermissions(): List<String> {
    val permissions = mutableListOf<String>()

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
    }

    permissions.add(Manifest.permission.CAMERA)
    permissions.add(Manifest.permission.RECORD_AUDIO)

    return permissions
}