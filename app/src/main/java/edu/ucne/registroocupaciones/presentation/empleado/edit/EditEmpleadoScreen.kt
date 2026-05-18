package edu.ucne.registroocupaciones.presentation.empleado.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEmpleadoScreen(
    empleadoId: Int?,
    onNavigateBack: () -> Unit,
    onDrawer: () -> Unit,
    viewModel: EditEmpleadoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var sexoExpanded by remember { mutableStateOf(false) }
    val sexoOpciones = listOf("Masculino", "Femenino")
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(empleadoId) {
        viewModel.onEvent(EditEmpleadoUiEvent.Load(empleadoId))
    }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar(
                message = " Empleado guardado exitosamente",
                duration = SnackbarDuration.Short
            )
            onNavigateBack()
        }
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) {
            snackbarHostState.showSnackbar(" Empleado eliminado")
            onNavigateBack()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fechaIngreso ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(
                        EditEmpleadoUiEvent.FechaIngresoChanged(datePickerState.selectedDateMillis)
                    )
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isNew) "Nuevo Empleado" else "Editar Empleado") },
                navigationIcon = {
                    IconButton(onClick = onDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.nombres,
                        onValueChange = { viewModel.onEvent(EditEmpleadoUiEvent.NombresChanged(it)) },
                        label = { Text("Nombres") },
                        isError = state.nombresError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.nombresError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.fechaIngreso?.takeIf { it != 0L }
                            ?.let { dateFormatter.format(Date(it)) } ?: "",
                        onValueChange = {},
                        label = { Text("Fecha de Ingreso") },
                        readOnly = true,
                        isError = state.fechaIngresoError != null,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.fechaIngresoError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = sexoExpanded,
                        onExpandedChange = { sexoExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.sexo,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sexo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexoExpanded) },
                            isError = state.sexoError != null,
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
                                        viewModel.onEvent(EditEmpleadoUiEvent.SexoChanged(opcion))
                                        sexoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    state.sexoError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.sueldo?.toString() ?: "",
                        onValueChange = { viewModel.onEvent(EditEmpleadoUiEvent.SueldoChanged(it)) },
                        label = { Text("Sueldo") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.sueldoError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.sueldoError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(EditEmpleadoUiEvent.Save) },
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(if (state.isNew) "Guardar" else "Actualizar")
                        }

                        if (!state.isNew) {
                            OutlinedButton(
                                onClick = { viewModel.onEvent(EditEmpleadoUiEvent.Delete) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                Spacer(Modifier.width(4.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}