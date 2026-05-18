package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoScreen
import edu.ucne.registroocupaciones.presentation.empleado.list.EmpleadoListScreen
import edu.ucne.registroocupaciones.presentation.ocupacion.OcupacionMainScreen
import kotlinx.coroutines.launch

@Composable
fun RegistroNavHost(navHostController: NavHostController) {
    val scope = rememberCoroutineScope()
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
                OcupacionMainScreen(
                    onDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<Screen.EmpleadoList> {
                EmpleadoListScreen(
                    onDrawer = { scope.launch { drawerState.open() } },
                    goToEmpleado = { id -> navHostController.navigate(Screen.Empleado(id)) },
                    createEmpleado = { navHostController.navigate(Screen.Empleado(0)) }
                )
            }

            composable<Screen.Empleado> {
                val args = it.toRoute<Screen.Empleado>()
                EditEmpleadoScreen(
                    empleadoId = args.empleadoId,
                    onNavigateBack = { navHostController.navigateUp() },
                    onDrawer = { scope.launch { drawerState.open() } }
                )
            }
        }
    }
}