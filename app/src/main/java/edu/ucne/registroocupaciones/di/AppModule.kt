package edu.ucne.registroocupaciones.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.database.OcupacionDB
import edu.ucne.registroocupaciones.data.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.repository.EmpleadoRepositoryImpl
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOcupacionDB(@ApplicationContext context: Context): OcupacionDB =
        Room.databaseBuilder(
            context,
            OcupacionDB::class.java,
            "OcupacionDB"
        ).fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideOcupacionDao(db: OcupacionDB): OcupacionDao = db.ocupacionDao()

    @Provides
    @Singleton
    fun provideOcupacionRepositoryImpl(dao: OcupacionDao): OcupacionRepositoryImpl =
        OcupacionRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideOcupacionRepository(impl: OcupacionRepositoryImpl): OcupacionRepository = impl

    @Provides
    @Singleton
    fun provideEmpleadoDao(db: OcupacionDB): EmpleadoDao = db.empleadoDao()

    @Provides
    @Singleton
    fun provideEmpleadoRepositoryImpl(dao: EmpleadoDao): EmpleadoRepositoryImpl =
        EmpleadoRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideEmpleadoRepository(impl: EmpleadoRepositoryImpl): EmpleadoRepository = impl
}