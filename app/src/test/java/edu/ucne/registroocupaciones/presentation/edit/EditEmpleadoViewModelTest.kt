package edu.ucne.registroocupaciones.presentation.edit

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.usecase.*
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoUiEvent
import edu.ucne.registroocupaciones.presentation.empleado.edit.EditEmpleadoViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class EditEmpleadoViewModelTest {

    private lateinit var viewModel: EditEmpleadoViewModel
    private lateinit var getEmpleado: GetEmpleadoUseCase
    private lateinit var upsertEmpleado: UpsertEmpleadoUseCase
    private lateinit var deleteEmpleado: DeleteEmpleadoUseCase
    private lateinit var validate: ValidateEmpleadoUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getEmpleado = mockk()
        upsertEmpleado = mockk()
        deleteEmpleado = mockk()
        validate = mockk()
        viewModel = EditEmpleadoViewModel(getEmpleado, upsertEmpleado, deleteEmpleado, validate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load con id null inicializa estado nuevo`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Load(null))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isNew)
        assertNull(viewModel.state.value.empleadoId)
    }

    @Test
    fun `load con id 0 inicializa estado nuevo`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Load(0))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isNew)
    }

    @Test
    fun `load con id existente carga empleado correctamente`() = runTest {
        // Given
        val empleado = Empleado(
            empleadoId = 1,
            fechaIngreso = 1700000000000L,
            nombres = "Juan Perez",
            sexo = "Masculino",
            sueldo = 25000.0
        )
        coEvery { getEmpleado(1) } returns empleado

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Load(1))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.state.value.isNew)
        assertEquals(1, viewModel.state.value.empleadoId)
        assertEquals("Juan Perez", viewModel.state.value.nombres)
        assertEquals("Masculino", viewModel.state.value.sexo)
        assertEquals(25000.0, viewModel.state.value.sueldo)
    }

    @Test
    fun `NombresChanged actualiza nombres y limpia error`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.NombresChanged("Juan Perez"))

        // Then
        assertEquals("Juan Perez", viewModel.state.value.nombres)
        assertNull(viewModel.state.value.nombresError)
    }

    @Test
    fun `NombresChanged muestra error cuando esta vacio`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.NombresChanged(""))

        // Then
        assertEquals("El nombre es requerido", viewModel.state.value.nombresError)
    }

    @Test
    fun `FechaIngresoChanged actualiza fecha y limpia error`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.FechaIngresoChanged(1700000000000L))

        // Then
        assertEquals(1700000000000L, viewModel.state.value.fechaIngreso)
        assertNull(viewModel.state.value.fechaIngresoError)
    }

    @Test
    fun `FechaIngresoChanged muestra error cuando es null`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.FechaIngresoChanged(null))

        // Then
        assertEquals("La fecha es requerida", viewModel.state.value.fechaIngresoError)
    }

    @Test
    fun `SexoChanged actualiza sexo y limpia error`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.SexoChanged("Masculino"))

        // Then
        assertEquals("Masculino", viewModel.state.value.sexo)
        assertNull(viewModel.state.value.sexoError)
    }

    @Test
    fun `SueldoChanged actualiza sueldo correctamente`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.SueldoChanged("25000.0"))

        // Then
        assertEquals(25000.0, viewModel.state.value.sueldo)
        assertNull(viewModel.state.value.sueldoError)
    }

    @Test
    fun `SueldoChanged muestra error cuando es negativo`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.SueldoChanged("-100"))

        // Then
        assertEquals("El sueldo debe ser mayor a 0", viewModel.state.value.sueldoError)
    }

    @Test
    fun `SueldoChanged muestra error cuando esta vacio`() = runTest {
        // When
        viewModel.onEvent(EditEmpleadoUiEvent.SueldoChanged(""))

        // Then
        assertEquals("El sueldo es requerido", viewModel.state.value.sueldoError)
    }

    @Test
    fun `save guarda empleado correctamente cuando datos son validos`() = runTest {
        // Given
        val validationResult = ValidateEmpleadoUseCase.ValidationResult(isValid = true)
        coEvery {
            validate(any(), any(), any(), any(), any())
        } returns validationResult
        coEvery { upsertEmpleado(any()) } returns Result.success(1)

        viewModel.onEvent(EditEmpleadoUiEvent.NombresChanged("Juan Perez"))
        viewModel.onEvent(EditEmpleadoUiEvent.FechaIngresoChanged(1700000000000L))
        viewModel.onEvent(EditEmpleadoUiEvent.SexoChanged("Masculino"))
        viewModel.onEvent(EditEmpleadoUiEvent.SueldoChanged("25000.0"))

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.saved)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun `save muestra errores cuando validacion falla`() = runTest {
        // Given
        val validationResult = ValidateEmpleadoUseCase.ValidationResult(
            isValid = false,
            nombresError = "El nombre es requerido",
            fechaIngresoError = "La fecha es requerida",
            sexoError = "El sexo es requerido",
            sueldoError = "El sueldo es requerido"
        )
        coEvery {
            validate(any(), any(), any(), any(), any())
        } returns validationResult

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Save)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.saved)
        assertEquals("El nombre es requerido", viewModel.state.value.nombresError)
        assertEquals("La fecha es requerida", viewModel.state.value.fechaIngresoError)
        assertEquals("El sexo es requerido", viewModel.state.value.sexoError)
        assertEquals("El sueldo es requerido", viewModel.state.value.sueldoError)
    }

    @Test
    fun `delete elimina empleado correctamente`() = runTest {
        // Given
        coEvery { getEmpleado(1) } returns Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0)
        coEvery { deleteEmpleado(1) } just Runs

        viewModel.onEvent(EditEmpleadoUiEvent.Load(1))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Delete)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.deleted)
        assertFalse(viewModel.state.value.isDeleting)
        coVerify { deleteEmpleado(1) }
    }

    @Test
    fun `delete muestra error cuando falla`() = runTest {
        // Given
        coEvery { getEmpleado(1) } returns Empleado(1, 1700000000000L, "Juan Perez", "Masculino", 25000.0)
        coEvery { deleteEmpleado(1) } throws Exception("Error al eliminar")

        viewModel.onEvent(EditEmpleadoUiEvent.Load(1))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(EditEmpleadoUiEvent.Delete)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.deleted)
        assertEquals("Error al eliminar", viewModel.state.value.nombresError)
    }
}