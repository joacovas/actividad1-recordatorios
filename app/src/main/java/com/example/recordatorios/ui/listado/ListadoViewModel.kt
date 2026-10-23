package com.example.recordatorios.ui.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.EstadoRecordatorio
import com.example.recordatorios.Recordatorio
import com.example.recordatorios.data.EscenarioSimulacion
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.data.RepositorioRecordatoriosMemoria
import com.example.recordatorios.data.SimuladorEstados
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ListadoUiState {
    data object Cargando : ListadoUiState
    data object Vacio : ListadoUiState
    data class ConDatos(
        val recordatorios: List<Recordatorio>,
        val totalPendientes: Int
    ) : ListadoUiState

    data class Error(val mensaje: String) : ListadoUiState
}

sealed interface ListadoEvento {
    data object Reintentar : ListadoEvento
    data class CambiarEscenario(val escenario: EscenarioSimulacion) : ListadoEvento
}

class ListadoViewModel(
    private val repositorio: RecordatorioRepository = RepositorioRecordatoriosMemoria,
    private val simulador: SimuladorEstados = SimuladorEstados
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListadoUiState>(ListadoUiState.Cargando)
    val uiState: StateFlow<ListadoUiState> = _uiState.asStateFlow()

    val escenario: StateFlow<EscenarioSimulacion> get() = simulador.escenario

    init {
        cargar()
    }

    fun cargar() {
        _uiState.value = ListadoUiState.Cargando
        viewModelScope.launch {
            try {
                val lista = ordenar(repositorio.obtenerTodos())
                _uiState.update {
                    if (lista.isEmpty()) {
                        ListadoUiState.Vacio
                    } else {
                        ListadoUiState.ConDatos(
                            recordatorios = lista,
                            totalPendientes = lista.count { it.estado == EstadoRecordatorio.Pendiente }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    ListadoUiState.Error(
                        mensaje = e.message ?: "No se pudieron cargar los recordatorios."
                    )
                }
            }
        }
    }

    fun onEvento(evento: ListadoEvento) {
        when (evento) {
            is ListadoEvento.Reintentar -> cargar()
            is ListadoEvento.CambiarEscenario -> {
                simulador.configurar(evento.escenario)
                cargar()
            }
        }
    }

    private fun ordenar(lista: List<Recordatorio>): List<Recordatorio> =
        lista.sortedWith(
            compareBy<Recordatorio> { it.estado == EstadoRecordatorio.Completado }
                .thenByDescending { it.prioridad.orden }
                .thenBy { it.titulo.lowercase() }
        )
}
