package com.danielblanco.movieinfo.service;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.externalSelect;
import static com.slack.api.model.view.Views.view;
import static com.slack.api.model.view.Views.viewClose;
import static com.slack.api.model.view.Views.viewSubmit;
import static com.slack.api.model.view.Views.viewTitle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.danielblanco.movieinfo.model.Movie;
import com.slack.api.app_backend.interactive_components.response.BlockSuggestionResponse;
import com.slack.api.app_backend.interactive_components.response.Option;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.view.View;

@Service
public class ViewServiceImpl implements ViewService {

    @Override
    public View buildMovieSelectModal() {
        View modal = view(view -> view.callbackId("movie-modal-view").type("modal")
                .title(viewTitle(title -> title.type("plain_text").text("Movie Info")))
                .submit(viewSubmit(submit -> submit.type("plain_text").text("Submit")))
                .close(viewClose(close -> close.type("plain_text").text("Cancel")))
                .blocks(asBlocks(input(input -> input.blockId("movie-title-input")
                        .element(externalSelect(
                                es -> es.actionId("movie-external-select").minQueryLength(3)
                                        .placeholder(plainText(p -> p.text("Type the title...")))))
                        .label(plainText(pt -> pt.text("Type the title of the Movie:")))))));

        return modal;
    }

    @Override
    public BlockSuggestionResponse buildSuggestionResponseFrom(List<Movie> movies) {
        BlockSuggestionResponse response = new BlockSuggestionResponse();
        List<Option> options =
                movies.stream()
                        .map(m -> Option.builder().text(plainText(m.getTitle()))
                                .value(String.valueOf(m.getId())).build())
                        .collect(Collectors.toList());
        response.setOptions(options);
        return response;
    }

    @Override
    public ChatPostMessageRequest buildDirectMessageFrom(Movie movie, String userId) {
        String slackBotToken = System.getenv("SLACK_BOT_TOKEN");
        String movieDetailsJsonStr = buildMovieDetailsJsonStr(movie);
        return ChatPostMessageRequest.builder().token(slackBotToken).channel(userId)
                .blocksAsString(movieDetailsJsonStr).build();
    }

    private String buildMovieDetailsJsonStr(Movie movie) {
        String titleFormatted = movie.getTitle().replace("\"", "\\\"");
        String overviewFormatted = movie.getOverview().replace("\"", "\\\"");
        String releaseDateFormatted = getFormattedDate(movie.getReleaseDate());

        return "[\n" + "       {\n" + "            \"type\": \"section\",\n"
                + "         \"text\": {\n" + "              \"type\": \"plain_text\",\n"
                + "             \"text\": \"Here's the movie info you requested.\",\n"
                + "             \"emoji\": true\n" + "          }\n" + "        },\n" + "       {\n"
                + "         \"type\": \"header\",\n" + "           \"text\": {\n"
                + "             \"type\": \"plain_text\",\n" + "                \"text\": \""
                + titleFormatted + "\"\n" + "           }\n" + "        },\n" + "       {\n"
                + "         \"type\": \"section\",\n" + "           \"text\": {\n"
                + "             \"type\": \"mrkdwn\",\n"
                + "                \"text\": \"*Release date: " + releaseDateFormatted + "*\\n"
                + overviewFormatted + "\"\n" + "            },\n" + "         \"accessory\": {\n"
                + "             \"type\": \"image\",\n" + "             \"image_url\": \""
                + movie.getPosterPath() + "\",\n" + "             \"alt_text\": \"cute cat\"\n"
                + "           }\n" + "        }]\n";
    }

    private String getFormattedDate(LocalDate date) {
        if (date == null) return "-";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        return formatter.format(date);
    }

}
