package edu.ucne.registroocupaciones.presentation.ocupacion.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ObserveOcupacionUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListOcupacionViewModel @Inject constructor(
    private val observeOcupacion: ObserveOcupacionUseCase,
    private val deleteOcupacion: DeleteOcupacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListOcupacionUiState(isLoading = true))
    val state: StateFlow<ListOcupacionUiState> = _state.asStateFlow()

    init { load() }

    fun onEvent(event: ListOcupacionUiEvent) {
        when (event) {
            ListOcupacionUiEvent.Load -> load()
            is ListOcupacionUiEvent.Delete -> delete(event.id)
            else -> Unit
        }
    }

    private fun load() {
        viewModelScope.launch {
            observeOcupacion().collectLatest { list ->
                _state.update { it.copy(isLoading = false, ocupaciones = list) }
            }
        }
    }

    private fun delete(id: Int) {
        viewModelScope.launch {
            try {
                deleteOcupacion(id)
                _state.update { it.copy(message = "Ocupación eliminada") }
            } catch (e: Exception) {
                _state.update { it.copy(message = "Error: ${e.message}") }
            }
        }
    }
}