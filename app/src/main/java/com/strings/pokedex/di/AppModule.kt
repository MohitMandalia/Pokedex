package com.strings.pokedex.di

import com.strings.pokedex.data.remote.PokeApi
import com.strings.pokedex.repository.PokemonRepository
import com.strings.pokedex.repository.PokemonRepositoryImpl
import com.strings.pokedex.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePokeApi(): PokeApi{
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PokeApi::class.java)
    }

    @Provides
    @Singleton
    fun providePokemonRepository(api:PokeApi): PokemonRepository{
        return PokemonRepositoryImpl(api)
    }

}