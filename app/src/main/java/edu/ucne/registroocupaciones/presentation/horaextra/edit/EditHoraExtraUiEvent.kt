package edu.ucne.registroocupaciones.presentation.horaextra.edit

sealed interface EditHoraExtraUiEvent {
    data class Load(val id: Int?) : EditHoraExtraUiEvent
    data class EmpleadoChanged(val empleadoId: Int) : EditHoraExtraUiEvent
    data class FechaDesdeChanged(val value: Long?) : EditHoraExtraUiEvent
    data class FechaHastaChanged(val value: Long?) : EditHoraExtraUiEvent
    data class HorasTotalesChanged(val value: String) : EditHoraExtraUiEvent
    data class HorasNocturnasChanged(val value: String) : EditHoraExtraUiEvent
    data object Save : EditHoraExtraUiEvent
    data object Delete : EditHoraExtraUiEvent
}