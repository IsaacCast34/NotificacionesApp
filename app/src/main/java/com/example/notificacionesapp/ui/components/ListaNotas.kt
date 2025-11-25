package com.example.notificacionesapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.entities.Nota

@Composable
fun ListaNotas(notas: List<Nota>, onNotaClick: (Nota) -> Unit) {
    val padding = dimensionResource(id = R.dimen.padding_normal)
    val titleSize = dimensionResource(id = R.dimen.text_size_title)
    val bodySize = dimensionResource(id = R.dimen.text_size_body)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notas.size) { index ->
            val nota = notas[index]
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .clickable { onNotaClick(nota) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // ← Corrección aquí
            ) {
                Column(modifier = Modifier.padding(padding)) {
                    Text(text = nota.titulo, fontSize = titleSize.value.sp)
                    Text(text = nota.descripcion, fontSize = bodySize.value.sp)
                    Text("${stringResource(R.string.hora)}: ${nota.hora}", fontSize = bodySize.value.sp)
                    Text(
                        text = if (nota.completado)
                            stringResource(R.string.completado)
                        else
                            stringResource(R.string.pendiente),
                        fontSize = bodySize.value.sp
                    )
                }
            }
        }
    }
}
