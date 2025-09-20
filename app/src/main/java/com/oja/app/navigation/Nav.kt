package com.oja.app.navigation

sealed class Route(val path: String) {
    data object Welcome: Route("welcome")
    data object Home: Route("home")
    data object Cart: Route("cart")
    data object Jobs: Route("jobs")
    data object Track: Route("track/{orderId}") {
        fun path(orderId: String) = "track/$orderId"
    }
    data object TransporterSignup: Route("transporter")
    data object VendorSignup: Route("vendor")
    data object Payments: Route("payments")
}
