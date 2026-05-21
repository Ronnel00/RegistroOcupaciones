package edu.ucne.registroocupaciones.domain.empleado.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.empleados.usecase.GetEmpleadoUseCase
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetEmpleadoUseCaseTest {

    private lateinit var useCase: GetEmpleadoUseCase
    private lateinit var repository: EmpleadoRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEmpleadoUseCase(repository)
    }

    @Test
    fun `invoke retorna empleado cuando existe`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        coEvery { repository.getEmpleado(1) } returns empleado

        // When
        val result = useCase(1)

        // Then
        assertNotNull(result)
        assertEquals("Juan Perez", result?.nombres)
        assertEquals("Masculino", result?.sexo)
        assertEquals(25000.0, result?.sueldo)
        coVerify { repository.getEmpleado(1) }
    }

    @Test
    fun `invoke retorna null cuando no existe`() = runTest {
        // Given
        coEvery { repository.getEmpleado(99) } returns null

        // When
        val result = useCase(99)

        // Then
        assertNull(result)
        coVerify { repository.getEmpleado(99) }
    }
}