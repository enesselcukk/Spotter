package com.example.spotter.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun SpotterNavHost(
    navController: NavHostController = rememberNavController(),
    newsListContent: @Composable (onArticleClick: (String) -> Unit) -> Unit,
    newsDetailContent: @Composable (articleId: String, onBackClick: () -> Unit) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NewsListRoute,
    ) {
        composable<NewsListRoute> {
            newsListContent { articleId ->
                navController.navigate(NewsDetailRoute(id = articleId))
            }
        }
        composable<NewsDetailRoute> { entry ->
            val route = entry.toRoute<NewsDetailRoute>()
            newsDetailContent(route.id) {
                navController.popBackStack()
            }
        }
    }
}
