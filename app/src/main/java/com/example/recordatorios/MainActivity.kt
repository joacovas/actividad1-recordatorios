package com.example.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            TemaRecordatorios {
                PantallaRecordatorios()
            }
        }
    }
}

@Composable
fun PantallaRecordatorios() {
    val recordatorios = remember {
        mutableStateListOf<Recordatorio>().apply { addAll(recordatoriosIniciales) }
    }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var soloPendientes by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    val visibles = recordatorios
        .filter { categoriaSeleccionada == null || it.categoria == categoriaSeleccionada }
        .filter { !soloPendientes || it.estado == EstadoRecordatorio.Pendiente }
        .sortedWith(
            compareByDescending<Recordatorio> { it.estado == EstadoRecordatorio.Pendiente }
                .thenByDescending { it.prioridad.orden }
        )

    val totalPendientes = recordatorios.count { it.estado == EstadoRecordatorio.Pendiente }
    val totalUrgentes = recordatorios.count {
        it.estado == EstadoRecordatorio.Pendiente && it.prioridad == Prioridad.ALTA
    }
    val fecha = remember {
        SimpleDateFormat("EEEE d 'de' MMMM", Locale.forLanguageTag("es-AR"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    val hayFiltros = categoriaSeleccionada != null || soloPendientes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo recordatorio") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Encabezado(
                    totalPendientes = totalPendientes,
                    total = recordatorios.size,
                    fecha = fecha
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaResumen(
                        numero = totalPendientes,
                        etiqueta = "Pendientes",
                        icono = Icons.Filled.Notifications,
                        fondo = MaterialTheme.colorScheme.primaryContainer,
                        contenido = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TarjetaResumen(
                        numero = totalUrgentes,
                        etiqueta = "Prioridad alta",
                        icono = Icons.Filled.Warning,
                        fondo = MaterialTheme.colorScheme.secondaryContainer,
                        contenido = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Filtros(
                    categoriaSeleccionada = categoriaSeleccionada,
                    soloPendientes = soloPendientes,
                    onCategoria = { categoriaSeleccionada = it },
                    onSoloPendientes = { soloPendientes = it }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TUS RECORDATORIOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = visibles.size.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (visibles.isEmpty()) {
                item {
                    EstadoVacio(
                        hayFiltros = hayFiltros,
                        onLimpiar = {
                            categoriaSeleccionada = null
                            soloPendientes = false
                        }
                    )
                }
            }

            items(visibles, key = { it.id }) { recordatorio ->
                RecordatorioCard(
                    recordatorio = recordatorio,
                    onAlternarEstado = {
                        val indice = recordatorios.indexOfFirst { it.id == recordatorio.id }
                        if (indice >= 0) {
                            val actual = recordatorios[indice]
                            recordatorios[indice] = actual.copy(
                                estado = if (actual.estado == EstadoRecordatorio.Pendiente) {
                                    EstadoRecordatorio.Completado
                                } else {
                                    EstadoRecordatorio.Pendiente
                                }
                            )
                        }
                    },
                    onEliminar = { recordatorios.removeAll { it.id == recordatorio.id } },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNuevoRecordatorio(
            onCerrar = { mostrarDialogo = false },
            onGuardar = { nuevo ->
                val siguienteId = (recordatorios.maxOfOrNull { it.id } ?: 0) + 1
                recordatorios.add(0, nuevo.copy(id = siguienteId))
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun Encabezado(totalPendientes: Int, total: Int, fecha: String) {
    Column(Modifier.padding(top = 16.dp, bottom = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(6.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "AGENDA PERSONAL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Recordatorios",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = fecha,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "$totalPendientes pendientes de $total tareas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TarjetaResumen(
    numero: Int,
    etiqueta: String,
    icono: ImageVector,
    fondo: Color,
    contenido: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = contenido,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = contenido.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = numero.toString().padStart(2, '0'),
                style = MaterialTheme.typography.headlineMedium,
                color = contenido
            )
        }
    }
}

@Composable
private fun Filtros(
    categoriaSeleccionada: Categoria?,
    soloPendientes: Boolean,
    onCategoria: (Categoria?) -> Unit,
    onSoloPendientes: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChipFiltro(
                texto = "Todas",
                seleccionado = categoriaSeleccionada == null,
                onClick = { onCategoria(null) }
            )
            categoriasDisponibles.sortedBy { it.ordinal }.forEach { categoria ->
                ChipFiltro(
                    texto = etiquetasCategorias[categoria] ?: categoria.etiqueta,
                    seleccionado = categoriaSeleccionada == categoria,
                    onClick = {
                        onCategoria(if (categoriaSeleccionada == categoria) null else categoria)
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mostrar solo pendientes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            Switch(
                checked = soloPendientes,
                onCheckedChange = onSoloPendientes
            )
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto, style = MaterialTheme.typography.labelLarge) },
        shape = RoundedCornerShape(50),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun RecordatorioCard(
    recordatorio: Recordatorio,
    onAlternarEstado: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completado = recordatorio.estado == EstadoRecordatorio.Completado
    val colorCategoria = coloresCategoria.getValue(recordatorio.categoria)
    val colorPrioridad = coloresPrioridad.getValue(recordatorio.prioridad)
    val colorEstado = if (completado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val colorAcento = if (completado) MaterialTheme.colorScheme.outlineVariant else colorPrioridad
    val forma = RoundedCornerShape(20.dp)
    val colorCirculo by animateColorAsState(
        targetValue = if (completado) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "colorCirculo"
    )
    val bordeCirculo by animateColorAsState(
        targetValue = if (completado) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outline
        },
        label = "bordeCirculo"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma),
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(colorAcento)
            )
            Row(
                modifier = Modifier.padding(start = 12.dp, end = 6.dp, top = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(colorCirculo)
                        .border(2.dp, bordeCirculo, CircleShape)
                        .clickable(onClick = onAlternarEstado),
                    contentAlignment = Alignment.Center
                ) {
                    if (completado) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Marcar como pendiente",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = recordatorio.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (completado) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (completado) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    recordatorio.descripcion?.let { descripcion ->
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(
                            modifier = Modifier
                                .background(colorCategoria.copy(alpha = 0.12f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(5.dp)
                                    .background(colorCategoria, CircleShape)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = recordatorio.categoria.etiqueta,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorCategoria,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Box(
                            Modifier
                                .size(5.dp)
                                .background(colorPrioridad, CircleShape)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = recordatorio.prioridad.etiqueta,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorPrioridad,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(
                            Modifier
                                .size(5.dp)
                                .background(colorEstado, CircleShape)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = recordatorio.estado.etiqueta,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorEstado,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar recordatorio",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(hayFiltros: Boolean, onLimpiar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (hayFiltros) "Nada con este filtro" else "Todo al día",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (hayFiltros) {
                "Probá con otra categoría o quitá los filtros."
            } else {
                "Agregá tu primer recordatorio con el botón de abajo."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (hayFiltros) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onLimpiar) {
                Text("Quitar filtros")
            }
        }
    }
}

@Composable
private fun DialogoNuevoRecordatorio(
    onCerrar: () -> Unit,
    onGuardar: (Recordatorio) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(Categoria.ESTUDIO) }
    var prioridad by remember { mutableStateOf(Prioridad.MEDIA) }
    var tituloTocado by remember { mutableStateOf(false) }

    val borrador = Recordatorio(
        id = 0,
        titulo = titulo.trim(),
        descripcion = descripcion.trim().ifBlank { null },
        categoria = categoria,
        prioridad = prioridad
    )
    val valido = borrador.esValido()

    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Nuevo recordatorio",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                        tituloTocado = true
                    },
                    label = { Text("Título") },
                    placeholder = { Text("Ej. Entregar la actividad") },
                    singleLine = true,
                    isError = tituloTocado && !valido,
                    supportingText = {
                        if (tituloTocado && !valido) {
                            Text("El título es obligatorio")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción (opcional)") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Categoria.entries.forEach { opcion ->
                        FilterChip(
                            selected = categoria == opcion,
                            onClick = { categoria = opcion },
                            label = { Text(opcion.etiqueta) },
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
                Text(
                    text = "Prioridad",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Prioridad.entries.forEach { opcion ->
                        FilterChip(
                            selected = prioridad == opcion,
                            onClick = { prioridad = opcion },
                            label = { Text(opcion.etiqueta) },
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(borrador) },
                enabled = valido
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCerrar) {
                Text("Cancelar")
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF6F2EA, heightDp = 900)
@Composable
private fun VistaPreviaPantalla() {
    TemaRecordatorios {
        PantallaRecordatorios()
    }
}
