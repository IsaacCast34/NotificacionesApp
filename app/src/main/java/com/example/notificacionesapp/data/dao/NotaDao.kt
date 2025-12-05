package com.example.notificacionesapp.data.dao

import androidx.room.*
import com.example.notificacionesapp.data.entities.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("SELECT * FROM notas ORDER BY hora ASC")
    fun obtenerTodas(): Flow<List<Nota>>

    @Query("SELECT * FROM notas WHERE id = :id")
    fun obtenerPorId(id: Int): Flow<Nota?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
<<<<<<< HEAD
    suspend fun insertar(nota: Nota): Long
=======
    suspend fun insertar(nota: Nota)
>>>>>>> 027f8f25115bc5ecebc6ed55cd5a024dbdd8f879

    @Update
    suspend fun actualizar(nota: Nota)

    @Delete
    suspend fun eliminar(nota: Nota)
}
