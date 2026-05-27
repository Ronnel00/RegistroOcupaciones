package edu.ucne.registroocupaciones.presentation.horaextra.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHoraExtraScreen(
    horaExtraId: Int?,
    onNavigateBack: () -> Unit,
    onDrawer: () -> Unit,
    viewModel: EditHoraExtraViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var empleadoExpanded by remember { mutableStateOf(false) }
    var showDateDesdePicker by remember { mutableStateOf(false) }
    var showDateHastaPicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(horaExtraId) {
        viewModel.onEvent(EditHoraExtraUiEvent.Load(horaExtraId))
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onNavigateBack()
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onNavigateBack()
    }

    if (showDateDesdePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fechaDesde ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDateDesdePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(
                        EditHoraExtraUiEvent.FechaDesdeChanged(datePickerState.selectedDateMillis)
                    )
                    showDateDesdePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDateDesdePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showDateHastaPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.fechaHasta ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDateHastaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(
                        EditHoraExtraUiEvent.FechaHastaChanged(datePickerState.selectedDateMillis)
                    )
                    showDateHastaPicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDateHastaPicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isNew) "Nueva Hora Extra" else "Editar Hora Extra") },
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
                .verticalScroll(rememberScrollState())
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    ExposedDropdownMenuBox(
                        expanded = empleadoExpanded,
                        onExpandedChange = { empleadoExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = if (state.empleadoNombre.isNotBlank())
                                "#${state.empleadoId} - ${state.empleadoNombre}"
                            else "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Empleado") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = empleadoExpanded)
                            },
                            isError = state.empleadoError != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = empleadoExpanded,
                            onDismissRequest = { empleadoExpanded = false }
                        ) {
                            state.empleados.forEach { empleado ->
                                DropdownMenuItem(
                                    text = { Text("#${empleado.empleadoId} - ${empleado.nombres}") },
                                    onClick = {
                                        viewModel.onEvent(
                                            EditHoraExtraUiEvent.EmpleadoChanged(empleado.empleadoId)
                                        )
                                        empleadoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    state.empleadoError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.fechaDesde?.takeIf { it != 0L }
                            ?.let { dateFormatter.format(Date(it)) } ?: "",
                        onValueChange = {},
                        label = { Text("Fecha Desde") },
                        readOnly = true,
                        isError = state.fechaDesdeError != null,
                        trailingIcon = {
                            IconButton(onClick = { showDateDesdePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Fecha desde")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.fechaDesdeError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.fechaHasta?.takeIf { it != 0L }
                            ?.let { dateFormatter.format(Date(it)) } ?: "",
                        onValueChange = {},
                        label = { Text("Fecha Hasta") },
                        readOnly = true,
                        isError = state.fechaHastaError != null,
                        trailingIcon = {
                            IconButton(onClick = { showDateHastaPicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Fecha hasta")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.fechaHastaError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = if (state.horasTotales == 0.0) ""
                        else state.horasTotales.toString(),
                        onValueChange = {
                            viewModel.onEvent(EditHoraExtraUiEvent.HorasTotalesChanged(it))
                        },
                        label = { Text("Horas Trabajadas en la Semana") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.horasError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = if (state.horasNocturnas == 0.0) ""
                        else state.horasNocturnas.toString(),
                        onValueChange = {
                            viewModel.onEvent(EditHoraExtraUiEvent.HorasNocturnasChanged(it))
                        },
                        label = { Text("Horas Nocturnas") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError = state.horasError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.horasError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    if (state.totalAPagar > 0) {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Resumen del Cálculo",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))

                        ResumenItem("Sueldo por día", "$${state.sueldoPorDia}")
                        ResumenItem("Sueldo por hora", "$${state.sueldoPorHora}")
                        ResumenItem("Horas normales (≤44)", "${state.horasNormales} hrs")
                        ResumenItem("Horas extras totales", "${state.horasExtrasTotales} hrs")
                        ResumenItem("Horas al 35% (≤24 extras)", "${state.horasAl35} hrs → $${state.monto35}")
                        ResumenItem("Horas al 100% (>24 extras)", "${state.horasAl100} hrs → $${state.monto100}")
                        ResumenItem("Monto nocturno (+15%)", "$${state.montoNocturno}")

                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Total a Pagar: $${state.totalAPagar}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onEvent(EditHoraExtraUiEvent.Save) },
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(if (state.isNew) "Guardar" else "Actualizar")
                        }

                        if (!state.isNew) {
                            OutlinedButton(
                                onClick = { viewModel.onEvent(EditHoraExtraUiEvent.Delete) },
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

@Composable
private fun ResumenItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}