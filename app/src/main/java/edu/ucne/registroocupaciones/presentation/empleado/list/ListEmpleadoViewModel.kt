package edu.ucne.registroocupaciones.presentation.empleado.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEmpleadoViewModel @Inject constructor(
    private val observeEmpleado: ObserveEmpleadoUseCase,
    private val deleteEmpleado: DeleteEmpleadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListEmpleadoUiState(isLoading = true))
    val state: StateFlow<ListEmpleadoUiState> = _state.asStateFlow()

    init { load() }

    fun onEvent(event: ListEmpleadoUiEvent) {
        when (event) {
            ListEmpleadoUiEvent.Load -> load()
            is ListEmpleadoUiEvent.Delete -> delete(event.id)
        }
    }

    private fun load() {
        viewModelScope.launch {
            observeEmpleado().collectLatest { list ->
                _state.update { it.copy(isLoading = false, empleados = list) }
            }
        }
    }

    private fun delete(id: Int) {
        viewModelScope.launch {
            try {
                deleteEmpleado(id)
                _state.update { it.copy(message = "Empleado eliminado") }
            } catch (e: Exception) {
                _state.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }
}