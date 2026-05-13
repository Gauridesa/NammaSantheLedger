package com.example.nammasantheledger.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nammasantheledger.viewmodel.SantheViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: SantheViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable("add_customer") {
            AddCustomerScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            "customer_detail/{customerId}/{customerName}/{customerPhone}",
            arguments = listOf(
                navArgument("customerId") { type = NavType.IntType },
                navArgument("customerName") { type = NavType.StringType },
                navArgument("customerPhone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            CustomerDetailScreen(
                navController = navController,
                viewModel = viewModel,
                customerId = backStackEntry.arguments?.getInt("customerId") ?: 0,
                customerName = backStackEntry.arguments?.getString("customerName") ?: "",
                customerPhone = backStackEntry.arguments?.getString("customerPhone") ?: ""
            )
        }
        composable(
            "add_transaction/{customerId}/{customerName}",
            arguments = listOf(
                navArgument("customerId") { type = NavType.IntType },
                navArgument("customerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            AddTransactionScreen(
                navController = navController,
                viewModel = viewModel,
                customerId = backStackEntry.arguments?.getInt("customerId") ?: 0,
                customerName = backStackEntry.arguments?.getString("customerName") ?: ""
            )
        }
        composable("daily_summary") {
            DailySummaryScreen(navController = navController, viewModel = viewModel)
        }
    }
}
