package com.example.aksarajawa

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object EasyGame : Screen("easy_game")
    object MediumGame : Screen("medium_game")
    object HardGame : Screen("hard_game")
    object Result : Screen("result/{difficulty}") {
        fun createRoute(difficulty: String) = "result/$difficulty"
    }
    object Profile : Screen("profile")
    object Leaderboard : Screen("leaderboard")
    object About : Screen("about")
    object Dictionary : Screen("dictionary")
    object DictionaryDetail : Screen("dictionary_detail/{aksaraName}") {
        fun createRoute(aksaraName: String) = "dictionary_detail/$aksaraName"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GameViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, viewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController, viewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, viewModel)
        }
        composable(Screen.EasyGame.route) {
            GameScreen(navController, viewModel, "Mudah")
        }
        composable(Screen.MediumGame.route) {
            GameScreen(navController, viewModel, "Sedang")
        }
        composable(Screen.HardGame.route) {
            HardGameScreen(navController, viewModel)
        }
        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("difficulty") { type = NavType.StringType })
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            ResultScreen(navController, viewModel, difficulty)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController, viewModel)
        }
        composable(Screen.About.route) {
            AboutScreen(navController)
        }
        composable(Screen.Dictionary.route) {
            DictionaryScreen(navController)
        }
        composable(
            route = Screen.DictionaryDetail.route,
            arguments = listOf(navArgument("aksaraName") { type = NavType.StringType })
        ) { backStackEntry ->
            val aksaraName = backStackEntry.arguments?.getString("aksaraName") ?: ""
            DictionaryDetailScreen(navController, aksaraName)
        }
    }
}
