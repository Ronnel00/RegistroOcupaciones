package edu.ucne.registroocupaciones.presentation.ocupacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionUiEvent
import edu.ucne.registroocupaciones.presentation.ocupacion.edit.EditOcupacionViewModel
import edu.ucne.registroocupaciones.presentation.ocupacion.list.ListOcupacionUiEvent
import edu.ucne.registroocupaciones.presentation.ocupacion.list.ListOcupacionViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OcupacionMainScreen(
    editViewModel: EditOcupacionViewModel = hiltViewModel(),
    listViewModel: ListOcupacionViewModel = hiltViewModel()
) {
    val editState by editViewModel.state.collectAsStateWithLifecycle()
    val listState by listViewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    LaunchedEffect(editState.saved) {
        if (editState.saved) {
            snackbarHostState.showSnackbar("Ocupación guardada exitosamente")
            editViewModel.onEvent(EditOcupacionUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    LaunchedEffect(editState.deleted) {
        if (editState.deleted) {
            snackbarHostState.showSnackbar("🗑 Ocupación eliminada")
            editViewModel.onEvent(EditOcupacionUiEvent.Load(null))
            navigator.navigateTo(ListDetailPaneScaffoldRole.List)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro de Ocupaciones") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editViewModel.onEvent(EditOcupacionUiEvent.Load(null))
                scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail) }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Ocupación")
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
                            text = "Ocupaciones registradas (${listState.ocupaciones.size})",
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
                        } else if (listState.ocupaciones.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay ocupaciones registradas", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(listState.ocupaciones) { ocupacion ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        onClick = {
                                            editViewModel.onEvent(
                                                EditOcupacionUiEvent.Load(ocupacion.ocupacionId)
                                            )
                                            scope.launch {
                                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, ocupacion.ocupacionId)
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
                                                    "#${ocupacion.ocupacionId} - ${ocupacion.descripcion}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    "Sueldo: $${ocupacion.sueldo}",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                            IconButton(onClick = {
                                                listViewModel.onEvent(
                                                    ListOcupacionUiEvent.Delete(ocupacion.ocupacionId)
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
                    ) {
                        Text(
                            text = if (editState.isNew) "Nueva Ocupación" else "Editar Ocupación",
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
                                OutlinedTextField(
                                    value = editState.descripcion,
                                    onValueChange = {
                                        editViewModel.onEvent(
                                            EditOcupacionUiEvent.DescripcionChanged(it)
                                        )
                                    },
                                    label = { Text("Descripción") },
                                    isError = editState.descripcionError != null,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                editState.descripcionError?.let {
                                    Text(
                                        it,
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = editState.sueldo?.toString() ?: "",
                                    onValueChange = {
                                        editViewModel.onEvent(
                                            EditOcupacionUiEvent.SueldoChanged(it)
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
                                            editViewModel.onEvent(EditOcupacionUiEvent.Save)
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
                                                editViewModel.onEvent(EditOcupacionUiEvent.Delete)
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
                                            editViewModel.onEvent(EditOcupacionUiEvent.Load(null))
                                            scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.List) }
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