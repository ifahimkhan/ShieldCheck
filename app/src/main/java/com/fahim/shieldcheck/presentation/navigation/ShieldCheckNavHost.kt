package com.fahim.shieldcheck.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fahim.shieldcheck.presentation.screens.appaudit.AppAuditScreen
import com.fahim.shieldcheck.presentation.screens.appaudit.AppDetailScreen
import com.fahim.shieldcheck.presentation.screens.devicesecurity.DeviceSecurityScreen
import com.fahim.shieldcheck.presentation.screens.home.HomeScreen
import com.fahim.shieldcheck.presentation.screens.network.NetworkScanScreen
import com.fahim.shieldcheck.presentation.screens.privacydashboard.PrivacyDashboardScreen
import com.fahim.shieldcheck.presentation.screens.settings.SettingsScreen

@Composable
fun ShieldCheckNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAppAudit = { navController.navigate(Screen.AppAudit.route) },
                onNavigateToDeviceSecurity = { navController.navigate(Screen.DeviceSecurity.route) },
                onNavigateToNetworkScan = { navController.navigate(Screen.NetworkScan.route) },
                onNavigateToPrivacyDashboard = { navController.navigate(Screen.PrivacyDashboard.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.AppAudit.route) {
            AppAuditScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAppDetail = { packageName ->
                    navController.navigate(Screen.AppDetail.createRoute(packageName))
                }
            )
        }

        composable(
            route = Screen.AppDetail.route,
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val packageName = backStackEntry.arguments?.getString("packageName") ?: ""
            AppDetailScreen(
                packageName = packageName,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.DeviceSecurity.route) {
            DeviceSecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NetworkScan.route) {
            NetworkScanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PrivacyDashboard.route) {
            PrivacyDashboardScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAppAudit = { navController.navigate(Screen.AppAudit.route) },
                onNavigateToDeviceSecurity = { navController.navigate(Screen.DeviceSecurity.route) },
                onNavigateToNetworkScan = { navController.navigate(Screen.NetworkScan.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
