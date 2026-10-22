package com.example.recordatorios

enum class Categoria(val etiqueta: String) {
    ESTUDIO("Estudio"),
    TRABAJO("Trabajo"),
    PERSONAL("Personal")
}

enum class Prioridad(val etiqueta: String, val orden: Int) {
    BAJA("Baja", 0),
    MEDIA("Media", 1),
    ALTA("Alta", 2)
}

sealed class EstadoRecordatorio(val etiqueta: String) {
    data object Pendiente : EstadoRecordatorio("Pendiente")
    data object Completado : EstadoRecordatorio("Completado")
}

data class Recordatorio(
    val id: Int,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: Categoria,
    val prioridad: Prioridad,
    val estado: EstadoRecordatorio = EstadoRecordatorio.Pendiente
) {
    fun esValido(): Boolean = titulo.isNotBlank()
}

val recordatoriosIniciales = listOf(
    Recordatorio(
        id = 1,
        titulo = "Estudiar Kotlin",
        descripcion = "Repasar las clases y las colecciones",
        categoria = Categoria.ESTUDIO,
        prioridad = Prioridad.ALTA
    ),
    Recordatorio(
        id = 2,
        titulo = "Comprar alimentos",
        categoria = Categoria.PERSONAL,
        prioridad = Prioridad.MEDIA
    ),
    Recordatorio(
        id = 3,
        titulo = "Entregar actividad",
        categoria = Categoria.ESTUDIO,
        prioridad = Prioridad.ALTA
    ),
    Recordatorio(
        id = 4,
        titulo = "Revisar correo",
        categoria = Categoria.TRABAJO,
        prioridad = Prioridad.BAJA,
        estado = EstadoRecordatorio.Completado
    )
)

// Categorías sin repetir
val categoriasDisponibles: Set<Categoria> = recordatoriosIniciales
    .map { it.categoria }
    .toSet()

// Recordatorios pendientes
val recordatoriosPendientes: List<Recordatorio> = recordatoriosIniciales
    .filter { it.estado == EstadoRecordatorio.Pendiente }

// Recordatorios de prioridad alta
val recordatoriosUrgentes: List<Recordatorio> = recordatoriosIniciales
    .filter { it.prioridad == Prioridad.ALTA }

// Relacionar cada categoría con su etiqueta
val etiquetasCategorias: Map<Categoria, String> = Categoria.entries
    .associateWith { it.etiqueta }
