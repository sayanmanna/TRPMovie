package com.example.trpmovie

// MovieViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trpmovie.apiServices.MovieApiService
import com.example.trpmovie.data.Genre
import com.example.trpmovie.data.MovieListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Reuse MovieListState, Movie, and Genre models here

class MovieViewModel(private val api: MovieApiService) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state: StateFlow<MovieListState> = _state.asStateFlow()

    init {
        // Start fetching real data on initialization
        fetchGenres()
        fetchMovies(genre = null)
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                // 1. Fetch data from the real API
                val genresFromApi = api.getGenres()
                    .mapNotNull { tuple ->
                        // Safely parse the List<List<*>> format from the API
                        if (tuple.size == 2 && tuple[0] is String && tuple[1] is Number) {
                            Genre(tuple[0] as String, (tuple[1] as Number).toInt())
                        } else {
                            null
                        }
                    }

                // 2. Calculate total and prepend "All movies" option
                val allMoviesCount = genresFromApi.sumOf { it.count }
                val allGenres = listOf(Genre("All movies", allMoviesCount)) + genresFromApi

                _state.update { it.copy(genres = allGenres, isLoading = false) }

            } catch (e: Exception) {
                // Handle network or parsing errors
                _state.update { it.copy(error = "Failed to load genres: ${e.message}", isLoading = false) }
            }
        }
    }

    fun fetchMovies(genre: String?) {
        viewModelScope.launch {
            try {
                // Reset movies list and update selected genre
                _state.update { it.copy(
                    isLoading = true,
                    error = null,
                    selectedGenre = genre ?: "All movies",
                    movies = emptyList() // Clear list while loading new set
                ) }

                // Pass null to the API for "All movies" filter
                val genreQuery = if (genre == "All movies" || genre == null) null else genre

                // 3. Fetch movies with the genre filter
                val moviesList = api.getMovies(genre = genreQuery)

                _state.update { it.copy(movies = moviesList, isLoading = false) }

            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load movies: ${e.message}", isLoading = false) }
            }
        }
    }

    fun onGenreSelected(genreName: String) {
        if (genreName != _state.value.selectedGenre) {
            fetchMovies(genreName)
        }
    }
}