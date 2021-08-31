package com.danielblanco.movieinfo.service;

import java.util.List;
import com.danielblanco.movieinfo.model.Movie;

public interface MovieService {

    void updateMovieDictionary();

    List<Movie> getSuggestions(String titleStart, int limit);

    Movie getMovieDetails(Long movieId);
}
