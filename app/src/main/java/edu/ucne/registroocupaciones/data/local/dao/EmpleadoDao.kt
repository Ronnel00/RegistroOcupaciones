package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.*
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmpleadoDao {

    @Query("SELECT * FROM empleados")
    fun observeAll(): Flow<List<EmpleadoEntity>>

    @Query("SELECT * FROM empleados WHERE empleadoId = :id")
    suspend fun getById(id: Int): EmpleadoEntity?

    @Upsert
    suspend fun upsert(empleado: EmpleadoEntity): Long

    @Query("DELETE FROM empleados WHERE empleadoId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM empleados WHERE nombres = :nombres")
    suspend fun getByNombres(nombres: String): List<EmpleadoEntity>
}