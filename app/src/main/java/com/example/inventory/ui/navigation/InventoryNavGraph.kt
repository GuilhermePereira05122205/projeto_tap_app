/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.inventory.ui.home.HomeDestination
import com.example.inventory.ui.home.HomeScreen
import com.example.inventory.ui.item.ProdutoDetailsDestination
import com.example.inventory.ui.item.ProdutoDetailsScreen
import com.example.inventory.ui.item.ProdutoEditDestination
import com.example.inventory.ui.item.ProdutoEditScreen
import com.example.inventory.ui.item.ProdutoEntryDestination
import com.example.inventory.ui.item.ProdutoEntryScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToProdutoEntry = { navController.navigate(ProdutoEntryDestination.route) },
                navigateToProdutoUpdate = {
                    navController.navigate("${ProdutoDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = ProdutoEntryDestination.route) {
            ProdutoEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = ProdutoDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(ProdutoDetailsDestination.produtoIdArg) {
                type = NavType.IntType
            })
        ) {
            ProdutoDetailsScreen(
                ProdutoToEditItem = { navController.navigate("${ProdutoEditDestination.route}/$it") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = ProdutoEditDestination.routeWithArgs,
            arguments = listOf(navArgument(ProdutoEditDestination.produtoIdArg) {
                type = NavType.IntType
            })
        ) {
            ProdutoEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
