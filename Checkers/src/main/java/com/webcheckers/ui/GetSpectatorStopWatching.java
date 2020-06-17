package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.ui.GetGameRoute.CURRENT_USER_ATTR;

public class GetSpectatorStopWatching implements Route {

    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());
    private GameLobby gameLobby;
    private final TemplateEngine templateEngine;

    public GetSpectatorStopWatching(TemplateEngine templateEngine, GameLobby gameLobby) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.gameLobby = gameLobby;
        LOG.config("GetSpectatorStopWatching is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) {
        Map<String, Object> vm = new HashMap<>();
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);
        //set the user to waiting so he is no longer in a game
        currentPlayer.setCurrentState(Player.currentState.WAITING);
        vm.put("title", "Welcome!");
        response.redirect(WebServer.HOME_URL);
        return templateEngine.render(new ModelAndView(vm, "home.ftl"));
    }
}
