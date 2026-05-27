package edu.ucne.registroocupaciones.presentation.horaextra.list

sealed interface ListHoraExtraUiEvent {
    data object Load : ListHoraExtraUiEvent
    data class Delete(val id: Int) : ListHoraExtraUiEvent
}