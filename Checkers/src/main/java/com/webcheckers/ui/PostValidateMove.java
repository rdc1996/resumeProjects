package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.model.*;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import spark.*;
import java.util.logging.Logger;

public class PostValidateMove implements Route {

    // Logger
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    // Attributes for PostValidateMove
    private GameLobby gameLobby;
    private Gson gson;
    public static final String MOVE =  "MOVE";


    public PostValidateMove(GameLobby gameLobby, Gson gson) {
        this.gameLobby = gameLobby;
        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("PostValidateMove is invoked.");

        String moveMade = request.queryParams("actionData");
        Move move = gson.fromJson(moveMade, Move.class);

        Session httpSession = request.session();

        Player currentPlayer = httpSession.attribute("currentUser");
        String gameId = currentPlayer.getGameID();
        Game currentGame = gameLobby.getGame(gameId);

        Message message;
        if (currentPlayer.getColor() == PlayerColor.WHITE) {
            move.flipMove();
        }

        message = currentGame.validateMove(move);
        if (message.getType() == Message.Type.INFO) {
            currentGame.pushMove(move);
        }
        String temp;
        temp = gson.toJson(message);
        return temp;
    }
}
