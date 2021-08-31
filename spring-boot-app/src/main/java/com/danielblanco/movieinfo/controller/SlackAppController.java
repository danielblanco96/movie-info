package com.danielblanco.movieinfo.controller;

import javax.servlet.annotation.WebServlet;
import com.slack.api.bolt.App;
import com.slack.api.bolt.servlet.SlackAppServlet;

@WebServlet("/user-actions")
public class SlackAppController extends SlackAppServlet {
    public SlackAppController(App app) {
        super(app);
    }
}
