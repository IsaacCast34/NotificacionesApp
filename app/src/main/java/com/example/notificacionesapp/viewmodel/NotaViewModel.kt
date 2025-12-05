package com.example.notificacionesapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificacionesapp.data.entities.Nota
import com.example.notificacionesapp.data.repository.NotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotaViewModel @Inject constructor(
    private val notaRepository: NotaRepository
) : ViewModel() {

    val todasLasNotas: Flow<List<Nota>> = notaRepository.obtenerTodas()

    val titulo = mutableStateOf("")
    val descripcion = mutableStateOf("")
    val hora = mutableStateOf("")
    val completado = mutableStateOf(false)
    val prioridad = mutableStateOf("Media")

<<<<<<< HEAD
    suspend fun insertar(nota: Nota): Long {
        return notaRepository.insertar(nota)
=======
    fun insertar(nota: Nota) {
        viewModelScope.launch { notaRepository.insertar(nota) }
>>>>>>> 027f8f25115bc5ecebc6ed55cd5a024dbdd8f879
    }

    fun actualizar(nota: Nota) {
        viewModelScope.launch { notaRepository.actualizar(nota) }
    }

    fun eliminar(nota: Nota) {
        viewModelScope.launch {
            notaRepository.eliminar(nota)
        }
    }

    fun obtenerNotaPorId(id: Int): Flow<Nota?> {
        return notaRepository.obtenerPorId(id)
    }
}