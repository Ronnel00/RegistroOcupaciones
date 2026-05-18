package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import javax.inject.Inject

class ValidateEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    data class ValidationResult(
        val isValid: Boolean,
        val nombresError: String? = null,
        val fechaIngresoError: String? = null,
        val sexoError: String? = null,
        val sueldoError: String? = null
    )

    suspend operator fun invoke(
        nombres: String,
        fechaIngreso: Long?,
        sexo: String,
        sueldo: Double?,
        currentEmpleadoId: Int? = null
    ): ValidationResult {

        val nombresError = when {
            nombres.isBlank() -> "El nombre es requerido"
            else -> {
                val existing = repository.getByNombres(nombres)
                val isDuplicate = if (currentEmpleadoId != null)
                    existing.any { it.empleadoId != currentEmpleadoId }
                else
                    existing.isNotEmpty()
                if (isDuplicate) "Ya existe un empleado con ese nombre" else null
            }
        }

        val fechaIngresoError = when {
            fechaIngreso == null || fechaIngreso == 0L -> "La fecha de ingreso es requerida"
            else -> null
        }

        val sexoError = when {
            sexo.isBlank() -> "El sexo es requerido"
            else -> null
        }

        val sueldoError = when {
            sueldo == null -> "El sueldo es requerido"
            sueldo <= 0 -> "El sueldo debe ser mayor a 0"
            else -> null
        }

        return ValidationResult(
            isValid = nombresError == null && fechaIngresoError == null &&
                    sexoError == null && sueldoError == null,
            nombresError = nombresError,
            fechaIngresoError = fechaIngresoError,
            sexoError = sexoError,
            sueldoError = sueldoError
        )
    }
}