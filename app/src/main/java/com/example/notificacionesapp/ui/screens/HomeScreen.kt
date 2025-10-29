package com.example.notificacionesapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.ui.components.ListaNotas
import com.example.notificacionesapp.viewmodel.NotaViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Esta también es útil


@Composable
fun HomeScreen(navController: NavController, notaViewModel: NotaViewModel) {
    val notas by notaViewModel.todasLasNotas.collectAsState(initial = emptyList())

    val padding = dimensionResource(id = R.dimen.padding_normal)

    // SOLUCIÓN 3: Extraer el valor float del recurso
    val titleSizeDp = dimensionResource(id = R.dimen.text_size_title)
    val titleSize = titleSizeDp.value.sp

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.notas),
                        fontSize = titleSize
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("notaForm") }) {
                Text("+", fontSize = titleSize)
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