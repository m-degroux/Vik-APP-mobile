package fr.math.vikapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.math.vikapp.ui.*
import fr.math.vikapp.ui.theme.VIKAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VIKAPPTheme {
                val viewModel: VikViewModel = viewModel()
                val navController = rememberNavController()
                
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Text("Accueil") },
                                selected = false,
                                onClick = { navController.navigate("home") }
                            )
                            NavigationBarItem(
                                icon = { Text("Compte") },
                                selected = false,
                                onClick = { 
                                    if (viewModel.token == null) navController.navigate("login")
                                    else navController.navigate("profile")
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { navController.navigate("login") },
                                onNavigateToRaids = { /* Navigate to raids list */ }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                viewModel = viewModel,
                                onLoginSuccess = { navController.navigate("home") },
                                onNavigateToSignup = { navController.navigate("signup") }
                            )
                        }
                        composable("signup") {
                            SignupScreen(
                                viewModel = viewModel,
                                onSignupSuccess = { navController.navigate("home") },
                                onNavigateToLogin = { navController.navigate("login") }
                            )
                        }
                    }
                }
            }
        }
    }
}
