package com.example.trpmovie.data

data class MovieListState(
    val genres: List<Genre> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val selectedGenre: String = "All movies", // Default state
    val isLoading: Boolean = false,
    val error: String? = null
)