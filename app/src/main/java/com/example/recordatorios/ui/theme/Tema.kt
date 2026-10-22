package com.example.recordatorios

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Papel = Color(0xFFF6F2EA)
val Tinta = Color(0xFF22201B)
val VerdeBosque = Color(0xFF2C5F45)
val Terracota = Color(0xFFA9552F)

private val EsquemaClaro = lightColorScheme(
    primary = VerdeBosque,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE9DF),
    onPrimaryContainer = Color(0xFF0F3524),
    secondary = Terracota,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF6E2D6),
    onSecondaryContainer = Color(0xFF3F1B0B),
    background = Papel,
    onBackground = Tinta,
    surface = Color(0xFFFFFDF9),
    onSurface = Tinta,
    surfaceVariant = Color(0xFFEDE6D9),
    onSurfaceVariant = Color(0xFF5F594E),
    outline = Color(0xFFA8A094),
    outlineVariant = Color(0xFFE5DDCF),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val Tipografia = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 42.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp,
        lineHeight = 27.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)

@Composable
fun TemaRecordatorios(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Tipografia,
        content = content
    )
}

// Map que asocia cada categoría con su color
val coloresCategoria: Map<Categoria, Color> = mapOf(
    Categoria.ESTUDIO to Color(0xFF3C6E9F),
    Categoria.TRABAJO to Color(0xFF9A6A1E),
    Categoria.PERSONAL to Color(0xFF3F7D57)
)

// Map que asocia cada prioridad con su color
val coloresPrioridad: Map<Prioridad, Color> = mapOf(
    Prioridad.BAJA to Color(0xFF4C7A54),
    Prioridad.MEDIA to Color(0xFFC07A18),
    Prioridad.ALTA to Color(0xFFB3402E)
)
