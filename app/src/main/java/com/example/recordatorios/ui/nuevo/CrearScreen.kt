package com.example.recordatorios.ui.nuevo

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recordatorios.Categoria
import com.example.recordatorios.Prioridad
import com.example.recordatorios.ui.componentes.CampoTextoControlado
import com.example.recordatorios.ui.componentes.ChipSeleccion

@Composable
fun PantallaCrearRecordatorio(
    viewModel: CrearViewModel,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.guardadoExitoso) {
        LaunchedEffect(Unit) { onGuardar() }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BarraCrear(onCancelar = onCancelar)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CampoTextoControlado(
                value = uiState.titulo,
                onValueChange = { viewModel.onEvento(CrearEvento.TituloCambiado(it)) },
                label = "Título",
                placeholder = { Text("¿Qué tenés que recordar?") },
                isError = uiState.tituloError != null,
                mensajeError = uiState.tituloError
            )

            CampoTextoControlado(
                value = uiState.descripcion,
                onValueChange = { viewModel.onEvento(CrearEvento.DescripcionCambiada(it)) },
                label = "Descripción (opcional)",
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                isError = uiState.descripcionError != null,
                mensajeError = uiState.descripcionError,
                textoAuxiliar = {
                    Text(
                        text = "${uiState.descripcion.length} de 200 caracteres",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            SelectorOpciones(
                tituloBloque = "Categoría",
                textoAyuda = "Elegí una categoría de las disponibles.",
                ayudaVisible = uiState.categoria == null,
                opciones = {
                    Categoria.entries.forEach { opcion ->
                        ChipSeleccion(
                            texto = opcion.etiqueta,
                            seleccionado = uiState.categoria == opcion,
                            onSeleccion = { viewModel.onEvento(CrearEvento.CategoriaSeleccionada(opcion)) }
                        )
                    }
                }
            )

            SelectorOpciones(
                tituloBloque = "Prioridad",
                textoAyuda = "Elegí una prioridad para poder guardar.",
                ayudaVisible = uiState.prioridad == null,
                opciones = {
                    Prioridad.entries.forEach { opcion ->
                        ChipSeleccion(
                            texto = opcion.etiqueta,
                            seleccionado = uiState.prioridad == opcion,
                            onSeleccion = { viewModel.onEvento(CrearEvento.PrioridadSeleccionada(opcion)) }
                        )
                    }
                }
            )

            if (uiState.errorGeneral != null) {
                AvisoErrorGeneral(
                    mensaje = uiState.errorGeneral.orEmpty(),
                    onReintentar = { viewModel.onEvento(CrearEvento.Guardar) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.onEvento(CrearEvento.Guardar) },
                    enabled = uiState.guardadoDisponible,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Guardar recordatorio")
                }
                OutlinedButton(
                    onClick = onCancelar,
                    enabled = !uiState.enviando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
            }

            if (uiState.enviando) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text(
                        text = "Guardando recordatorio…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraCrear(onCancelar: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = { Text(text = "Nuevo recordatorio", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onCancelar) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cancelar y volver"
                )
            }
        }
    )
}

@Composable
private fun SelectorOpciones(
    tituloBloque: String,
    textoAyuda: String,
    ayudaVisible: Boolean,
    opciones: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = tituloBloque,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones()
        }
        if (ayudaVisible) {
            Text(
                text = textoAyuda,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AvisoErrorGeneral(
    mensaje: String,
    onReintentar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Error al guardar",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(4.dp))
        TextButton(onClick = onReintentar) {
            Text("Reintentar")
        }
    }
}
