package com.example.notificacionesapp.data.repository

import com.example.notificacionesapp.data.dao.NotaDao
import com.example.notificacionesapp.data.entities.Nota
import kotlinx.coroutines.flow.Flow

class NotaRepository(private val notaDao: NotaDao) {

    fun obtenerTodas(): Flow<List<Nota>> = notaDao.obtenerTodas()

    fun obtenerPorId(id: Int): Flow<Nota?> = notaDao.obtenerPorId(id)

    suspend fun insertar(nota: Nota): Long = notaDao.insertar(nota)

    suspend fun actualizar(nota: Nota) = notaDao.actualizar(nota)

    suspend fun eliminar(nota: Nota) = notaDao.eliminar(nota)
}
