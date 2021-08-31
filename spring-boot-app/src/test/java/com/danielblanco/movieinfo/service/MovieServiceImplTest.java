package com.danielblanco.movieinfo.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.danielblanco.movieinfo.client.MovieDBClient;
import com.danielblanco.movieinfo.model.Movie;
import com.danielblanco.movieinfo.model.MovieDictionary;

@ExtendWith(MockitoExtension.class)
public class MovieServiceImplTest {

    @Mock
    private MovieDBClient movieClient;

    @Mock
    private MovieDictionary movieDictionary;

    @InjectMocks
    private MovieServiceImpl movieServiceImpl;

    @Test
    public void givenMovieService_whenGetSuggestions_thenCallGetSuggestionsOnDictionary() {
        movieServiceImpl.getSuggestions("titleStart", 10);
        Mockito.verify(movieDictionary, Mockito.times(1)).getSuggestions("titleStart", 10);
    }

    @Test
    public void givenMovieService_whenGetMovieDetailsReturnsValidMovie_thenSetFullPosterPath() {
        String apiKey = System.getenv("THEMOVIEDB_API_KEY");
        Movie movie = new Movie(1l, "title");
        movie.setPosterPath("posterPath");
        Mockito.when(movieClient.getMovieDetails(1l, apiKey)).thenReturn(movie);

        Movie movieDetails = movieServiceImpl.getMovieDetails(1l);

        Mockito.verify(movieClient, Mockito.times(1)).getMovieDetails(1l, apiKey);
        assertEquals("https://image.tmdb.org/t/p/w200/posterPath", movieDetails.getPosterPath());
    }

    @Test
    public void givenMovieService_whenGetMovieDetailsReturnsNullMovie_thenReturnNull() {
        String apiKey = System.getenv("THEMOVIEDB_API_KEY");
        Mockito.when(movieClient.getMovieDetails(1l, apiKey)).thenReturn(null);

        Movie movieDetails = movieServiceImpl.getMovieDetails(1l);

        assertNull(movieDetails);
    }
}
