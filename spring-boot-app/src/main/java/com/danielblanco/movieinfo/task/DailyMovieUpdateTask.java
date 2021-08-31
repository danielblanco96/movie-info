package com.danielblanco.movieinfo.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.danielblanco.movieinfo.service.MovieService;

@Component
public class DailyMovieUpdateTask {
    @Autowired
    private MovieService movieService;

    @Scheduled(cron = "0 0 10 * * ?")
    public void scheduleFixedDelayTask() {
        movieService.updateMovieDictionary();
    }

    @EventListener
    public void on(ApplicationReadyEvent e) {
        movieService.updateMovieDictionary();
    }
}
