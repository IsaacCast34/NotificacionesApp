package com.example.notificacionesapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notificacionesapp.ui.screens.HomeScreen
import com.example.notificacionesapp.ui.screens.NotaDetalleScreen
import com.example.notificacionesapp.ui.screens.NotaFormScreen
import com.example.notificacionesapp.viewmodel.NotaViewModel

@Composable
fun AppNavigation(navController: NavHostController, notaViewModel: NotaViewModel) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(navController = navController, notaViewModel = notaViewModel)
        }

        composable("notaForm") {
            NotaFormScreen(notaViewModel = notaViewModel, navController = navController, notaId = null)
        }

        composable("detalleNota/{notaId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("notaId")?.toIntOrNull()
            if (id != null) {
                NotaDetalleScreen(notaId = id, navController = navController, notaViewModel = notaViewModel)
            }
        }

        composable("editarNota/{notaId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("notaId")?.toIntOrNull()
            if (id != null) {
                NotaFormScreen(notaViewModel = notaViewModel, navController = navController, notaId = id)
            }
        }
    }
}

