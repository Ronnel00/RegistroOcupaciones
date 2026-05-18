package edu.ucne.registroocupaciones.domain.empleado.usecase

import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteEmpleadoUseCaseTest {

    private lateinit var useCase: DeleteEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke elimina empleado correctamente`() = runTest {
        // Given
        val empleadoId = 1
        coEvery { repository.delete(empleadoId) } just Runs

        // When
        useCase(empleadoId)

        // Then
        coVerify { repository.delete(empleadoId) }
    }

    @Test
    fun `invoke falla cuando el repositorio lanza excepcion`() = runTest {
        // Given
        val empleadoId = 99
        coEvery { repository.delete(empleadoId) } throws Exception("Empleado no encontrado")

        // When - Then
        try {
            useCase(empleadoId)
            fail("Debió lanzar excepción")
        } catch (e: Exception) {
            assertEquals("Empleado no encontrado", e.message)
        }
    }
}