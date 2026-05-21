package edu.ucne.registroocupaciones.domain.empleado.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.empleados.usecase.UpsertEmpleadoUseCase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UpsertEmpleadoUseCaseTest {

    private lateinit var useCase: UpsertEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpsertEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke guarda empleado nuevo correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        coEvery { repository.upsert(empleado) } returns 1

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { repository.upsert(empleado) }
    }

    @Test
    fun `invoke actualiza empleado existente correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez Actualizado",
            sexo = "Masculino",
            sueldo = 30000.0
        )
        coEvery { repository.upsert(empleado) } returns 1

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { repository.upsert(empleado) }
    }

    @Test
    fun `invoke retorna failure cuando el repositorio lanza excepcion`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 0,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        coEvery { repository.upsert(empleado) } throws Exception("Error de base de datos")

        // When
        val result = useCase(empleado)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error de base de datos", result.exceptionOrNull()?.message)
        coVerify { repository.upsert(empleado) }
    }
}