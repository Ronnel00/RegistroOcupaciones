package edu.ucne.registroocupaciones.presentation.horaextra.list

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra

data class ListHoraExtraUiState(
    val isLoading: Boolean = false,
    val horasExtras: List<HoraExtra> = emptyList(),
    val message: String? = null
)