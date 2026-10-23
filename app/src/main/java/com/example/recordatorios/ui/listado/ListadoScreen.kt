package com.example.recordatorios.ui.listado

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recordatorios.Terracota
import com.example.recordatorios.data.EscenarioSimulacion
import com.example.recordatorios.ui.componentes.ChipEtiqueta
import com.example.recordatorios.ui.componentes.ContenidoCargando
import com.example.recordatorios.ui.componentes.ContenidoError
import com.example.recordatorios.ui.componentes.ContenidoVacio
import com.example.recordatorios.ui.componentes.RecordatorioItem

@Composable
fun PantallaListado(
    viewModel: ListadoViewModel,
    onCrearRecordatorio: () -> Unit,
    onVerDetalle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dialogoSimulacionAbierto by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraListado(
                escenario = viewModel.escenario.collectAsStateWithLifecycle().value,
                onAbrirSimulacion = { dialogoSimulacionAbierto = true }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearRecordatorio,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo recordatorio") }
            )
        }
    ) { innerPadding ->
        when (val estado = uiState) {
            ListadoUiState.Cargando -> {
                ContenidoCargando(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    mensaje = "Cargando recordatorios…"
                )
            }

            ListadoUiState.Vacio -> {
                ContenidoVacio(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    titulo = "Sin recordatorios",
                    mensaje = "Todavía no hay recordatorios guardados en la agenda.",
                    textoAccion = "Crear recordatorio",
                    onAccion = onCrearRecordatorio
                )
            }

            is ListadoUiState.ConDatos -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        EncabezadoListado(
                            totalPendientes = estado.totalPendientes,
                            total = estado.recordatorios.size
                        )
                    }
                    items(estado.recordatorios, key = { it.id }) { recordatorio ->
                        RecordatorioItem(
                            recordatorio = recordatorio,
                            onSeleccionar = { onVerDetalle(recordatorio.id) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }

            is ListadoUiState.Error -> {
                ContenidoError(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    mensaje = estado.mensaje,
                    onReintentar = { viewModel.onEvento(ListadoEvento.Reintentar) }
                )
            }
        }
    }

    if (dialogoSimulacionAbierto) {
        DialogoSimulacion(
            escenarioActual = viewModel.escenario.collectAsStateWithLifecycle().value,
            onSeleccionar = { nuevoEscenario ->
                viewModel.onEvento(ListadoEvento.CambiarEscenario(nuevoEscenario))
                dialogoSimulacionAbierto = false
            },
            onCerrar = { dialogoSimulacionAbierto = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraListado(
    escenario: EscenarioSimulacion,
    onAbrirSimulacion: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = { Text(text = "Recordatorios", style = MaterialTheme.typography.titleLarge) },
        actions = {
            if (escenario != EscenarioSimulacion.NORMAL) {
                ChipEtiqueta(
                    texto = "Simulación: ${escenario.etiqueta}",
                    color = Terracota
                )
            }
            IconButton(onClick = onAbrirSimulacion) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configurar modo de simulación"
                )
            }
        }
    )
}

@Composable
private fun EncabezadoListado(totalPendientes: Int, total: Int) {
    Column(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
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
        Spacer(Modifier.height(6.dp))
        Text(
            text = "$totalPendientes pendientes de $total tareas guardadas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DialogoSimulacion(
    escenarioActual: EscenarioSimulacion,
    onSeleccionar: (EscenarioSimulacion) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        shape = RoundedCornerShape(28.dp),
        title = { Text(text = "Modo de simulación", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Cambia cómo responde la fuente de datos para poder verificar cada estado de la pantalla.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                EscenarioSimulacion.entries.forEach { escenario ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = escenario == escenarioActual,
                            onClick = { onSeleccionar(escenario) }
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = escenario.etiqueta,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = escenario.descripcion,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}
