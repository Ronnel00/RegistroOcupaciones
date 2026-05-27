package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import javax.inject.Inject

class ObserveHorasExtrasUseCase @Inject constructor(
    private val repository: HoraExtraRepository
) {
    operator fun invoke() = repository.observeHorasExtras()
}