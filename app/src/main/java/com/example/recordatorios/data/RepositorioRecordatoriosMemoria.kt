package com.example.recordatorios.data

import com.example.recordatorios.Recordatorio
import com.example.recordatorios.recordatoriosIniciales
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

object RepositorioRecordatoriosMemoria : RecordatorioRepository {

    private val mutex = Mutex()
    private val almacenamiento = mutableListOf<Recordatorio>()
    private var siguienteId = 1

    init {
        almacenamiento.addAll(recordatoriosIniciales)
        siguienteId = (recordatoriosIniciales.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun obtenerTodos(): List<Recordatorio> {
        delay(SimuladorEstados.DEMORA_CARGA_MS)
        if (SimuladorEstados.valorActual() == EscenarioSimulacion.ERROR) {
            throw IOException("No se pudieron cargar los recordatorios. Falla simulada de red.")
        }
        return mutex.withLock {
            if (SimuladorEstados.valorActual() == EscenarioSimulacion.VACIO) {
                emptyList()
            } else {
                almacenamiento.toList()
            }
        }
    }

    override suspend fun obtenerPorId(id: Int): Recordatorio? {
        delay(SimuladorEstados.DEMORA_BUSQUEDA_MS)
        return mutex.withLock { almacenamiento.firstOrNull { it.id == id } }
    }

    override suspend fun guardar(recordatorio: Recordatorio): Recordatorio {
        delay(SimuladorEstados.DEMORA_GUARDADO_MS)
        if (SimuladorEstados.valorActual() == EscenarioSimulacion.ERROR) {
            throw IOException("No se pudo guardar el recordatorio. Falla simulada de red.")
        }
        return mutex.withLock {
            val almacenado = recordatorio.copy(id = siguienteId)
            siguienteId += 1
            almacenamiento.add(almacenado)
            almacenado
        }
    }
}
