package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import edu.ucne.registroocupaciones.presentation.empleado.EmpleadoAdaptiveScreen
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoScreen
import edu.ucne.registroocupaciones.presentation.horaextra.HoraExtraAdaptiveScreen
import edu.ucne.registroocupaciones.presentation.horaextra.edit.EditHoraExtraScreen
import edu.ucne.registroocupaciones.presentation.ocupacion.OcupacionMainScreen

@Composable
fun RegistroNavHost(navHostController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    DrawerMenu(
        drawerState = drawerState,
        navHostController = navHostController
    ) {
        NavHost(
            navController = navHostController,
            startDestination = Screen.OcupacionList
        ) {
            composable<Screen.OcupacionList> {
                OcupacionMainScreen()
            }
            composable<Screen.Ocupacion> {
                OcupacionMainScreen()
            }
            composable<Screen.EmpleadoList> {
                EmpleadoAdaptiveScreen(onDrawer = {})
            }
            composable<Screen.Empleado> {
                val args = it.toRoute<Screen.Empleado>()
                EditEmpleadoScreen(
                    empleadoId = args.empleadoId,
                    onNavigateBack = { navHostController.navigateUp() },
                    onDrawer = {}
                )
            }
            composable<Screen.HoraExtraList> {
                HoraExtraAdaptiveScreen(onDrawer = {})
            }
            composable<Screen.HoraExtra> {
                val args = it.toRoute<Screen.HoraExtra>()
                EditHoraExtraScreen(
                    horaExtraId = args.horaExtraId,
                    onNavigateBack = { navHostController.navigateUp() },
                    onDrawer = {}
                )
            }
        }
    }
}