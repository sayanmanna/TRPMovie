package com.example.trpmovie.data

// Data class for a single movie
data class Movie(
    val id: Int,
    val title: String,
    val genres: List<String>,
    // The API returns release_date as YYYY-MM-DD
    val release_date: String,
    val tagline: String,
    val overview: String,
    val url: String
) {
    // Helper property to extract the year for display
    val releaseYear: String
        get() = release_date.split("-").firstOrNull() ?: "N/A"
}

