package com.example.notificacionesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
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

    val padding = dimensionResource(id = R.dimen.padding_normal)
    val titleSize = dimensionResource(id = R.dimen.text_size_title)
    val bodySize = dimensionResource(id = R.dimen.text_size_body)

    nota?.let {
        Column(modifier = Modifier.padding(padding)) {
            Text(text = it.titulo, fontSize = titleSize.value.sp)
            Spacer(modifier = Modifier.height(padding))
            Text(text = it.descripcion, fontSize = bodySize.value.sp)
            Spacer(modifier = Modifier.height(padding))
            Text("${stringResource(R.string.hora)}: ${it.hora}", fontSize = bodySize.value.sp)
            Text(
                text = if (it.completado)
                    stringResource(R.string.completado)
                else
                    stringResource(R.string.pendiente),
                fontSize = bodySize.value.sp
            )
            Spacer(modifier = Modifier.height(padding))

            Row {
                Button(onClick = {
                    navController.navigate("editarNota/${it.id}")
                }) {
                    Text(stringResource(R.string.editar))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    notaViewModel.eliminar(it)
                    navController.popBackStack()
                }) {
                    Text(stringResource(R.string.eliminar))
                }
            }
        }
    } ?: run {
        Text(
            text = stringResource(R.string.nota_no_encontrada),
            modifier = Modifier.padding(padding)
        )
    }
}

