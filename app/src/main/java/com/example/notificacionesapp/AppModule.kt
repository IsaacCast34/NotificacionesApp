package com.example.notificacionesapp

import android.content.Context
import com.example.notificacionesapp.data.database.AppDatabase
import com.example.notificacionesapp.data.repository.NotaRepository
import com.example.notificacionesapp.data.dao.NotaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNotaDao(appDatabase: AppDatabase): NotaDao {
        return appDatabase.notaDao()
    }

    @Provides
    @Singleton
    fun provideNotaRepository(notaDao: NotaDao): NotaRepository {
        return NotaRepository(notaDao)
    }
}