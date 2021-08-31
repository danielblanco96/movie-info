package com.danielblanco.movieinfo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.danielblanco.movieinfo.model.Movie;

@FeignClient(name = "moviedb-client", url = "https://api.themoviedb.org/3")
public interface MovieDBClient {

    @RequestMapping(method = RequestMethod.GET, value = "/movie/{movie_id}?language=en-US")
    Movie getMovieDetails(@PathVariable("movie_id") Long movieId,
            @RequestParam("api_key") String apiKey);
}
