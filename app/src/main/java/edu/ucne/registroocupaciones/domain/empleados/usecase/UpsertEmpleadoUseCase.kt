package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import javax.inject.Inject

class UpsertEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    suspend operator fun invoke(empleado: Empleado): Result<Int> {
        return try {
            val id = repository.upsert(empleado)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}