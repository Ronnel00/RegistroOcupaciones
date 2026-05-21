package edu.ucne.registroocupaciones.presentation.empleado.list

sealed interface ListEmpleadoUiEvent {
    data object Load : ListEmpleadoUiEvent
    data class Delete(val id: Int) : ListEmpleadoUiEvent
}