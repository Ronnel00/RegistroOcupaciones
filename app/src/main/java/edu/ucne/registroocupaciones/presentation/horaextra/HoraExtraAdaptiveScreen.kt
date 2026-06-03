@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package edu.ucne.registroocupaciones.presentation.horaextra

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
import edu.ucne.registroocupaciones.presentation.horaextra.edit.EditHoraExtraUiEvent
import edu.ucne.registroocupaciones.presentation.horaextra.edit.EditHoraExtraViewModel
import edu.ucne.registroocupaciones.presentation.horaextra.list.ListHoraExtraUiEvent
import edu.ucne.registroocupaciones.presentation.horaextra.list.ListHoraExtraViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun HoraExtraAdaptiveScreen(
    onDrawer: () -> Unit = {},
    editViewModel: EditHoraExtraViewModel = hiltViewModel(),
    listViewModel: ListHoraExtraViewModel = hiltViewModel()
) {
    val editState by editViewModel.state.collectAsStateWithLifecycle()
    val listState by listViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var showDateDesdePicker by remember { mutableStateOf(false) }
    var showDateHastaPicker by remember { mutableStateOf(false) }
    var empleadoExpanded by remember { mutableStateOf(false) }

    val empleadoMap = remember(editState.empleados) {
        editState.empleados.associate { it.empleadoId to it.nombres }
    }

    LaunchedEffect(editState.saved) {
        if (editState.saved) {
            snackbarHostState.showSnackbar("Hora extra guardada exitosamente")
            editViewModel.onEvent(EditHoraExtraUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    LaunchedEffect(editState.deleted) {
        if (editState.deleted) {
            snackbarHostState.showSnackbar("Hora extra eliminada")
            editViewModel.onEvent(EditHoraExtraUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    if (showDateDesdePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = editState.fechaDesde ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDateDesdePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    editViewModel.onEvent(
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
            initialSelectedDateMillis = editState.fechaHasta ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDateHastaPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    editViewModel.onEvent(
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
                title = { Text("Horas Extra (${listState.horasExtras.size})") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editViewModel.onEvent(EditHoraExtraUiEvent.Load(null))
                scope.launch {
                    navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Hora Extra")
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
                            text = "Horas Extra registradas (${listState.horasExtras.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (listState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        } else if (listState.horasExtras.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay horas extra registradas", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(listState.horasExtras) { horaExtra ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        onClick = {
                                            editViewModel.onEvent(
                                                EditHoraExtraUiEvent.Load(horaExtra.horaExtraId)
                                            )
                                            scope.launch {
                                                navigator.navigateTo(
                                                    ListDetailPaneScaffoldRole.Detail,
                                                    horaExtra.horaExtraId
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
                                                    "#${horaExtra.horaExtraId} - ${empleadoMap[horaExtra.empleadoId] ?: "Empleado #${horaExtra.empleadoId}"}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    "Desde: ${dateFormatter.format(Date(horaExtra.fechaDesde))}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    "Hasta: ${dateFormatter.format(Date(horaExtra.fechaHasta))}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    "Horas totales: ${horaExtra.horasTotales}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    "Total a pagar: $${"%.2f".format(horaExtra.totalAPagar)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            IconButton(onClick = {
                                                listViewModel.onEvent(
                                                    ListHoraExtraUiEvent.Delete(horaExtra.horaExtraId)
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (editState.isNew) "Nueva Hora Extra" else "Editar Hora Extra",
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
                                    ExposedDropdownMenuBox(
                                        expanded = empleadoExpanded,
                                        onExpandedChange = { empleadoExpanded = it }
                                    ) {
                                        OutlinedTextField(
                                            value = editState.empleadoNombre.ifEmpty {
                                                editState.empleadoId?.let { "Empleado #$it" } ?: ""
                                            },
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Empleado") },
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = empleadoExpanded
                                                )
                                            },
                                            isError = editState.empleadoError != null,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = empleadoExpanded,
                                            onDismissRequest = { empleadoExpanded = false }
                                        ) {
                                            editState.empleados.forEach { empleado ->
                                                DropdownMenuItem(
                                                    text = { Text(empleado.nombres) },
                                                    onClick = {
                                                        editViewModel.onEvent(
                                                            EditHoraExtraUiEvent.EmpleadoChanged(
                                                                empleado.empleadoId
                                                            )
                                                        )
                                                        empleadoExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    editState.empleadoError?.let {
                                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editState.fechaDesde?.takeIf { it != 0L }
                                            ?.let { dateFormatter.format(Date(it)) } ?: "",
                                        onValueChange = {},
                                        label = { Text("Fecha Desde") },
                                        readOnly = true,
                                        isError = editState.fechaDesdeError != null,
                                        trailingIcon = {
                                            IconButton(onClick = { showDateDesdePicker = true }) {
                                                Icon(Icons.Default.DateRange, contentDescription = "Fecha Desde")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    editState.fechaDesdeError?.let {
                                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = editState.fechaHasta?.takeIf { it != 0L }
                                            ?.let { dateFormatter.format(Date(it)) } ?: "",
                                        onValueChange = {},
                                        label = { Text("Fecha Hasta") },
                                        readOnly = true,
                                        isError = editState.fechaHastaError != null,
                                        trailingIcon = {
                                            IconButton(onClick = { showDateHastaPicker = true }) {
                                                Icon(Icons.Default.DateRange, contentDescription = "Fecha Hasta")
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    editState.fechaHastaError?.let {
                                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = if (editState.horasTotales == 0.0) "" else editState.horasTotales.toString(),
                                        onValueChange = {
                                            editViewModel.onEvent(
                                                EditHoraExtraUiEvent.HorasTotalesChanged(it)
                                            )
                                        },
                                        label = { Text("Horas Totales") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        isError = editState.horasError != null,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = if (editState.horasNocturnas == 0.0) "" else editState.horasNocturnas.toString(),
                                        onValueChange = {
                                            editViewModel.onEvent(
                                                EditHoraExtraUiEvent.HorasNocturnasChanged(it)
                                            )
                                        },
                                        label = { Text("Horas Nocturnas") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        isError = editState.horasError != null,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    editState.horasError?.let {
                                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                    }

                                    if (editState.totalAPagar > 0.0) {
                                        Spacer(Modifier.height(12.dp))
                                        HorizontalDivider()
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Resumen de cálculo",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        ResumenFila("Sueldo por día", editState.sueldoPorDia)
                                        ResumenFila("Sueldo por hora", editState.sueldoPorHora)
                                        ResumenFila("Horas normales", editState.horasNormales)
                                        ResumenFila("Horas extras totales", editState.horasExtrasTotales)
                                        ResumenFila("Horas al 35%", editState.horasAl35)
                                        ResumenFila("Horas al 100%", editState.horasAl100)
                                        ResumenFila("Monto 35%", editState.monto35)
                                        ResumenFila("Monto 100%", editState.monto100)
                                        ResumenFila("Monto nocturno", editState.montoNocturno)
                                        Spacer(Modifier.height(4.dp))
                                        HorizontalDivider()
                                        Spacer(Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "Total a pagar",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "$${"%.2f".format(editState.totalAPagar)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(16.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        OutlinedButton(
                                            onClick = { editViewModel.onEvent(EditHoraExtraUiEvent.Save) },
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
                                                onClick = { editViewModel.onEvent(EditHoraExtraUiEvent.Delete) },
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = Color.Red
                                                )
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                                Spacer(Modifier.width(4.dp))
                                                Text("Eliminar")
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                editViewModel.onEvent(EditHoraExtraUiEvent.Load(null))
                                                scope.launch {
                                                    navigator.navigateTo(ListDetailPaneScaffoldRole.List)
                                                }
                                            }
                                        ) {
                                            Text("Cancelar")
                                        }
                                    }

                                    Spacer(Modifier.height(32.dp))
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun ResumenFila(label: String, valor: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text("$${"%.2f".format(valor)}", style = MaterialTheme.typography.bodySmall)
    }
}