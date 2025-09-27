package com.example.trpmovie.ui

// MovieScreen.kt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.items
import com.example.trpmovie.MovieViewModel
import com.example.trpmovie.MovieViewModelFactory
import com.example.trpmovie.apiServices.ApiModule
import com.example.trpmovie.data.Genre
import com.example.trpmovie.data.Movie

// --- Main Screen Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen() {
    // 1. Setup the ViewModel Factory for DI
    val factory = MovieViewModelFactory(ApiModule.movieApiService)

    // 2. Instantiate the ViewModel using the factory
    val viewModel: MovieViewModel = viewModel(factory = factory)

    // 3. Collect the state to drive the UI
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TRP Movie Database") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Genre Filter Row
            GenreList(
                genres = state.genres,
                selectedGenre = state.selectedGenre,
                onGenreSelected = viewModel::onGenreSelected,
                // Disable clicking while a fetch is in progress
                isFilterClickable = !state.isLoading
            )

            Divider()

            // Main Content Area: Handle Loading, Error, and Success
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && state.movies.isEmpty() -> {
                        // Show a full-screen loading indicator for initial load
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.error != null -> {
                        Text(
                            text = "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                    else -> {
                        MovieList(movies = state.movies)
                    }
                }
            }
        }
    }
}

// --- Helper Composables ---

@Composable
fun GenreList(
    genres: List<Genre>,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    isFilterClickable: Boolean
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(genres, key = { it.name }) { genre ->
            val isSelected = genre.name == selectedGenre
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isFilterClickable) onGenreSelected(genre.name)
                },
                // Requirement: see the total number of movies in a particular genre in a parenthetical
                label = {
                    Text("${genre.name} (${genre.count})")
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(movies, key = { it.id }) { movie ->
            MovieCard(movie = movie)
            Spacer(modifier = Modifier.height(16.dp))
        }
        // NOTE: Here is where the LazyColumn would trigger a 'load more' event for pagination
    }
}

@Composable
fun MovieCard(movie: Movie) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Requirement: click on a movie's card and be taken to the movie's URL (IMDB)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(movie.url))
                context.startActivity(intent)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title and Year (Requirement)
            Text(
                text = "${movie.title} (${movie.releaseYear})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Overview (Requirement)
            Text(
                text = movie.overview.ifBlank { "No overview provided." },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Genres (Requirement)
            Text(
                text = "Genres: ${movie.genres.joinToString()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}