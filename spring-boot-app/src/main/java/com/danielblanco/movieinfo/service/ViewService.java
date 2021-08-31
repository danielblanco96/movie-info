package com.danielblanco.movieinfo.service;

import java.util.List;
import com.danielblanco.movieinfo.model.Movie;
import com.slack.api.app_backend.interactive_components.response.BlockSuggestionResponse;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.view.View;

public interface ViewService {

    View buildMovieSelectModal();

    BlockSuggestionResponse buildSuggestionResponseFrom(List<Movie> movies);

    ChatPostMessageRequest buildDirectMessageFrom(Movie movie, String userId);
}
