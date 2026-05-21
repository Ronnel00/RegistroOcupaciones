package edu.ucne.registroocupaciones.domain.empleado.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ObserveEmpleadoUseCaseTest {

    private lateinit var useCase: ObserveEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = ObserveEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke retorna flow con lista de empleados`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0),
            Empleado(2, 1700000000000L, "Maria Garcia", "Femenino", 30000.0)
        )
        every { repository.observeEmpleados() } returns flowOf(empleados)

        // When
        val result = useCase().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Juan Perez", result[0].nombres)
        assertEquals("Maria Garcia", result[1].nombres)
        verify { repository.observeEmpleados() }
    }

    @Test
    fun `invoke retorna flow vacio cuando no hay empleados`() = runTest {
        // Given
        every { repository.observeEmpleados() } returns flowOf(emptyList())

        // When
        val result = useCase().first()

        // Then
        assertTrue(result.isEmpty())
        verify { repository.observeEmpleados() }
    }

    @Test
    fun `invoke retorna flow con un solo empleado`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0)
        )
        every { repository.observeEmpleados() } returns flowOf(empleados)

        // When
        val result = useCase().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Juan Perez", result[0].nombres)
        verify { repository.observeEmpleados() }
    }
}