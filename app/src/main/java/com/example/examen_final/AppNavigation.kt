package com.example.examen_final

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "product_list") {
        composable("product_list") {
            ProductListScreen { id ->
                navController.navigate("product_detail/$id")
            }
        }
        composable(
            route = "product_detail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("productId")
            id?.let { ProductDetailScreen(it) }
        }
    }
}

@Composable
fun ProductListScreen(onProductClick: (Int) -> Unit) {}

@Composable
fun ProductDetailScreen(productId: Int) {}