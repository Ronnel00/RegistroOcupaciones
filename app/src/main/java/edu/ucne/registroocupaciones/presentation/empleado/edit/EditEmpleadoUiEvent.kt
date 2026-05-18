package edu.ucne.registroocupaciones.presentation.empleado.edit

sealed interface EditEmpleadoUiEvent {
    data class Load(val id: Int?) : EditEmpleadoUiEvent
    data class NombresChanged(val value: String) : EditEmpleadoUiEvent
    data class FechaIngresoChanged(val value: Long?) : EditEmpleadoUiEvent
    data class SexoChanged(val value: String) : EditEmpleadoUiEvent
    data class SueldoChanged(val value: String) : EditEmpleadoUiEvent
    data object Save : EditEmpleadoUiEvent
    data object Delete : EditEmpleadoUiEvent
}