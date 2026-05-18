package edu.ucne.registroocupaciones.data.empleado.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.entities.EmpleadoEntity
import edu.ucne.registroocupaciones.data.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EmpleadoRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: EmpleadoRepositoryImpl
    private lateinit var dao: EmpleadoDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = EmpleadoRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda empleado correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        val entitySlot = slot<EmpleadoEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } returns 1L

        // When
        val result = repository.upsert(empleado)

        // Then
        assertEquals(1, result)
        coVerify { dao.upsert(any()) }
        assertEquals(empleado.nombres, entitySlot.captured.nombres)
        assertEquals(empleado.sexo, entitySlot.captured.sexo)
        assertEquals(empleado.sueldo, entitySlot.captured.sueldo, 0.0)
    }

    @Test
    fun `upsert actualiza empleado correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez Actualizado",
            sexo = "Masculino",
            sueldo = 30000.0
        )
        coEvery { dao.upsert(any()) } returns 1L

        // When
        val result = repository.upsert(empleado)

        // Then
        assertEquals(1, result)
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `delete elimina empleado correctamente`() = runTest {
        // Given
        val empleadoId = 1
        coEvery { dao.deleteById(empleadoId) } just Runs

        // When
        repository.delete(empleadoId)

        // Then
        coVerify { dao.deleteById(empleadoId) }
    }

    @Test
    fun `observeEmpleados retorna flow de empleados`() = runTest {
        // Given
        val entities = listOf(
            EmpleadoEntity(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0),
            EmpleadoEntity(2, 1700000000000L, "Maria Garcia", "Femenino", 30000.0)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeEmpleados().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Juan Perez", result[0].nombres)
        assertEquals("Maria Garcia", result[1].nombres)
    }

    @Test
    fun `getEmpleado retorna empleado por id`() = runTest {
        // Given
        val entity = EmpleadoEntity(
            empleadoId = 1,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        coEvery { dao.getById(1) } returns entity

        // When
        val result = repository.getEmpleado(1)

        // Then
        assertNotNull(result)
        assertEquals("Juan Perez", result?.nombres)
        assertEquals("Masculino", result?.sexo)
        assertEquals(25000.0, result?.sueldo)
    }

    @Test
    fun `getByNombres retorna empleados con ese nombre`() = runTest {
        // Given
        val entities = listOf(
            EmpleadoEntity(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0)
        )
        coEvery { dao.getByNombres("Juan Perez") } returns entities

        // When
        val result = repository.getByNombres("Juan Perez")

        // Then
        assertEquals(1, result.size)
        assertEquals("Juan Perez", result[0].nombres)
    }

    @Test
    fun `getEmpleado retorna null si no existe`() = runTest {
        // Given
        coEvery { dao.getById(99) } returns null

        // When
        val result = repository.getEmpleado(99)

        // Then
        assertNull(result)
    }
}