package fr.math.vikapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.math.vikapp.ui.*
import fr.math.vikapp.ui.theme.VIKAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VikViewModel = viewModel()
            
            VIKAPPTheme(darkTheme = viewModel.isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                                label = { Text("Accueil") },
                                selected = currentRoute == "home",
                                onClick = { navController.navigate("home") }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Person, contentDescription = "Compte") },
                                label = { Text("Compte") },
                                selected = currentRoute == "login" || currentRoute == "profile" || currentRoute == "signup",
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
                                onNavigateToRaids = { navController.navigate("raids_list") },
                                onNavigateToDetail = { raidId -> 
                                    navController.navigate("raid_detail/$raidId")
                                }
                            )
                        }
                        composable("raids_list") {
                            RaidListScreen(
                                viewModel = viewModel,
                                onNavigateToDetail = { raidId -> 
                                    navController.navigate("raid_detail/$raidId")
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "raid_detail/{raidId}",
                            arguments = listOf(navArgument("raidId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val raidId = backStackEntry.arguments?.getInt("raidId") ?: 0
                            RaidDetailScreen(
                                raidId = raidId,
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(viewModel = viewModel)
                        }
                        composable("login") {
                            if (viewModel.token != null) {
                                LaunchedEffect(Unit) { navController.navigate("profile") { popUpTo("login") { inclusive = true } } }
                            } else {
                                LoginScreen(
                                    viewModel = viewModel,
                                    onLoginSuccess = { navController.navigate("profile") { popUpTo("login") { inclusive = true } } },
                                    onNavigateToSignup = { navController.navigate("signup") }
                                )
                            }
                        }
                        composable("signup") {
                            if (viewModel.token != null) {
                                LaunchedEffect(Unit) { navController.navigate("profile") { popUpTo("signup") { inclusive = true } } }
                            } else {
                                SignupScreen(
                                    viewModel = viewModel,
                                    onSignupSuccess = { navController.navigate("profile") { popUpTo("signup") { inclusive = true } } },
                                    onNavigateToLogin = { navController.navigate("login") }
                                )
                            }
                        }
                        composable("profile") {
                            if (viewModel.token == null) {
                                LaunchedEffect(Unit) { navController.navigate("login") { popUpTo("profile") { inclusive = true } } }
                            } else {
                                ProfileScreen(
                                    viewModel = viewModel,
                                    onLogout = { navController.navigate("home") { popUpTo("profile") { inclusive = true } } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
