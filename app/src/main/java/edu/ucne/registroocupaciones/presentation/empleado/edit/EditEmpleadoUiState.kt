package edu.ucne.registroocupaciones.presentation.empleado.edit

data class EditEmpleadoUiState(
    val empleadoId: Int? = null,
    val fechaIngreso: Long? = null,
    val nombres: String = "",
    val sexo: String = "",
    val sueldo: Double? = null,
    val nombresError: String? = null,
    val fechaIngresoError: String? = null,
    val sexoError: String? = null,
    val sueldoError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false
)