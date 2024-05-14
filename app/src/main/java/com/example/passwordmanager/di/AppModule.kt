package com.example.passwordmanager.di

import android.content.Context
import androidx.room.Room
import com.example.passwordmanager.data.PasswordDao
import com.example.passwordmanager.data.PasswordDatabase
import com.example.passwordmanager.data.repository.PasswordRepository
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
    fun provideDatabase(@ApplicationContext appContext: Context): PasswordDatabase {
        return Room.databaseBuilder(appContext, PasswordDatabase::class.java, "passwords_db").build()
    }

    @Provides
    fun providePasswordDao(database: PasswordDatabase): PasswordDao {
        return database.passwordDao()
    }

    @Provides
    @Singleton
    fun providePasswordRepository(passwordDao: PasswordDao, @ApplicationContext appContext: Context): PasswordRepository {
        return PasswordRepository(passwordDao, appContext)
    }
}