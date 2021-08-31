package com.danielblanco.movieinfo.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import com.danielblanco.movieinfo.model.Movie;
import com.slack.api.app_backend.interactive_components.response.BlockSuggestionResponse;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;

public class ViewServiceImplTest {

    private ViewService viewService;

    public ViewServiceImplTest() {
        viewService = new ViewServiceImpl();
    }

    @Test
    public void givenViewService_whenBuildSuggestionResponseFromMovies_thenReturnResponse() {
        Movie firstMovie = new Movie(1l, "firstTitle");
        Movie secondMovie = new Movie(2l, "secondTitle");

        BlockSuggestionResponse response =
                viewService.buildSuggestionResponseFrom(Arrays.asList(firstMovie, secondMovie));

        assertEquals("1", response.getOptions().get(0).getValue());
        assertEquals("2", response.getOptions().get(1).getValue());
        assertEquals("firstTitle", response.getOptions().get(0).getText().getText());
        assertEquals("secondTitle", response.getOptions().get(1).getText().getText());
    }

    @Test
    public void givenViewService_whenBuildDirectMessageFrom_thenReturnChatPostMessageRequest() {
        String botToken = System.getenv("SLACK_BOT_TOKEN");
        Movie movie = new Movie(9876l, "movieTitle");
        movie.setOverview("This is the movie overview");
        movie.setPosterPath("posterPath");
        movie.setReleaseDate(LocalDate.of(2021, 8, 31));

        ChatPostMessageRequest req = viewService.buildDirectMessageFrom(movie, "userId");

        assertEquals(botToken, req.getToken());
        assertEquals("userId", req.getChannel());
        assertTrue(req.getBlocksAsString().contains("movieTitle"));
        assertTrue(req.getBlocksAsString().contains("This is the movie overview"));
        assertTrue(req.getBlocksAsString().contains("posterPath"));
        assertTrue(req.getBlocksAsString().contains("August 31, 2021"));
    }
}
