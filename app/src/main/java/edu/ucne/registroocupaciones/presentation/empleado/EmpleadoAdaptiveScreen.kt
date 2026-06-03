@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package edu.ucne.registroocupaciones.presentation.empleado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoViewModel
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun EmpleadoAdaptiveScreen(
    onDrawer: () -> Unit = {},
    editViewModel: EditEmpleadoViewModel = hiltViewModel(),
    listViewModel: ListEmpleadoViewModel = hiltViewModel()
) {
    val editState by editViewModel.state.collectAsStateWithLifecycle()
    val listState by listViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var sexoExpanded by remember { mutableStateOf(false) }
    val sexoOpciones = listOf("Masculino", "Femenino")

    LaunchedEffect(editState.saved) {
        if (editState.saved) {
            snackbarHostState.showSnackbar("Empleado guardado exitosamente")
            editViewModel.onEvent(EditEmpleadoUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    LaunchedEffect(editState.deleted) {
        if (editState.deleted) {
            snackbarHostState.showSnackbar("Empleado eliminado")
            editViewModel.onEvent(EditEmpleadoUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = editState.fechaIngreso ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    editViewModel.onEvent(
                        EditEmpleadoUiEvent.FechaIngresoChanged(datePickerState.selectedDateMillis)
                    )
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Empleados (${listState.empleados.size})") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editViewModel.onEvent(EditEmpleadoUiEvent.Load(null))
                scope.launch {
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Empleado")
            }
        }
    ) { padding ->
        ListDetailPaneScaffold(
            modifier = Modifier.padding(padding),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Empleados registrados (${listState.empleados.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (listState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else if (listState.empleados.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay empleados registrados", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(listState.empleados) { empleado ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        onClick = {
                                            editViewModel.onEvent(
                                                EditEmpleadoUiEvent.Load(empleado.empleadoId)
                                            )
                                            scope.launch {
                                                navigator.navigateTo(
                                                    ListDetailPaneScaffoldRole.Detail,
                                                    empleado.empleadoId
                                                )
                                            }
                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    "#${empleado.empleadoId} - ${empleado.nombres}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    "Fecha: ${dateFormatter.format(Date(empleado.fechaIngreso))}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    "Sexo: ${empleado.sexo}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    "Sueldo: $${empleado.sueldo}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            IconButton(onClick = {
                                                listViewModel.onEvent(
                                                    ListEmpleadoUiEvent.Delete(empleado.empleadoId)
                                                )
                                            }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.Red
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            detailPane = {
                AnimatedPane {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = if (editState.isNew) "Nuevo Empleado" else "Editar Empleado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Nombres
                                OutlinedTextField(
                                    value = editState.nombres,
                                    onValueChange = {
                                        editViewModel.onEvent(
                                            EditEmpleadoUiEvent.NombresChanged(it)
                                        )
                                    },
                                    label = { Text("Nombres") },
                                    isError = editState.nombresError != null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                editState.nombresError?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Fecha Ingreso
                                OutlinedTextField(
                                    value = editState.fechaIngreso?.takeIf { it != 0L }
                                        ?.let { dateFormatter.format(Date(it)) } ?: "",
                                    onValueChange = {},
                                    label = { Text("Fecha de Ingreso") },
                                    readOnly = true,
                                    isError = editState.fechaIngresoError != null,
                                    trailingIcon = {
                                        IconButton(onClick = { showDatePicker = true }) {
                                            Icon(
                                                Icons.Default.DateRange,
                                                contentDescription = "Fecha"
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                editState.fechaIngresoError?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(8.dp))


                                ExposedDropdownMenuBox(
                                    expanded = sexoExpanded,
                                    onExpandedChange = { sexoExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = editState.sexo,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Sexo") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = sexoExpanded
                                            )
                                        },
                                        isError = editState.sexoError != null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = sexoExpanded,
                                        onDismissRequest = { sexoExpanded = false }
                                    ) {
                                        sexoOpciones.forEach { opcion ->
                                            DropdownMenuItem(
                                                text = { Text(opcion) },
                                                onClick = {
                                                    editViewModel.onEvent(
                                                        EditEmpleadoUiEvent.SexoChanged(opcion)
                                                    )
                                                    sexoExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                                editState.sexoError?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Sueldo
                                OutlinedTextField(
                                    value = editState.sueldo?.toString() ?: "",
                                    onValueChange = {
                                        editViewModel.onEvent(
                                            EditEmpleadoUiEvent.SueldoChanged(it)
                                        )
                                    },
                                    label = { Text("Sueldo") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    ),
                                    isError = editState.sueldoError != null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                editState.sueldoError?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            editViewModel.onEvent(EditEmpleadoUiEvent.Save)
                                        },
                                        enabled = !editState.isSaving
                                    ) {
                                        if (editState.isSaving) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(Icons.Default.Edit, contentDescription = null)
                                        }
                                        Spacer(Modifier.width(4.dp))
                                        Text(if (editState.isNew) "Guardar" else "Actualizar")
                                    }

                                    if (!editState.isNew) {
                                        OutlinedButton(
                                            onClick = {
                                                editViewModel.onEvent(EditEmpleadoUiEvent.Delete)
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color.Red
                                            )
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Eliminar"
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text("Eliminar")
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            editViewModel.onEvent(EditEmpleadoUiEvent.Load(null))
                                            scope.launch {
                                                navigator.navigateTo(ListDetailPaneScaffoldRole.List)
                                            }
                                        }
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}