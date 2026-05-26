package edu.ucne.registroocupaciones.presentation.horaextra.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHoraExtraViewModel @Inject constructor(
    private val getHoraExtra: GetHoraExtraUseCase,
    private val upsertHoraExtra: UpsertHoraExtraUseCase,
    private val deleteHoraExtra: DeleteHoraExtraUseCase,
    private val validate: ValidateHoraExtraUseCase,
    private val calcular: CalcularHorasExtrasUseCase,
    private val observeEmpleados: ObserveEmpleadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditHoraExtraUiState())
    val state: StateFlow<EditHoraExtraUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeEmpleados().collect { empleados ->
                _state.update { it.copy(empleados = empleados) }
            }
        }
    }

    fun onEvent(event: EditHoraExtraUiEvent) {
        when (event) {
            is EditHoraExtraUiEvent.Load -> load(event.id)
            is EditHoraExtraUiEvent.EmpleadoChanged -> {
                val empleado = _state.value.empleados
                    .find { it.empleadoId == event.empleadoId }
                _state.update {
                    it.copy(
                        empleadoId = event.empleadoId,
                        empleadoNombre = empleado?.nombres ?: "",
                        empleadoError = null
                    )
                }
                recalcular()
            }
            is EditHoraExtraUiEvent.FechaDesdeChanged -> _state.update {
                it.copy(fechaDesde = event.value, fechaDesdeError = null)
            }
            is EditHoraExtraUiEvent.FechaHastaChanged -> _state.update {
                it.copy(fechaHasta = event.value, fechaHastaError = null)
            }
            is EditHoraExtraUiEvent.HorasTotalesChanged -> {
                val horas = event.value.toDoubleOrNull() ?: 0.0
                _state.update { it.copy(horasTotales = horas, horasError = null) }
                recalcular()
            }
            is EditHoraExtraUiEvent.HorasNocturnasChanged -> {
                val horas = event.value.toDoubleOrNull() ?: 0.0
                _state.update { it.copy(horasNocturnas = horas, horasError = null) }
                recalcular()
            }
            EditHoraExtraUiEvent.Save -> save()
            EditHoraExtraUiEvent.Delete -> delete()
        }
    }

    private fun recalcular() {
        val empleado = _state.value.empleados
            .find { it.empleadoId == _state.value.empleadoId }
        if (empleado != null && _state.value.horasTotales > 0) {
            val resultado = calcular(
                sueldoMensual = empleado.sueldo,
                horasTotales = _state.value.horasTotales,
                horasNocturnas = _state.value.horasNocturnas
            )
            _state.update {
                it.copy(
                    sueldoPorDia = resultado.sueldoPorDia,
                    sueldoPorHora = resultado.sueldoPorHora,
                    horasNormales = resultado.horasNormales,
                    horasExtrasTotales = resultado.horasExtrasTotales,
                    horasAl35 = resultado.horasAl35,
                    horasAl100 = resultado.horasAl100,
                    monto35 = resultado.monto35,
                    monto100 = resultado.monto100,
                    montoNocturno = resultado.montoNocturno,
                    totalAPagar = resultado.totalAPagar
                )
            }
        }
    }

    private fun load(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, horaExtraId = null) }
            return
        }
        viewModelScope.launch {
            getHoraExtra(id)?.let { h ->
                val empleado = _state.value.empleados
                    .find { it.empleadoId == h.empleadoId }
                _state.update {
                    it.copy(
                        isNew = false,
                        horaExtraId = h.horaExtraId,
                        empleadoId = h.empleadoId,
                        empleadoNombre = empleado?.nombres ?: "",
                        fechaDesde = h.fechaDesde,
                        fechaHasta = h.fechaHasta,
                        horasTotales = h.horasTotales,
                        horasNocturnas = h.horasNocturnas,
                        horasNormales = h.horasNormales,
                        horasAl35 = h.horasAl35,
                        horasAl100 = h.horasAl100,
                        monto35 = h.monto35,
                        monto100 = h.monto100,
                        montoNocturno = h.montoNocturno,
                        totalAPagar = h.totalAPagar
                    )
                }
                recalcular()
            }
        }
    }

    private fun save() {
        viewModelScope.launch {
            val v = validate(
                empleadoId = _state.value.empleadoId,
                fechaDesde = _state.value.fechaDesde,
                fechaHasta = _state.value.fechaHasta,
                horasTotales = _state.value.horasTotales,
                horasNocturnas = _state.value.horasNocturnas
            )
            if (!v.isValid) {
                _state.update {
                    it.copy(
                        empleadoError = v.empleadoError,
                        fechaDesdeError = v.fechaDesdeError,
                        fechaHastaError = v.fechaHastaError,
                        horasError = v.horasError
                    )
                }
                return@launch
            }
            _state.update { it.copy(isSaving = true) }
            try {
                upsertHoraExtra(
                    HoraExtra(
                        horaExtraId = _state.value.horaExtraId ?: 0,
                        empleadoId = _state.value.empleadoId ?: 0,
                        fechaDesde = _state.value.fechaDesde ?: 0L,
                        fechaHasta = _state.value.fechaHasta ?: 0L,
                        horasTotales = _state.value.horasTotales,
                        horasNocturnas = _state.value.horasNocturnas,
                        horasNormales = _state.value.horasNormales,
                        horasAl35 = _state.value.horasAl35,
                        horasAl100 = _state.value.horasAl100,
                        monto35 = _state.value.monto35,
                        monto100 = _state.value.monto100,
                        montoNocturno = _state.value.montoNocturno,
                        totalAPagar = _state.value.totalAPagar
                    )
                )
                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, empleadoError = e.message) }
            }
        }
    }

    private fun delete() {
        val id = _state.value.horaExtraId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            try {
                deleteHoraExtra(id)
                _state.update { it.copy(isDeleting = false, deleted = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isDeleting = false, empleadoError = e.message) }
            }
        }
    }
}