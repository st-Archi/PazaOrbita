package com.plazaorbita.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plazaorbita.app.ui.admin.AdminHomeScreen
import com.plazaorbita.app.ui.auth.LoginScreen
import com.plazaorbita.app.ui.auth.RegisterScreen
import com.plazaorbita.app.ui.business.BusinessHomeScreen
import com.plazaorbita.app.ui.customer.CustomerHomeScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ADMIN_HOME = "admin_home"
    const val BUSINESS_HOME = "business_home"
    const val CUSTOMER_HOME = "customer_home"
}

@Composable
fun PlazaOrbitaNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { role -> navigateByRole(navController, role) },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { role -> navigateByRole(navController, role) },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_HOME) { AdminHomeScreen() }

        // businessId=1L es un placeholder: en el siguiente sprint se obtiene
        // el negocio real ligado al usuario dueño (GET /api/businesses?ownerId=...)
        composable(Routes.BUSINESS_HOME) { BusinessHomeScreen(businessId = 1L) }

        composable(Routes.CUSTOMER_HOME) { CustomerHomeScreen() }
    }
}

private fun navigateByRole(navController: NavHostController, role: String) {
    val destination = when (role) {
        "ADMIN" -> Routes.ADMIN_HOME
        "BUSINESS_OWNER" -> Routes.BUSINESS_HOME
        else -> Routes.CUSTOMER_HOME
    }
    navController.navigate(destination) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}
