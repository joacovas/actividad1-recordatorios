package com.example.recordatorios.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.Recordatorio
import com.example.recordatorios.coloresCategoria
import com.example.recordatorios.coloresPrioridad
import com.example.recordatorios.ui.componentes.ChipEtiqueta
import com.example.recordatorios.ui.componentes.ContenidoCargando
import com.example.recordatorios.ui.componentes.ContenidoError
import com.example.recordatorios.ui.componentes.ContenidoVacio

@Composable
fun PantallaDetalleRecordatorio(
    recordatorioId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleViewModel = viewModel(
        key = "detalle-$recordatorioId",
        factory = DetalleViewModel.factory(recordatorioId)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraDetalle(onVolver = onVolver)
        }
    ) { innerPadding ->
        when (val estado = uiState) {
            DetalleUiState.Cargando -> {
                ContenidoCargando(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    mensaje = "Buscando el recordatorio…"
                )
            }

            is DetalleUiState.Contenido -> {
                ContenidoDetalle(
                    recordatorio = estado.recordatorio,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            DetalleUiState.NoEncontrado -> {
                ContenidoVacio(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    titulo = "No se encontró",
                    mensaje = "No existe un recordatorio con el identificador $recordatorioId.",
                    textoAccion = "Volver",
                    onAccion = onVolver
                )
            }

            is DetalleUiState.Error -> {
                ContenidoError(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    mensaje = estado.mensaje,
                    onReintentar = { viewModel.cargar() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraDetalle(onVolver: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = { Text(text = "Detalle del recordatorio", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onVolver) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
    )
}

@Composable
private fun ContenidoDetalle(
    recordatorio: Recordatorio,
    modifier: Modifier = Modifier
) {
    val colorCategoria = coloresCategoria.getValue(recordatorio.categoria)
    val colorPrioridad = coloresPrioridad.getValue(recordatorio.prioridad)
    val colorEstado = if (recordatorio.estado.etiqueta == "Completado") {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val forma = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = recordatorio.titulo,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ChipEtiqueta(texto = recordatorio.categoria.etiqueta, color = colorCategoria)
            ChipEtiqueta(texto = recordatorio.prioridad.etiqueta, color = colorPrioridad)
            ChipEtiqueta(texto = recordatorio.estado.etiqueta, color = colorEstado)
        }

        Card(
            shape = forma,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "DESCRIPCIÓN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recordatorio.descripcion ?: "Sin descripción.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                FilaMetadatos(etiqueta = "Identificador", valor = "#${recordatorio.id}")
                FilaMetadatos(etiqueta = "Categoría", valor = recordatorio.categoria.etiqueta)
                FilaMetadatos(etiqueta = "Prioridad", valor = recordatorio.prioridad.etiqueta)
                FilaMetadatos(etiqueta = "Estado", valor = recordatorio.estado.etiqueta)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FilaMetadatos(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
