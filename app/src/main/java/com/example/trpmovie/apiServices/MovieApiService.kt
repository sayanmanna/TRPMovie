package com.example.trpmovie.apiServices

import com.example.trpmovie.data.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    // 1. Endpoint to get the list of all genres and their counts
    // The API returns an array of arrays, e.g., [["Action", 5291], ...]
    @GET("/api/genres")
    suspend fun getGenres(): List<List<*>> // Use List<List<*>> to handle the tuple-like structure

    // 2. Endpoint to get the list of movies
    @GET("/api/movies")
    suspend fun getMovies(
        // The API defaults to 500 if not specified, max 500
        @Query("limit") limit: Int = 500,
        // Used for pagination
        @Query("from") from: Int? = null,
        // Optional filter for a specific genre
        @Query("genre") genre: String? = null
    ): List<Movie>

}