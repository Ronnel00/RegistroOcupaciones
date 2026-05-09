package edu.ucne.registroocupaciones.presentation.ocupacion.edit

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOcupacionScreen(
    ocupacionId: Int?,
    onNavigateBack: () -> Unit,
    onDrawer: () -> Unit,
    viewModel: EditOcupacionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(ocupacionId) {
        viewModel.onEvent(EditOcupacionUiEvent.Load(ocupacionId))
    }

    // Muestra mensaje y limpia formulario al guardar
    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("✅ Ocupación guardada exitosamente")
            viewModel.onEvent(EditOcupacionUiEvent.Load(null))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isNew) "Nueva Ocupación" else "Editar Ocupación") },
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
                        value = state.descripcion,
                        onValueChange = { viewModel.onEvent(EditOcupacionUiEvent.DescripcionChanged(it)) },
                        label = { Text("Descripción") },
                        isError = state.descripcionError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.descripcionError?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = state.sueldo?.toString() ?: "",
                        onValueChange = { viewModel.onEvent(EditOcupacionUiEvent.SueldoChanged(it)) },
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
                            onClick = { viewModel.onEvent(EditOcupacionUiEvent.Save) },
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
                            Text("Guardar")
                        }

                        if (!state.isNew) {
                            OutlinedButton(
                                onClick = { viewModel.onEvent(EditOcupacionUiEvent.Delete) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}