package com.example.giphywatcher.di

import android.content.Context
import com.example.giphywatcher.database.AppDataBase
import com.example.giphywatcher.database.dao.GiphyDataDao
import com.example.giphywatcher.database.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataBaseModule {
    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase {
        return AppDataBase.getInstance(context)
    }

    @Provides
    fun provideGiphyDataDao(appDataBase: AppDataBase): GiphyDataDao {
        return appDataBase.giphyDataDao()
    }

    @Provides
    fun provideRemoteKeysDao(appDataBase: AppDataBase): RemoteKeysDao {
        return appDataBase.remoteKeysDao()
    }
}