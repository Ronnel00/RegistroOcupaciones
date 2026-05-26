package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UpsertHoraExtraUseCaseTest {

    private lateinit var useCase: UpsertHoraExtraUseCase
    private lateinit var repository: HoraExtraRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = UpsertHoraExtraUseCase(repository)
    }

    @Test
    fun `invoke guarda hora extra correctamente`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            empleadoId = 1,
            fechaDesde = 1700000000000L,
            fechaHasta = 1700100000000L,
            horasTotales = 54.0,
            horasNocturnas = 0.0,
            totalAPagar = 2151.50
        )
        coEvery { repository.upsert(horaExtra) } returns 1

        // When
        val result = useCase(horaExtra)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull())
        coVerify { repository.upsert(horaExtra) }
    }

    @Test
    fun `invoke retorna failure cuando repositorio lanza excepcion`() = runTest {
        // Given
        val horaExtra = HoraExtra(empleadoId = 1, horasTotales = 54.0)
        coEvery { repository.upsert(horaExtra) } throws Exception("Error de base de datos")

        // When
        val result = useCase(horaExtra)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Error de base de datos", result.exceptionOrNull()?.message)
    }
}