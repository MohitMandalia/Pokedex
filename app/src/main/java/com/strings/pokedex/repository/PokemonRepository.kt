package com.strings.pokedex.repository

import com.strings.pokedex.data.remote.responses.Pokemon
import com.strings.pokedex.data.remote.responses.PokemonList
import com.strings.pokedex.util.Resource

interface PokemonRepository {


    suspend fun getPokemonList(
       limit: Int,
       offset: Int
    ): Resource<PokemonList>


    suspend fun getPokemonInfo(
        name: String
    ): Resource<Pokemon>

}