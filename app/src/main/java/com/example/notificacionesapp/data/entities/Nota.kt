package com.example.notificacionesapp.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val hora: String,
    val completado: Boolean,
    val imagenUri: String? = null,
    val audioUri: String? = null
)
