package com.example.trpmovie.apiServices

// ApiModule.kt

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiModule {
    // Base URL defined in the MovieApiService interface
    private const val BASE_URL = "https://movies-app-backend.replit.app/"

    // Lazy initialization of the Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Use Gson for converting JSON responses into Kotlin data classes
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Public property to expose the MovieApiService
    val movieApiService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }
}