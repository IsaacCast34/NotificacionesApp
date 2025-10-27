package com.example.notificacionesapp.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.entities.Nota
import com.example.notificacionesapp.notifications.AlarmReceiver
import com.example.notificacionesapp.viewmodel.NotaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun NotaFormScreen(
    notaViewModel: NotaViewModel,
    navController: NavController,
    notaId: Int?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var completado by remember { mutableStateOf(false) }
    var prioridad by remember { mutableStateOf("Media") }

    val padding = dimensionResource(id = R.dimen.padding_normal)
    val titleSize = dimensionResource(id = R.dimen.text_size_title)
    val bodySize = dimensionResource(id = R.dimen.text_size_body)

    // Precarga si notaId ≠ null
    LaunchedEffect(notaId) {
        if (notaId != null) {
            notaViewModel.obtenerNotaPorId(notaId).collectLatest { nota ->
                nota?.let {
                    titulo = it.titulo
                    descripcion = it.descripcion
                    hora = it.hora
                    completado = it.completado
                    prioridad = "Media" // puedes extender esto si lo guardas
                }
            }
        }
    }

    Column(modifier = Modifier.padding(padding)) {
        Text(
            text = if (notaId == null) stringResource(R.string.nueva_nota)
            else stringResource(R.string.editar_nota),
            fontSize = titleSize.value.sp
        )

        Spacer(modifier = Modifier.height(padding))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(stringResource(R.string.titulo), fontSize = bodySize.value.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(padding))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text(stringResource(R.string.descripcion), fontSize = bodySize.value.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(padding))

        Button(onClick = {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hour, minute -> hora = "%02d:%02d".format(hour, minute) },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }) {
            Text(stringResource(R.string.seleccionar_hora), fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text("${stringResource(R.string.hora)}: $hora", fontSize = bodySize.value.sp)

        Spacer(modifier = Modifier.height(padding))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = completado, onCheckedChange = { completado = it })
            Text(stringResource(R.string.completado), fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Text(stringResource(R.string.prioridad), fontSize = bodySize.value.sp)
        Row {
            RadioButton(selected = prioridad == "Alta", onClick = { prioridad = "Alta" })
            Text(stringResource(R.string.alta), fontSize = bodySize.value.sp)
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = prioridad == "Media", onClick = { prioridad = "Media" })
            Text(stringResource(R.string.media), fontSize = bodySize.value.sp)
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = prioridad == "Baja", onClick = { prioridad = "Baja" })
            Text(stringResource(R.string.baja), fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Button(onClick = {
            if (titulo.isNotBlank() && hora.isNotBlank()) {
                val nota = Nota(
                    id = notaId ?: 0,
                    titulo = titulo,
                    descripcion = descripcion,
                    hora = hora,
                    completado = completado
                )

                scope.launch {
                    if (notaId == null) {
                        notaViewModel.insertar(nota)
                        programarNotificacion(context, nota)
                        Toast.makeText(context, stringResource(R.string.nota_guardada), Toast.LENGTH_SHORT).show()
                    } else {
                        notaViewModel.actualizar(nota)
                        Toast.makeText(context, stringResource(R.string.nota_actualizada), Toast.LENGTH_SHORT).show()
                    }
                    navController.popBackStack()
                }
            } else {
                Toast.makeText(context, stringResource(R.string.campos_obligatorios), Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(stringResource(R.string.guardar), fontSize = bodySize.value.sp)
        }
    }
}

private fun programarNotificacion(context: Context, nota: Nota) {
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("titulo", nota.titulo)
        putExtra("mensaje", nota.descripcion)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        nota.id,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val cal = Calendar.getInstance().apply {
        val parts = nota.hora.split(":")
        set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        set(Calendar.MINUTE, parts[1].toInt())
        set(Calendar.SECOND, 0)
    }
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
}
