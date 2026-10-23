package com.example.recordatorios.data

import com.example.recordatorios.Recordatorio

interface RecordatorioRepository {
    suspend fun obtenerTodos(): List<Recordatorio>
    suspend fun obtenerPorId(id: Int): Recordatorio?
    suspend fun guardar(recordatorio: Recordatorio): Recordatorio
}
