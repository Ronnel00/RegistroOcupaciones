package edu.ucne.registroocupaciones.presentation.list

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.DeleteEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.list.ListEmpleadoViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ListEmpleadoViewModelTest {

    private lateinit var viewModel: ListEmpleadoViewModel
    private lateinit var observeEmpleado: ObserveEmpleadoUseCase
    private lateinit var deleteEmpleado: DeleteEmpleadoUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        observeEmpleado = mockk()
        deleteEmpleado = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga lista de empleados correctamente`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0),
            Empleado(2, 1700000000000L, "Maria Garcia", "Femenino", 30000.0)
        )
        every { observeEmpleado() } returns flowOf(empleados)

        // When
        viewModel = ListEmpleadoViewModel(observeEmpleado, deleteEmpleado)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(2, viewModel.state.value.empleados.size)
        Assert.assertEquals(false, viewModel.state.value.isLoading)
        Assert.assertEquals("Juan Perez", viewModel.state.value.empleados[0].nombres)
        Assert.assertEquals("Maria Garcia", viewModel.state.value.empleados[1].nombres)
    }

    @Test
    fun `init carga lista vacia correctamente`() = runTest {
        // Given
        every { observeEmpleado() } returns flowOf(emptyList())

        // When
        viewModel = ListEmpleadoViewModel(observeEmpleado, deleteEmpleado)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertTrue(viewModel.state.value.empleados.isEmpty())
        Assert.assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun `delete elimina empleado y muestra mensaje`() = runTest {
        // Given
        every { observeEmpleado() } returns flowOf(emptyList())
        coEvery { deleteEmpleado(1) } just Runs
        viewModel = ListEmpleadoViewModel(observeEmpleado, deleteEmpleado)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Delete(1))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { deleteEmpleado(1) }
        Assert.assertEquals("Empleado eliminado", viewModel.state.value.message)
    }

    @Test
    fun `delete muestra error cuando falla`() = runTest {
        // Given
        every { observeEmpleado() } returns flowOf(emptyList())
        coEvery { deleteEmpleado(99) } throws Exception("Empleado no encontrado")
        viewModel = ListEmpleadoViewModel(observeEmpleado, deleteEmpleado)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Delete(99))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals("Error: Empleado no encontrado", viewModel.state.value.message)
    }

    @Test
    fun `load recarga la lista de empleados`() = runTest {
        // Given
        val empleados = listOf(
            Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0)
        )
        every { observeEmpleado() } returns flowOf(empleados)
        viewModel = ListEmpleadoViewModel(observeEmpleado, deleteEmpleado)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(ListEmpleadoUiEvent.Load)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        Assert.assertEquals(1, viewModel.state.value.empleados.size)
        Assert.assertEquals("Juan Perez", viewModel.state.value.empleados[0].nombres)
    }
}