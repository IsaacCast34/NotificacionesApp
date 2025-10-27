package com.example.notificacionesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notificacionesapp.ui.theme.NotificacionesAppTheme
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.notificacionesapp.navigation.AppNavigation
import com.example.notificacionesapp.viewmodel.NotaViewModel

class MainActivity : ComponentActivity() {
    private val notaViewModel: NotaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AppNavigation(navController, notaViewModel)
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NotificacionesAppTheme {
        Greeting("Android")
    }
}