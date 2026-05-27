package edu.ucne.registroocupaciones.data.repository

import edu.ucne.registroocupaciones.data.local.dao.HoraExtraDao
import edu.ucne.registroocupaciones.data.mapper.toDomain
import edu.ucne.registroocupaciones.data.mapper.toEntity
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HoraExtraRepositoryImpl @Inject constructor(
    private val horaExtraDao: HoraExtraDao
) : HoraExtraRepository {

    override fun observeHorasExtras(): Flow<List<HoraExtra>> =
        horaExtraDao.observeAll().map { it.map { entity -> entity.toDomain() } }

    override fun observeByEmpleado(empleadoId: Int): Flow<List<HoraExtra>> =
        horaExtraDao.observeByEmpleado(empleadoId)
            .map { it.map { entity -> entity.toDomain() } }

    override suspend fun getHoraExtra(id: Int): HoraExtra? =
        horaExtraDao.getById(id)?.toDomain()

    override suspend fun upsert(horaExtra: HoraExtra): Int {
        val result = horaExtraDao.upsert(horaExtra.toEntity())
        return if (horaExtra.horaExtraId == 0) result.toInt() else horaExtra.horaExtraId
    }

    override suspend fun delete(id: Int) {
        horaExtraDao.deleteById(id)
    }
}