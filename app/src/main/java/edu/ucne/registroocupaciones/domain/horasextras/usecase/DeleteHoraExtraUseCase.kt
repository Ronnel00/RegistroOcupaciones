package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import javax.inject.Inject

class DeleteHoraExtraUseCase @Inject constructor(
    private val repository: HoraExtraRepository
) {
    suspend operator fun invoke(id: Int) = repository.delete(id)
}