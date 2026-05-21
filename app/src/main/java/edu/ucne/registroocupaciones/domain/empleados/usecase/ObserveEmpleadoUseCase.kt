package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import javax.inject.Inject

class ObserveEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    operator fun invoke() = repository.observeEmpleados()
}