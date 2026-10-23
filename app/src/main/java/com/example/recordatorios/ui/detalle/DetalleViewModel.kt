package com.example.recordatorios.ui.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.recordatorios.Recordatorio
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.data.RepositorioRecordatoriosMemoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Contenido(val recordatorio: Recordatorio) : DetalleUiState
    data object NoEncontrado : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

class DetalleViewModel(
    private val recordatorioId: Int,
    private val repositorio: RecordatorioRepository = RepositorioRecordatoriosMemoria
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _uiState.value = DetalleUiState.Cargando
        viewModelScope.launch {
            try {
                val encontrado = repositorio.obtenerPorId(recordatorioId)
                _uiState.update {
                    if (encontrado == null) {
                        DetalleUiState.NoEncontrado
                    } else {
                        DetalleUiState.Contenido(recordatorio = encontrado)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    DetalleUiState.Error(
                        mensaje = e.message ?: "No se pudo cargar el detalle del recordatorio."
                    )
                }
            }
        }
    }

    companion object {
        fun factory(recordatorioId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetalleViewModel(recordatorioId = recordatorioId) }
        }
    }
}
