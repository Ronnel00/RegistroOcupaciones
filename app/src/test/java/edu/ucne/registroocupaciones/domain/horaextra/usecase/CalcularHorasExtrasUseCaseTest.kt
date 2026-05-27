package edu.ucne.registroocupaciones.domain.horasextras.usecase

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CalcularHorasExtrasUseCaseTest {

    private lateinit var useCase: CalcularHorasExtrasUseCase

    @Before
    fun setup() {
        useCase = CalcularHorasExtrasUseCase()
    }

    @Test
    fun `caso 1 - solo horas normales sin extras`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 40.0
        val horasNocturnas = 0.0

        // When
        val resultado = useCase(sueldo, horasTotales, horasNocturnas)

        // Then
        assertEquals(40.0, resultado.horasNormales, 0.01)
        assertEquals(0.0, resultado.horasExtrasTotales, 0.01)
        assertEquals(0.0, resultado.horasAl35, 0.01)
        assertEquals(0.0, resultado.horasAl100, 0.01)
        assertEquals(0.0, resultado.montoNocturno, 0.01)
        assertEquals(0.0, resultado.totalAPagar, 0.01)
    }

    @Test
    fun `caso 2 - horas extras al 35 por ciento`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 54.0
        val horasNocturnas = 0.0

        // When
        val resultado = useCase(sueldo, horasTotales, horasNocturnas)

        // Then
        assertEquals(44.0, resultado.horasNormales, 0.01)
        assertEquals(10.0, resultado.horasExtrasTotales, 0.01)
        assertEquals(10.0, resultado.horasAl35, 0.01)
        assertEquals(0.0, resultado.horasAl100, 0.01)
        assertEquals(2151.50, resultado.totalAPagar, 1.0)
    }

    @Test
    fun `caso 3 - horas extras al 35 y al 100 por ciento`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 75.0
        val horasNocturnas = 0.0

        // When
        val resultado = useCase(sueldo, horasTotales, horasNocturnas)

        // Then
        assertEquals(44.0, resultado.horasNormales, 0.01)
        assertEquals(31.0, resultado.horasExtrasTotales, 0.01)
        assertEquals(24.0, resultado.horasAl35, 0.01)
        assertEquals(7.0, resultado.horasAl100, 0.01)
        assertTrue(resultado.totalAPagar > 0)
    }

    @Test
    fun `caso 4 - con horas nocturnas`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 54.0
        val horasNocturnas = 5.0

        // When
        val resultado = useCase(sueldo, horasTotales, horasNocturnas)

        // Then
        assertEquals(10.0, resultado.horasAl35, 0.01)
        assertTrue(resultado.montoNocturno > 0)
        assertEquals(2271.05, resultado.totalAPagar, 1.0)
    }

    @Test
    fun `sueldo por hora se calcula correctamente`() {
        // Given
        val sueldo = 30000.0

        // When
        val resultado = useCase(sueldo, 44.0, 0.0)

        // Then
        assertEquals(1274.97, resultado.sueldoPorDia, 1.0)
        assertEquals(159.37, resultado.sueldoPorHora, 1.0)
    }

    @Test
    fun `exactamente 44 horas no genera extras`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 44.0

        // When
        val resultado = useCase(sueldo, horasTotales, 0.0)

        // Then
        assertEquals(44.0, resultado.horasNormales, 0.01)
        assertEquals(0.0, resultado.horasExtrasTotales, 0.01)
        assertEquals(0.0, resultado.totalAPagar, 0.01)
    }

    @Test
    fun `exactamente 68 horas usa solo tope al 35`() {
        // Given
        val sueldo = 30000.0
        val horasTotales = 68.0

        // When
        val resultado = useCase(sueldo, horasTotales, 0.0)

        // Then
        assertEquals(24.0, resultado.horasAl35, 0.01)
        assertEquals(0.0, resultado.horasAl100, 0.01)
    }
}