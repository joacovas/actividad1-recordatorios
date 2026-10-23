package com.example.recordatorios.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EscenarioSimulacion(val etiqueta: String, val descripcion: String) {
    NORMAL("Normal", "Carga y guarda los recordatorios sin fallas."),
    VACIO("Sin datos", "La fuente devuelve una lista vacía para ver el estado vacío."),
    ERROR("Error de red", "La carga y el guardado fallan para ver el estado de error.")
}

object SimuladorEstados {
    const val DEMORA_CARGA_MS = 1200L
    const val DEMORA_GUARDADO_MS = 800L
    const val DEMORA_BUSQUEDA_MS = 400L

    private val _escenario = MutableStateFlow(EscenarioSimulacion.NORMAL)
    val escenario: StateFlow<EscenarioSimulacion> = _escenario.asStateFlow()

    fun configurar(nuevoEscenario: EscenarioSimulacion) {
        _escenario.value = nuevoEscenario
    }

    fun valorActual(): EscenarioSimulacion = _escenario.value
}
