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
class GetHoraExtraUseCaseTest {

    private lateinit var useCase: GetHoraExtraUseCase
    private lateinit var repository: HoraExtraRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetHoraExtraUseCase(repository)
    }

    @Test
    fun `invoke retorna hora extra cuando existe`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            horaExtraId = 1,
            empleadoId = 1,
            horasTotales = 54.0,
            totalAPagar = 2151.50
        )
        coEvery { repository.getHoraExtra(1) } returns horaExtra

        // When
        val result = useCase(1)

        // Then
        assertNotNull(result)
        assertEquals(1, result?.horaExtraId)
        assertEquals(54.0, result?.horasTotales)
        coVerify { repository.getHoraExtra(1) }
    }

    @Test
    fun `invoke retorna null cuando no existe`() = runTest {
        // Given
        coEvery { repository.getHoraExtra(99) } returns null

        // When
        val result = useCase(99)

        // Then
        assertNull(result)
        coVerify { repository.getHoraExtra(99) }
    }
}