package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    drawerState: DrawerState,
    navHostController: NavHostController,
    content: @Composable () -> Unit
) {
    val selectedItem = remember { mutableStateOf("Ocupaciones") }
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    item {
                        DrawerItem(
                            title = "Ocupaciones",
                            icon = Icons.Filled.Work,
                            isSelected = selectedItem.value == "Ocupaciones"
                        ) {
                            navHostController.navigate(Screen.OcupacionList) {
                                launchSingleTop = true
                            }
                            selectedItem.value = "Ocupaciones"
                            scope.launch { drawerState.close() }
                        }
                    }
                    item {
                        DrawerItem(
                            title = "Empleados",
                            icon = Icons.Filled.People,
                            isSelected = selectedItem.value == "Empleados"
                        ) {
                            navHostController.navigate(Screen.EmpleadoList) {
                                launchSingleTop = true
                            }
                            selectedItem.value = "Empleados"
                            scope.launch { drawerState.close() }
                        }
                    }
                }
            }
        }
    ) {
        content()
    }
}