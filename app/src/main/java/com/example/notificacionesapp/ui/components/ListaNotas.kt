package com.example.notificacionesapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.entities.Nota

@Composable
fun ListaNotas(notas: List<Nota>, onNotaClick: (Nota) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(notas, key = { it.id }) { nota ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onNotaClick(nota) },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = nota.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(text = nota.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Text("${stringResource(R.string.hora)}: ${nota.hora}", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = if (nota.completado)
                            stringResource(R.string.completado)
                        else
                            stringResource(R.string.pendiente),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
