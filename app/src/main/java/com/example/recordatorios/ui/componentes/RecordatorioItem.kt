package com.example.recordatorios.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.recordatorios.Recordatorio
import com.example.recordatorios.coloresCategoria
import com.example.recordatorios.coloresPrioridad

@Composable
fun RecordatorioItem(
    recordatorio: Recordatorio,
    onSeleccionar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorCategoria = coloresCategoria.getValue(recordatorio.categoria)
    val colorPrioridad = coloresPrioridad.getValue(recordatorio.prioridad)
    val colorEstado = if (recordatorio.estado.etiqueta == "Completado") {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val colorAcento = if (recordatorio.estado.etiqueta == "Completado") {
        MaterialTheme.colorScheme.outlineVariant
    } else {
        colorPrioridad
    }
    val forma = RoundedCornerShape(20.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(forma)
            .clickable(onClick = onSeleccionar),
        shape = forma,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Column(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(colorAcento)
            ) {}

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = recordatorio.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                recordatorio.descripcion?.let { descripcion ->
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChipEtiqueta(
                        texto = recordatorio.categoria.etiqueta,
                        color = colorCategoria
                    )
                    ChipEtiqueta(
                        texto = recordatorio.prioridad.etiqueta,
                        color = colorPrioridad
                    )
                    ChipEtiqueta(
                        texto = recordatorio.estado.etiqueta,
                        color = colorEstado
                    )
                }
            }
        }
    }
}
