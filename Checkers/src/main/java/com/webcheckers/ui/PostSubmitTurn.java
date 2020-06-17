package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.model.Game;
import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Player;
import com.webcheckers.application.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

public class PostSubmitTurn implements Route {

    private GameLobby gameLobby;
    private Gson gson;
    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());

    public PostSubmitTurn(GameLobby gameLobby, Gson gson) {
        this.gameLobby = gameLobby;
        this.gson = gson;

        LOG.config("PostSubmitTurn has been initialized");
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("PostSubmitTurn has been invoked");

        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute("currentUser");
        String gameId = currentPlayer.getGameID();
        Game currentGame = gameLobby.getGame(gameId);
        Message submit = currentGame.submit();
        return gson.toJson(submit);
    }
}