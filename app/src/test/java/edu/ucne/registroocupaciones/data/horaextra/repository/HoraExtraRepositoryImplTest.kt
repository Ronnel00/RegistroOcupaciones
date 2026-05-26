package edu.ucne.registroocupaciones.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import edu.ucne.registroocupaciones.data.local.dao.HoraExtraDao
import edu.ucne.registroocupaciones.data.local.entities.HoraExtraEntity
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class HoraExtraRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: HoraExtraRepositoryImpl
    private lateinit var dao: HoraExtraDao

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        repository = HoraExtraRepositoryImpl(dao)
    }

    @Test
    fun `upsert guarda hora extra nueva correctamente`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            horaExtraId = 0,
            empleadoId = 1,
            horasTotales = 54.0,
            totalAPagar = 2151.50
        )
        val entitySlot = slot<HoraExtraEntity>()
        coEvery { dao.upsert(capture(entitySlot)) } returns 1L

        // When
        val result = repository.upsert(horaExtra)

        // Then
        assertEquals(1, result)
        coVerify { dao.upsert(any()) }
        assertEquals(horaExtra.horasTotales, entitySlot.captured.horasTotales, 0.01)
    }

    @Test
    fun `upsert actualiza hora extra existente correctamente`() = runTest {
        // Given
        val horaExtra = HoraExtra(
            horaExtraId = 1,
            empleadoId = 1,
            horasTotales = 75.0,
            totalAPagar = 7394.78
        )
        coEvery { dao.upsert(any()) } returns 1L

        // When
        val result = repository.upsert(horaExtra)

        // Then
        assertEquals(1, result)
        coVerify { dao.upsert(any()) }
    }

    @Test
    fun `delete elimina hora extra correctamente`() = runTest {
        // Given
        coEvery { dao.deleteById(1) } just Runs

        // When
        repository.delete(1)

        // Then
        coVerify { dao.deleteById(1) }
    }

    @Test
    fun `observeHorasExtras retorna flow de horas extras`() = runTest {
        // Given
        val entities = listOf(
            HoraExtraEntity(1, 1, horasTotales = 54.0, totalAPagar = 2151.50),
            HoraExtraEntity(2, 2, horasTotales = 75.0, totalAPagar = 7394.78)
        )
        every { dao.observeAll() } returns flowOf(entities)

        // When
        val result = repository.observeHorasExtras().first()

        // Then
        assertEquals(2, result.size)
        assertEquals(54.0, result[0].horasTotales, 0.01)
        assertEquals(75.0, result[1].horasTotales, 0.01)
    }

    @Test
    fun `getHoraExtra retorna hora extra por id`() = runTest {
        // Given
        val entity = HoraExtraEntity(
            horaExtraId = 1,
            empleadoId = 1,
            horasTotales = 54.0,
            totalAPagar = 2151.50
        )
        coEvery { dao.getById(1) } returns entity

        // When
        val result = repository.getHoraExtra(1)

        // Then
        assertNotNull(result)
        assertEquals(54.0, result?.horasTotales)
        assertEquals(2151.50, result?.totalAPagar)
    }

    @Test
    fun `getHoraExtra retorna null cuando no existe`() = runTest {
        // Given
        coEvery { dao.getById(99) } returns null

        // When
        val result = repository.getHoraExtra(99)

        // Then
        assertNull(result)
    }
}