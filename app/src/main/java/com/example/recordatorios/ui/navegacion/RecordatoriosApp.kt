package com.example.recordatorios.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios.ui.detalle.PantallaDetalleRecordatorio
import com.example.recordatorios.ui.listado.ListadoViewModel
import com.example.recordatorios.ui.listado.PantallaListado
import com.example.recordatorios.ui.nuevo.CrearViewModel
import com.example.recordatorios.ui.nuevo.PantallaCrearRecordatorio

object RutasApp {
    const val LISTADO = "listado"
    const val NUEVO = "nuevo"
    const val DETALLE = "detalle/{id}"

    fun detalle(recordatorioId: Int): String = "detalle/$recordatorioId"
}

@Composable
fun RecordatoriosApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasApp.LISTADO,
        modifier = modifier
    ) {
        composable(route = RutasApp.LISTADO) { entry ->
            val listadoViewModel: ListadoViewModel = viewModel { ListadoViewModel() }
            LaunchedEffect(entry) {
                entry?.savedStateHandle?.getStateFlow("guardado_reciente", false)?.collect { guardado ->
                    if (guardado) {
                        listadoViewModel.cargar()
                        entry.savedStateHandle["guardado_reciente"] = false
                    }
                }
            }
            PantallaListado(
                viewModel = listadoViewModel,
                onCrearRecordatorio = { navController.navigate(RutasApp.NUEVO) },
                onVerDetalle = { recordatorioId ->
                    navController.navigate(RutasApp.detalle(recordatorioId))
                }
            )
        }

        composable(route = RutasApp.NUEVO) {
            PantallaCrearRecordatorio(
                viewModel = viewModel { CrearViewModel() },
                onGuardar = {
                    navController.previousBackStackEntry?.savedStateHandle?.set("guardado_reciente", true)
                    navController.popBackStack()
                },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(
            route = RutasApp.DETALLE,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val recordatorioId = entry.arguments?.getInt("id") ?: -1
            PantallaDetalleRecordatorio(
                recordatorioId = recordatorioId,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
