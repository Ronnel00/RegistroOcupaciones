package edu.ucne.registroocupaciones.domain.horasextras.usecase

import javax.inject.Inject

class ValidateHoraExtraUseCase @Inject constructor() {

    data class ValidationResult(
        val isValid: Boolean,
        val empleadoError: String? = null,
        val fechaDesdeError: String? = null,
        val fechaHastaError: String? = null,
        val horasError: String? = null
    )

    operator fun invoke(
        empleadoId: Int?,
        fechaDesde: Long?,
        fechaHasta: Long?,
        horasTotales: Double,
        horasNocturnas: Double
    ): ValidationResult {

        val empleadoError = when {
            empleadoId == null || empleadoId == 0 -> "Debe seleccionar un empleado"
            else -> null
        }

        val fechaDesdeError = when {
            fechaDesde == null || fechaDesde == 0L -> "La fecha desde es requerida"
            else -> null
        }

        val fechaHastaError = when {
            fechaHasta == null || fechaHasta == 0L -> "La fecha hasta es requerida"
            fechaDesde != null && fechaHasta < fechaDesde ->
                "La fecha hasta debe ser mayor a la fecha desde"
            else -> null
        }

        val horasError = when {
            horasTotales <= 0 -> "Las horas totales deben ser mayor a 0"
            horasNocturnas < 0 -> "Las horas nocturnas no pueden ser negativas"
            horasNocturnas > horasTotales -> "Las horas nocturnas no pueden superar las horas totales"
            else -> null
        }

        return ValidationResult(
            isValid = empleadoError == null && fechaDesdeError == null &&
                    fechaHastaError == null && horasError == null,
            empleadoError = empleadoError,
            fechaDesdeError = fechaDesdeError,
            fechaHastaError = fechaHastaError,
            horasError = horasError
        )
    }
}