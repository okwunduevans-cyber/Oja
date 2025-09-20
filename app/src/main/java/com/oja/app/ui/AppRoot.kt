package com.oja.app.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oja.app.navigation.Route
import com.oja.app.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    OjaTheme {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Route.Welcome.path) {
            composable(Route.Welcome.path) { WelcomeScreen(onStart = { nav.navigate(Route.Home.path) }) }
            composable(Route.Home.path) { HomeScreen(nav) }
            composable(Route.Cart.path) { CartScreen(nav) }
            composable(Route.Jobs.path) { JobsDashboardScreen(nav) }
            composable(Route.TransporterSignup.path) { TransporterSignupScreen(nav) }
            composable(Route.VendorSignup.path) { VendorSignupScreen(nav) }
            composable(Route.Payments.path) { PaymentsScreen(nav) }
            composable(Route.Track.path) { backStack ->
                val orderId = backStack.arguments?.getString("orderId").orEmpty()
                TrackScreen(orderId = orderId, onBack = { nav.popBackStack() })
            }
        }
    }
}
