package com.example.giphywatcher.di

import com.example.giphywatcher.repositories.GiphyDataRepository
import com.example.giphywatcher.repositories.IGiphyDataRepository
import com.example.giphywatcher.requesters.GiphyDataRequester
import com.example.giphywatcher.requesters.IGiphyDataRequester
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppInterfacesModule {
    @Binds
    abstract fun bindGiphyDataRequester(impl: GiphyDataRequester): IGiphyDataRequester

    @Binds
    abstract fun bindGiphyDataRepository(impl: GiphyDataRepository): IGiphyDataRepository
}