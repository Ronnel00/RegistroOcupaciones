package edu.ucne.registroocupaciones.domain.empleados.model

import java.util.Date

data class Empleado(
    val empleadoId: Int = 0,
    val fechaIngreso: Long = 0L,
    val nombres: String = "",
    val sexo: String = "",
    val sueldo: Double = 0.0
)