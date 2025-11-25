package com.example.notificacionesapp.ui.screens

import android.app.AlarmManager
import android.Manifest
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notificacionesapp.R
import com.example.notificacionesapp.data.entities.Nota
import com.example.notificacionesapp.notification.AlarmReceiver
import com.example.notificacionesapp.viewmodel.NotaViewModel
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

    var imagenUri by rememberSaveable { mutableStateOf<String?>(null) }
    var audioUri by rememberSaveable { mutableStateOf<String?>(null) }

    val titulo by notaViewModel.titulo
    val descripcion by notaViewModel.descripcion
    val hora by notaViewModel.hora
    val completado by notaViewModel.completado
    val prioridad by notaViewModel.prioridad

    val padding = dimensionResource(id = R.dimen.padding_normal)
    val titleSize = dimensionResource(id = R.dimen.text_size_title)
    val bodySize = dimensionResource(id = R.dimen.text_size_body)

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imagenUri = uri?.toString() }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> audioUri = uri?.toString() }

    LaunchedEffect(notaId) {
        if (notaId != null) {
            notaViewModel.obtenerNotaPorId(notaId).collect { nota ->
                nota?.let {
                    notaViewModel.titulo.value = it.titulo
                    notaViewModel.descripcion.value = it.descripcion
                    notaViewModel.hora.value = it.hora
                    notaViewModel.completado.value = it.completado
                    notaViewModel.prioridad.value = it.prioridad
                    imagenUri = it.imagenUri
                    audioUri = it.audioUri
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
            onValueChange = { notaViewModel.titulo.value = it },
            label = { Text(stringResource(R.string.titulo), fontSize = bodySize.value.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(padding))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { notaViewModel.descripcion.value = it },
            label = { Text(stringResource(R.string.descripcion), fontSize = bodySize.value.sp) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(padding))

        Button(onClick = {
            val cal = Calendar.getInstance()
            TimePickerDialog(
                context,
                { _, hour, minute -> notaViewModel.hora.value = "%02d:%02d".format(hour, minute) },
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
            Checkbox(checked = completado, onCheckedChange = { notaViewModel.completado.value = it })
            Text(stringResource(R.string.completado), fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Text(stringResource(R.string.prioridad), fontSize = bodySize.value.sp)
        Row {
            RadioButton(selected = prioridad == "Alta", onClick = { notaViewModel.prioridad.value = "Alta" })
            Text(stringResource(R.string.alta), fontSize = bodySize.value.sp)
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = prioridad == "Media", onClick = { notaViewModel.prioridad.value = "Media" })
            Text(stringResource(R.string.media), fontSize = bodySize.value.sp)
            Spacer(modifier = Modifier.width(8.dp))
            RadioButton(selected = prioridad == "Baja", onClick = { notaViewModel.prioridad.value = "Baja" })
            Text(stringResource(R.string.baja), fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Text(stringResource(R.string.multimedia), fontSize = bodySize.value.sp)

        Button(onClick = { imagePicker.launch("image/*") }) {
            Text(stringResource(R.string.seleccionar_imagen), fontSize = bodySize.value.sp)
        }

        imagenUri?.let {
            Text("${stringResource(R.string.imagen_adjunta)}: $it", fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Button(onClick = { audioPicker.launch("audio/*") }) {
            Text(stringResource(R.string.seleccionar_audio), fontSize = bodySize.value.sp)
        }

        audioUri?.let {
            Text("${stringResource(R.string.audio_adjunta)}: $it", fontSize = bodySize.value.sp)
        }

        Spacer(modifier = Modifier.height(padding))

        Button(onClick = {
            if (titulo.isNotBlank() && hora.isNotBlank()) {
                val nota = Nota(
                    id = notaId ?: 0,
                    titulo = titulo,
                    descripcion = descripcion,
                    hora = hora,
                    completado = completado,
                    prioridad = prioridad,
                    imagenUri = imagenUri,
                    audioUri = audioUri
                )

                scope.launch {
                    if (notaId == null) {
                        notaViewModel.insertar(nota)
                        programarNotificacion(context, nota)
                        Toast.makeText(context, context.getString(R.string.nota_guardada), Toast.LENGTH_SHORT).show()
                    } else {
                        notaViewModel.actualizar(nota)
                        Toast.makeText(context, context.getString(R.string.nota_actualizada), Toast.LENGTH_SHORT).show()
                    }
                    navController.popBackStack()
                }
            } else {
                Toast.makeText(context, context.getString(R.string.campos_obligatorios), Toast.LENGTH_SHORT).show()
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