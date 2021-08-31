package com.danielblanco.movieinfo.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MovieDictionaryTest {

    @Test
    public void givenDictionaryWithMovies_whenGetSuggestionsWithValidStart_thenReturnMoviesByLimit() {
        MovieDictionary dict = new MovieDictionary();
        dict.insert(new Movie(1l, "movie_1"));
        dict.insert(new Movie(2l, "movie_2"));
        dict.insert(new Movie(3l, "movie 3"));

        List<Movie> suggestions = dict.getSuggestions("movie_", 2);
        assertEquals(1l, suggestions.get(0).getId());
        assertEquals(2l, suggestions.get(1).getId());
        assertEquals("movie_1", suggestions.get(0).getTitle());
        assertEquals("movie_2", suggestions.get(1).getTitle());

        suggestions = dict.getSuggestions("movie_", 1);
        assertEquals(suggestions.size(), 1);
        assertEquals(1l, suggestions.get(0).getId());
        assertEquals("movie_1", suggestions.get(0).getTitle());
    }

    @Test
    public void givenDictionaryWithMovies_whenGetSuggestionsWithInValidStart_thenReturnEmptyList() {
        MovieDictionary dict = new MovieDictionary();
        dict.insert(new Movie(1l, "movie_1"));
        dict.insert(new Movie(2l, "movie_2"));
        dict.insert(new Movie(3l, "movie 3"));

        List<Movie> suggestions = dict.getSuggestions("aaa", 2);
        assertTrue(suggestions.isEmpty());
    }
}
