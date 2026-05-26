package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DeleteHoraExtraUseCaseTest {

    private lateinit var useCase: DeleteHoraExtraUseCase
    private lateinit var repository: HoraExtraRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteHoraExtraUseCase(repository)
    }

    @Test
    fun `invoke elimina hora extra correctamente`() = runTest {
        // Given
        coEvery { repository.delete(1) } just Runs

        // When
        useCase(1)

        // Then
        coVerify { repository.delete(1) }
    }

    @Test
    fun `invoke falla cuando repositorio lanza excepcion`() = runTest {
        // Given
        coEvery { repository.delete(99) } throws Exception("No encontrado")

        // When - Then
        try {
            useCase(99)
            fail("Debió lanzar excepción")
        } catch (e: Exception) {
            assertEquals("No encontrado", e.message)
        }
    }
}