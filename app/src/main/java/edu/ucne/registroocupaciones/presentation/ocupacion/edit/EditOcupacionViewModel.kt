package edu.ucne.registroocupaciones.presentation.ocupacion.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.GetOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.UpsertOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ValidateOcupacionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditOcupacionViewModel @Inject constructor(
    private val getOcupacion: GetOcupacionUseCase,
    private val upsertOcupacion: UpsertOcupacionUseCase,
    private val deleteOcupacion: DeleteOcupacionUseCase,
    private val validate: ValidateOcupacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditOcupacionUiState())
    val state: StateFlow<EditOcupacionUiState> = _state.asStateFlow()

    fun onEvent(event: EditOcupacionUiEvent) {
        when (event) {
            is EditOcupacionUiEvent.Load -> load(event.id)
            is EditOcupacionUiEvent.DescripcionChanged -> {
                _state.update {
                    it.copy(
                        descripcion = event.value,
                        descripcionError = if (event.value.isBlank()) "La descripción es requerida" else null
                    )
                }
            }
            is EditOcupacionUiEvent.SueldoChanged -> {
                val d = event.value.toDoubleOrNull()
                _state.update {
                    it.copy(
                        sueldo = d,
                        sueldoError = when {
                            event.value.isBlank() -> "El sueldo es requerido"
                            d == null -> "Ingrese un número válido"
                            d <= 0 -> "El sueldo debe ser mayor a 0"
                            else -> null
                        }
                    )
                }
            }
            EditOcupacionUiEvent.Save -> save()
            EditOcupacionUiEvent.Delete -> delete()
        }
    }

    private fun load(id: Int?) {
        if (id == null || id == 0) {
            _state.value = EditOcupacionUiState(isNew = true, saved = false)
            return
        }
        viewModelScope.launch {
            getOcupacion(id)?.let { o ->
                _state.update {
                    it.copy(
                        isNew = false,
                        ocupacionId = o.ocupacionId,
                        descripcion = o.descripcion,
                        sueldo = o.sueldo,
                        saved = false
                    )
                }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val v = validate(
                descripcion = _state.value.descripcion,
                sueldo = _state.value.sueldo,
                currentOcupacionId = _state.value.ocupacionId
            )
            if (!v.isValid) {
                _state.update {
                    it.copy(
                        descripcionError = v.descripcionError,
                        sueldoError = v.sueldoError
                    )
                }
                return@launch
            }
            _state.update { it.copy(isSaving = true) }
            try {
                upsertOcupacion(
                    Ocupacion(
                        ocupacionId = _state.value.ocupacionId ?: 0,
                        descripcion = _state.value.descripcion.trim(),
                        sueldo = _state.value.sueldo ?: 0.0
                    )
                )
                _state.update {
                    EditOcupacionUiState(
                        isNew = true,
                        saved = true
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, descripcionError = e.message) }
            }
        }
    }

    private fun delete() {
        val id = _state.value.ocupacionId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            try {
                deleteOcupacion(id)
                _state.update { it.copy(isDeleting = false, deleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isDeleting = false, descripcionError = e.message) }
            }
        }
    }
}