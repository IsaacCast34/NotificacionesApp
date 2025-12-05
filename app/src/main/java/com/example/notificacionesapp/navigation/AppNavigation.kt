package com.example.notificacionesapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notificacionesapp.ui.screens.HomeScreen
import com.example.notificacionesapp.ui.screens.NotaDetalleScreen
import com.example.notificacionesapp.ui.screens.NotaFormScreen
import com.example.notificacionesapp.ui.screens.BackupScreen
import com.example.notificacionesapp.ui.screens.ConfiguracionScreen
import com.example.notificacionesapp.viewmodel.NotaViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            val notaViewModel: NotaViewModel = hiltViewModel()
            HomeScreen(navController = navController, notaViewModel = notaViewModel)
        }

        composable("notaForm") {
            val notaViewModel: NotaViewModel = hiltViewModel()
            NotaFormScreen(notaViewModel = notaViewModel, navController = navController, notaId = null)
        }

        composable("detalleNota/{notaId}") { backStackEntry ->
            val notaViewModel: NotaViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("notaId")?.toIntOrNull()
            if (id != null) {
                NotaDetalleScreen(notaId = id, navController = navController, notaViewModel = notaViewModel)
            }
        }

        composable("editarNota/{notaId}") { backStackEntry ->
            val notaViewModel: NotaViewModel = hiltViewModel()
            val id = backStackEntry.arguments?.getString("notaId")?.toIntOrNull()
            if (id != null) {
                NotaFormScreen(notaViewModel = notaViewModel, navController = navController, notaId = id)
            }
        }
        composable("backup") {
            val notaViewModel: NotaViewModel = hiltViewModel()
            BackupScreen(navController = navController, notaViewModel = notaViewModel)
        }
        composable("configuracion") {
            ConfiguracionScreen(navController = navController)
        }
    }
}

