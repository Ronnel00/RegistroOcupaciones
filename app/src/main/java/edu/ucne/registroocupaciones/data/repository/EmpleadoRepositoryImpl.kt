package edu.ucne.registroocupaciones.data.repository

import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.mapper.toDomain
import edu.ucne.registroocupaciones.data.mapper.toEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmpleadoRepositoryImpl @Inject constructor(
    private val empleadoDao: EmpleadoDao
) : EmpleadoRepository {

    override fun observeEmpleados(): Flow<List<Empleado>> =
        empleadoDao.observeAll().map { it.map { entity -> entity.toDomain() } }

    override suspend fun getEmpleado(id: Int): Empleado? =
        empleadoDao.getById(id)?.toDomain()

    override suspend fun upsert(empleado: Empleado): Int {
        val result = empleadoDao.upsert(empleado.toEntity())
        return if (empleado.empleadoId == 0) result.toInt() else empleado.empleadoId
    }

    override suspend fun delete(id: Int) {
        empleadoDao.deleteById(id)
    }

    override suspend fun getByNombres(nombres: String): List<Empleado> =
        empleadoDao.getByNombres(nombres).map { it.toDomain() }
}