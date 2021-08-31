package com.danielblanco.movieinfo.service;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.danielblanco.movieinfo.client.MovieDBClient;
import com.danielblanco.movieinfo.model.Movie;
import com.danielblanco.movieinfo.model.MovieDictionary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MovieServiceImpl implements MovieService {
    private static final Logger log = LoggerFactory.getLogger(MovieServiceImpl.class);
    private static final String MOVIE_JSON_URL = "/tmp/movies.json";
    private static final String IMAGE_BASE_URI = "https://image.tmdb.org/t/p/w200/";

    @Autowired
    private MovieDBClient movieDBClient;

    @Autowired
    private MovieDictionary movieDictionary;

    @Override
    public void updateMovieDictionary() {
        try {
            downloadAndUncompressMovies();
            insertMoviesIntoDictionary();
        } catch (IOException e) {
            log.error("Error importing movies from themoviedb: " + e);
        }
    }

    private void downloadAndUncompressMovies() throws IOException {
        String date = buildFormattedDate();
        String movieExportLink = "http://files.tmdb.org/p/exports/movie_ids_" + date + ".json.gz";
        log.info("Starting movie import from themoviedb: " + movieExportLink);

        URL movieExportUrl = new URL(movieExportLink);
        HttpURLConnection conn = (HttpURLConnection) movieExportUrl.openConnection();
        GZIPInputStream gzipStream = new GZIPInputStream(conn.getInputStream());
        FileOutputStream outputStream = new FileOutputStream(MOVIE_JSON_URL, false);

        byte[] moviesInfo = new byte[1024];
        int readCount = gzipStream.read(moviesInfo);
        while (readCount != -1) {
            byte[] moviePart = Arrays.copyOf(moviesInfo, readCount);
            outputStream.write(moviePart);
            readCount = gzipStream.read(moviesInfo);
        }

        outputStream.flush();
        outputStream.close();
        gzipStream.close();
    }

    private String buildFormattedDate() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();

        if (now.getMonthValue() < 10) sb.append('0');
        sb.append(now.getMonthValue());
        sb.append("_");

        if (now.getDayOfMonth() < 10) sb.append('0');
        sb.append(now.getDayOfMonth());
        sb.append("_");

        sb.append(now.getYear());

        return sb.toString();
    }

    private void insertMoviesIntoDictionary() throws IOException {
        FileReader reader = new FileReader(MOVIE_JSON_URL);
        BufferedReader bufferedReader = new BufferedReader(reader);
        ObjectMapper mapper = new ObjectMapper();

        long importCount = 0;
        String currentLine;
        while ((currentLine = bufferedReader.readLine()) != null) {
            try {
                movieDictionary.insert(mapper.readValue(currentLine, Movie.class));
                importCount++;
            } catch (JsonProcessingException e) {
                log.error("Cannot import movie from record: " + currentLine + ": " + e);
            }
        }

        bufferedReader.close();
        log.info("Finished import. Imported " + importCount + " movies");
    }

    @Override
    public List<Movie> getSuggestions(String titleStart, int limit) {
        return movieDictionary.getSuggestions(titleStart, limit);
    }

    @Override
    public Movie getMovieDetails(Long movieId) {
        String apiKey = System.getenv("THEMOVIEDB_API_KEY");
        Movie movie = movieDBClient.getMovieDetails(movieId, apiKey);
        if (movie != null) movie.setPosterPath(IMAGE_BASE_URI + movie.getPosterPath());
        return movie;
    }
}
