package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Inject

class ValidateOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    data class ValidationResult(
        val isValid: Boolean,
        val descripcionError: String? = null,
        val sueldoError: String? = null
    )

    suspend operator fun invoke(
        descripcion: String,
        sueldo: Double?,
        currentOcupacionId: Int? = null
    ): ValidationResult {

        val descripcionError = when {
            descripcion.isBlank() -> "La descripción es requerida"
            else -> {
                val existing = repository.getByDescripcion(descripcion)
                val isDuplicate = if (currentOcupacionId != null)
                    existing.any { it.ocupacionId != currentOcupacionId }
                else
                    existing.isNotEmpty()
                if (isDuplicate) "Ya existe una ocupación con esa descripción" else null
            }
        }

        val sueldoError = when {
            sueldo == null -> "El sueldo es requerido"
            sueldo <= 0 -> "El sueldo debe ser mayor a 0"
            else -> null
        }

        return ValidationResult(
            isValid = descripcionError == null && sueldoError == null,
            descripcionError = descripcionError,
            sueldoError = sueldoError
        )
    }
}