package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import javax.inject.Inject

class UpsertHoraExtraUseCase @Inject constructor(
    private val repository: HoraExtraRepository
) {
    suspend operator fun invoke(horaExtra: HoraExtra): Result<Int> {
        return try {
            val id = repository.upsert(horaExtra)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}