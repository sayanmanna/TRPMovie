package com.example.trpmovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trpmovie.apiServices.MovieApiService

class MovieViewModelFactory(
    private val apiService: MovieApiService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}