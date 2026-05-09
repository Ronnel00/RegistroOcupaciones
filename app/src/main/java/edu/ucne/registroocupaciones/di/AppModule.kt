package edu.ucne.registroocupaciones.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.ucne.registroocupaciones.data.database.OcupacionDB
import edu.ucne.registroocupaciones.data.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.repository.OcupacionRepositoryImpl
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Singleton          // ← javax, NO jakarta

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
        ).fallbackToDestructiveMigration(false)
            .build()

    @Provides
    @Singleton
    fun provideOcupacionDao(db: OcupacionDB): OcupacionDao =
        db.ocupacionDao()

    @Provides
    @Singleton
    fun provideOcupacionRepositoryImpl(dao: OcupacionDao): OcupacionRepositoryImpl =
        OcupacionRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideOcupacionRepository(impl: OcupacionRepositoryImpl): OcupacionRepository =
        impl
}