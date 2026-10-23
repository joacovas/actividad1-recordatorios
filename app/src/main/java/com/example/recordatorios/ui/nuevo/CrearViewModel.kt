package com.example.recordatorios.ui.nuevo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.Categoria
import com.example.recordatorios.Prioridad
import com.example.recordatorios.Recordatorio
import com.example.recordatorios.data.RecordatorioRepository
import com.example.recordatorios.data.RepositorioRecordatoriosMemoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CrearUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: Categoria? = null,
    val prioridad: Prioridad? = null,
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val enviando: Boolean = false,
    val errorGeneral: String? = null,
    val guardadoExitoso: Boolean = false
) {
    val formularioValido: Boolean
        get() = titulo.trim().length >= 4 &&
            descripcion.length <= 200 &&
            categoria != null &&
            prioridad != null

    val guardadoDisponible: Boolean
        get() = formularioValido && !enviando
}

sealed interface CrearEvento {
    data class TituloCambiado(val texto: String) : CrearEvento
    data class DescripcionCambiada(val texto: String) : CrearEvento
    data class CategoriaSeleccionada(val categoria: Categoria) : CrearEvento
    data class PrioridadSeleccionada(val prioridad: Prioridad) : CrearEvento
    data object Guardar : CrearEvento
}

class CrearViewModel(
    private val repositorio: RecordatorioRepository = RepositorioRecordatoriosMemoria
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearUiState())
    val uiState: StateFlow<CrearUiState> = _uiState.asStateFlow()

    fun onEvento(evento: CrearEvento) {
        when (evento) {
            is CrearEvento.TituloCambiado -> _uiState.update {
                it.copy(titulo = evento.texto, tituloError = validarTitulo(evento.texto))
            }

            is CrearEvento.DescripcionCambiada -> _uiState.update {
                it.copy(descripcion = evento.texto, descripcionError = validarDescripcion(evento.texto))
            }

            is CrearEvento.CategoriaSeleccionada -> _uiState.update {
                it.copy(categoria = evento.categoria)
            }

            is CrearEvento.PrioridadSeleccionada -> _uiState.update {
                it.copy(prioridad = evento.prioridad)
            }

            CrearEvento.Guardar -> guardar()
        }
    }

    private fun guardar() {
        val estadoActual = _uiState.value
        if (!estadoActual.formularioValido || estadoActual.enviando) return

        _uiState.update { it.copy(enviando = true, errorGeneral = null) }

        viewModelScope.launch {
            val borrador = Recordatorio(
                id = 0,
                titulo = estadoActual.titulo.trim(),
                descripcion = estadoActual.descripcion.trim().ifBlank { null },
                categoria = estadoActual.categoria!!,
                prioridad = estadoActual.prioridad!!
            )
            try {
                repositorio.guardar(borrador)
                _uiState.update { it.copy(enviando = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        enviando = false,
                        errorGeneral = e.message?.takeIf { mensaje -> mensaje.isNotBlank() }
                            ?: "No se pudo guardar el recordatorio. Probá de nuevo."
                    )
                }
            }
        }
    }

    private fun validarTitulo(valor: String): String? = when {
        valor.isBlank() -> "El título es obligatorio."
        valor.trim().length < 4 -> "El título necesita al menos 4 caracteres, sin contar espacios de más."
        else -> null
    }

    private fun validarDescripcion(valor: String): String? = when {
        valor.length > 200 -> "La descripción no puede superar los 200 caracteres."
        else -> null
    }
}
