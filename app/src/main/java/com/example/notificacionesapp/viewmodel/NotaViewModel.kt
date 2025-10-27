package com.example.notificacionesapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notificacionesapp.data.entities.Nota
import com.example.notificacionesapp.data.repository.NotaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotaViewModel(private val notaRepository: NotaRepository) : ViewModel() {

    val todasLasNotas: Flow<List<Nota>> = notaRepository.obtenerTodas()

    fun insertar(nota: Nota) {
        viewModelScope.launch { notaRepository.insertar(nota) }
    }

    fun actualizar(nota: Nota) {
        viewModelScope.launch { notaRepository.actualizar(nota) }
    }

    fun eliminar(nota: Nota) {
        viewModelScope.launch { notaRepository.eliminar(nota) }
    }

    fun obtenerNotaPorId(id: Int): Flow<Nota?> {
        return notaRepository.obtenerPorId(id)
    }
}
