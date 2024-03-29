package zuper.dev.android.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import zuper.dev.android.dashboard.screen.DashboardScreen
import zuper.dev.android.dashboard.screen.StatScreen
import zuper.dev.android.dashboard.widjets.AppScreens

/**
 * Navigation FUnction helps in navigating between screens
 */
@Composable
fun ScreenNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.DashboardScreen.name
    ) {

        composable(AppScreens.DashboardScreen.name) {
            DashboardScreen(navController = navController)
        }

        composable(
            AppScreens.StatScreen.name + "/{count}",
            arguments = listOf(navArgument(name = "count") { type = NavType.StringType })
        ) { backStackEntry ->
            StatScreen(navController = navController, backStackEntry.arguments?.getString("count"))
        }
    }
}