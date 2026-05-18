package edu.ucne.registroocupaciones.domain.empleados.repository

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import kotlinx.coroutines.flow.Flow

interface EmpleadoRepository {
    fun observeEmpleados(): Flow<List<Empleado>>
    suspend fun getEmpleado(id: Int): Empleado?
    suspend fun upsert(empleado: Empleado): Int
    suspend fun delete(id: Int)
    suspend fun getByNombres(nombres: String): List<Empleado>
}