package com.danielblanco.movieinfo.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.danielblanco.movieinfo.model.Movie;
import com.danielblanco.movieinfo.service.MovieService;
import com.danielblanco.movieinfo.service.ViewService;
import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;
import com.slack.api.methods.response.views.ViewsOpenResponse;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState.Value;

@Configuration
public class SlackApp {
    private static final int SUGGESTION_LIMIT = 10;

    @Autowired
    private ViewService viewService;

    @Autowired
    private MovieService movieService;

    @Bean
    public App initSlackApp() {
        App app = new App();

        addOpenModalAction(app);
        addMovieExternalSelectAction(app);
        addMovieSubmissionHandler(app);

        return app;
    }

    private void addOpenModalAction(App app) {
        app.blockAction("open-movie-modal", (req, ctx) -> {
            View modal = viewService.buildMovieSelectModal();
            ViewsOpenResponse viewsOpenRes =
                    ctx.client().viewsOpen(r -> r.triggerId(ctx.getTriggerId()).view(modal));

            if (viewsOpenRes.isOk()) return ctx.ack();

            return Response.builder().statusCode(500).body(viewsOpenRes.getError()).build();
        });
    }

    private void addMovieExternalSelectAction(App app) {
        app.blockSuggestion("movie-external-select", (req, ctx) -> {
            String titleStart = req.getPayload().getValue();
            List<Movie> movies = movieService.getSuggestions(titleStart, SUGGESTION_LIMIT);
            return ctx.ack(viewService.buildSuggestionResponseFrom(movies));
        });
    }

    private void addMovieSubmissionHandler(App app) {
        app.viewSubmission("movie-modal-view", (req, ctx) -> {
            Value formValue = req.getPayload().getView().getState().getValues()
                    .get("movie-title-input").get("movie-external-select");
            Movie movie = movieService
                    .getMovieDetails(Long.valueOf(formValue.getSelectedOption().getValue()));

            if (movie != null) {
                app.client().chatPostMessage(
                        viewService.buildDirectMessageFrom(movie, ctx.getRequestUserId()));
                return ctx.ack();
            }

            return Response.builder().statusCode(500).body("Error loading movie details").build();
        });
    }
}
