package edu.ucne.registroocupaciones.domain.horasextras.usecase

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ValidateHoraExtraUseCaseTest {

    private lateinit var useCase: ValidateHoraExtraUseCase

    @Before
    fun setup() {
        useCase = ValidateHoraExtraUseCase()
    }

    @Test
    fun `validacion exitosa con datos correctos`() {
        // When
        val result = useCase(
            empleadoId = 1,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 50.0,
            horasNocturnas = 5.0
        )

        // Then
        assertTrue(result.isValid)
        assertNull(result.empleadoError)
        assertNull(result.fechaDesdeError)
        assertNull(result.fechaHastaError)
        assertNull(result.horasError)
    }

    @Test
    fun `falla cuando empleado es null`() {
        // When
        val result = useCase(
            empleadoId = null,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 50.0,
            horasNocturnas = 0.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("Debe seleccionar un empleado", result.empleadoError)
    }

    @Test
    fun `falla cuando empleado es 0`() {
        // When
        val result = useCase(
            empleadoId = 0,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 50.0,
            horasNocturnas = 0.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("Debe seleccionar un empleado", result.empleadoError)
    }

    @Test
    fun `falla cuando fecha desde es null`() {
        // When
        val result = useCase(
            empleadoId = 1,
            fechaDesde = null,
            fechaHasta = 1700100000000L,
            horasTotales = 50.0,
            horasNocturnas = 0.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("La fecha desde es requerida", result.fechaDesdeError)
    }

    @Test
    fun `falla cuando fecha hasta es menor que fecha desde`() {
        // When
        val result = useCase(
            empleadoId = 1,
            fechaDesde = 1700100000000L,
            fechaHasta = 1700000000000L,
            horasTotales = 50.0,
            horasNocturnas = 0.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("La fecha hasta debe ser mayor a la fecha desde", result.fechaHastaError)
    }

    @Test
    fun `falla cuando horas totales son 0`() {
        // When
        val result = useCase(
            empleadoId = 1,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 0.0,
            horasNocturnas = 0.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("Las horas totales deben ser mayor a 0", result.horasError)
    }

    @Test
    fun `falla cuando horas nocturnas superan horas totales`() {
        // When
        val result = useCase(
            empleadoId = 1,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 10.0,
            horasNocturnas = 15.0
        )

        // Then
        assertFalse(result.isValid)
        assertEquals("Las horas nocturnas no pueden superar las horas totales", result.horasError)
    }
}