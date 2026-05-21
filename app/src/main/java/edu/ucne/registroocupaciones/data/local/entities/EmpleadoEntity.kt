package edu.ucne.registroocupaciones.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "empleados")
data class EmpleadoEntity(
    @PrimaryKey(autoGenerate = true)
    val empleadoId: Int = 0,
    val fechaIngreso: Long = 0L,
    val nombres: String = "",
    val sexo: String = "",
    val sueldo: Double = 0.0
)