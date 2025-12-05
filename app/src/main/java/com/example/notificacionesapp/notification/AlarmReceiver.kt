package com.example.notificacionesapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("titulo") ?: "Nota"
        val mensaje = intent.getStringExtra("mensaje") ?: "Recordatorio"
        NotificationHelper.createNotification(context, titulo, mensaje)
    }
}
