package edu.ucne.registroocupaciones.presentation.horaextra.edit

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado

data class EditHoraExtraUiState(
    val horaExtraId: Int? = null,
    val empleadoId: Int? = null,
    val empleadoNombre: String = "",
    val empleados: List<Empleado> = emptyList(),
    val fechaDesde: Long? = null,
    val fechaHasta: Long? = null,
    val horasTotales: Double = 0.0,
    val horasNocturnas: Double = 0.0,
    val sueldoPorDia: Double = 0.0,
    val sueldoPorHora: Double = 0.0,
    val horasNormales: Double = 0.0,
    val horasExtrasTotales: Double = 0.0,
    val horasAl35: Double = 0.0,
    val horasAl100: Double = 0.0,
    val monto35: Double = 0.0,
    val monto100: Double = 0.0,
    val montoNocturno: Double = 0.0,
    val totalAPagar: Double = 0.0,
    val empleadoError: String? = null,
    val fechaDesdeError: String? = null,
    val fechaHastaError: String? = null,
    val horasError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false
)