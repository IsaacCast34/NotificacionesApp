package com.example.notificacionesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.ui.components.ListaNotas
import com.example.notificacionesapp.viewmodel.NotaViewModel

@Composable
fun HomeScreen(navController: NavController, notaViewModel: NotaViewModel) {
    val notas = notaViewModel.todasLasNotas.value ?: emptyList()

    val padding = dimensionResource(id = R.dimen.padding_normal)
    val titleSize = dimensionResource(id = R.dimen.text_size_title)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notas),
                        fontSize = titleSize.value.sp
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("notaForm") }) {
                Text("+", fontSize = titleSize.value.sp)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(padding)) {
            ListaNotas(notas) { nota ->
                // Navegación futura a detalle si se desea
            }
        }
    }
}


