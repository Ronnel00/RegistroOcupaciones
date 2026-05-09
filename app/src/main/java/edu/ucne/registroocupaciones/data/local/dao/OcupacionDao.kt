package edu.ucne.registroocupaciones.data.local.dao

import androidx.room.*
import edu.ucne.registroocupaciones.data.local.entities.OcupacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OcupacionDao {

    @Query("SELECT * FROM ocupaciones")
    fun observeAll(): Flow<List<OcupacionEntity>>

    @Query("SELECT * FROM ocupaciones WHERE ocupacionId = :id")
    suspend fun getById(id: Int): OcupacionEntity?

    @Upsert
    suspend fun upsert(ocupacion: OcupacionEntity): Long

    @Query("DELETE FROM ocupaciones WHERE ocupacionId = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ocupaciones WHERE descripcion = :descripcion")
    suspend fun getByDescripcion(descripcion: String): List<OcupacionEntity>
}