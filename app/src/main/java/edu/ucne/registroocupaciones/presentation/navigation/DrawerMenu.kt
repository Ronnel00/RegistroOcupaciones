package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Screen
)

@Composable
fun DrawerMenu(
    drawerState: DrawerState,
    navHostController: NavHostController,
    content: @Composable () -> Unit
) {
    val items = listOf(
        NavigationItem("Ocupaciones", Icons.Filled.Work, Screen.OcupacionList),
        NavigationItem("Empleados", Icons.Filled.People, Screen.EmpleadoList),
        NavigationItem("Horas Extras", Icons.Filled.AccessTime, Screen.HoraExtraList)
    )

    var selectedItem by remember { mutableStateOf(0) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEachIndexed { index, item ->
                item(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        navHostController.navigate(item.screen) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) {
        content()
    }
}