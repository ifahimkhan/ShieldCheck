package com.fahim.shieldcheck.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AppAudit : Screen("app_audit")
    data object AppDetail : Screen("app_detail/{packageName}") {
        fun createRoute(packageName: String) = "app_detail/$packageName"
    }
    data object DeviceSecurity : Screen("device_security")
    data object NetworkScan : Screen("network_scan")
    data object PrivacyDashboard : Screen("privacy_dashboard")
    data object Settings : Screen("settings")
}
