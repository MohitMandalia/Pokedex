package com.strings.pokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.strings.pokedex.data.remote.responses.Pokemon
import com.strings.pokedex.repository.PokemonRepository
import com.strings.pokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon>{
        return repository.getPokemonInfo(pokemonName)
    }

}