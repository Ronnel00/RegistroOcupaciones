package edu.ucne.registroocupaciones.presentation.empleado.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEmpleadoViewModel @Inject constructor(
    private val getEmpleado: GetEmpleadoUseCase,
    private val upsertEmpleado: UpsertEmpleadoUseCase,
    private val deleteEmpleado: DeleteEmpleadoUseCase,
    private val validate: ValidateEmpleadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditEmpleadoUiState())
    val state: StateFlow<EditEmpleadoUiState> = _state.asStateFlow()

    fun onEvent(event: EditEmpleadoUiEvent) {
        when (event) {
            is EditEmpleadoUiEvent.Load -> load(event.id)
            is EditEmpleadoUiEvent.NombresChanged -> _state.update {
                it.copy(nombres = event.value, nombresError = if (event.value.isBlank()) "El nombre es requerido" else null)
            }
            is EditEmpleadoUiEvent.FechaIngresoChanged -> _state.update {
                it.copy(fechaIngreso = event.value, fechaIngresoError = if (event.value == null) "La fecha es requerida" else null)
            }
            is EditEmpleadoUiEvent.SexoChanged -> _state.update {
                it.copy(sexo = event.value, sexoError = if (event.value.isBlank()) "El sexo es requerido" else null)
            }
            is EditEmpleadoUiEvent.SueldoChanged -> {
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
            EditEmpleadoUiEvent.Save -> save()
            EditEmpleadoUiEvent.Delete -> delete()
        }
    }

    private fun load(id: Int?) {
        if (id == null || id == 0) {
            _state.value = EditEmpleadoUiState(isNew = true)
            return
        }
        viewModelScope.launch {
            getEmpleado(id)?.let { e ->
                _state.update {
                    it.copy(
                        isNew = false,
                        empleadoId = e.empleadoId,
                        fechaIngreso = e.fechaIngreso,
                        nombres = e.nombres,
                        sexo = e.sexo,
                        sueldo = e.sueldo
                    )
                }
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val v = validate(
                nombres = _state.value.nombres,
                fechaIngreso = _state.value.fechaIngreso,
                sexo = _state.value.sexo,
                sueldo = _state.value.sueldo,
                currentEmpleadoId = _state.value.empleadoId
            )
            if (!v.isValid) {
                _state.update {
                    it.copy(
                        nombresError = v.nombresError,
                        fechaIngresoError = v.fechaIngresoError,
                        sexoError = v.sexoError,
                        sueldoError = v.sueldoError
                    )
                }
                return@launch
            }
            _state.update { it.copy(isSaving = true) }
            try {
                upsertEmpleado(
                    Empleado(
                        empleadoId = _state.value.empleadoId ?: 0,
                        fechaIngreso = _state.value.fechaIngreso ?: 0L,
                        nombres = _state.value.nombres.trim(),
                        sexo = _state.value.sexo,
                        sueldo = _state.value.sueldo ?: 0.0
                    )
                )
                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, nombresError = e.message) }
            }
        }
    }

    private fun delete() {
        val id = _state.value.empleadoId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            try {
                deleteEmpleado(id)
                _state.update { it.copy(isDeleting = false, deleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isDeleting = false, nombresError = e.message) }
            }
        }
    }
}