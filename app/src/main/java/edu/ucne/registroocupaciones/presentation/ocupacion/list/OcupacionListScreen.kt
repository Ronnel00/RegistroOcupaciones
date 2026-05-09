package edu.ucne.registroocupaciones.presentation.ocupacion.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcupacionListScreen(
    onDrawer: () -> Unit,
    goToOcupacion: (Int) -> Unit,
    createOcupacion: () -> Unit,
    viewModel: ListOcupacionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ocupaciones") },
                navigationIcon = {
                    IconButton(onClick = onDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = createOcupacion) { Text("+") }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(state.ocupaciones) { ocupacion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { goToOcupacion(ocupacion.ocupacionId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ocupacion.descripcion, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Sueldo: $${ocupacion.sueldo}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = {
                                viewModel.onEvent(ListOcupacionUiEvent.Delete(ocupacion.ocupacionId))
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }
}