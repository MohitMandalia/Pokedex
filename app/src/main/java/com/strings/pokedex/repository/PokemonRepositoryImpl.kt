package com.strings.pokedex.repository

import com.strings.pokedex.data.remote.PokeApi
import com.strings.pokedex.data.remote.responses.Pokemon
import com.strings.pokedex.data.remote.responses.PokemonList
import com.strings.pokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.Exception
import javax.inject.Inject

@ActivityScoped
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi
): PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit,offset)
        }catch (e: Exception){
            return Resource.Error("Unknown Error")
        }
        return Resource.Success(response)
    }

    override suspend fun getPokemonInfo(name: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(name)
        }catch (e: Exception){
            return Resource.Error("Unknown Error")
        }
        return Resource.Success(response)
    }
}