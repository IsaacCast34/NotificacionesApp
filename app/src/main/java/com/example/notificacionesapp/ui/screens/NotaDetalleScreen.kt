package com.example.notificacionesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.viewmodel.NotaViewModel
import androidx.compose.ui.unit.dp
@Composable
fun NotaDetalleScreen(
    notaId: Int,
    navController: NavController,
    notaViewModel: NotaViewModel
) {
    val nota by notaViewModel.obtenerNotaPorId(notaId).collectAsState(initial = null)

    nota?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = it.titulo, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it.descripcion, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Hora: ${it.hora}", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (it.completado) "Completado" else "Pendiente",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    navController.navigate("editarNota/${it.id}")
                }) {
                    Text("Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        notaViewModel.eliminar(it)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        }
    } ?: run {
        Text(
            text = "Nota no encontrada",
            modifier = Modifier.padding(16.dp)
        )
    }
}
