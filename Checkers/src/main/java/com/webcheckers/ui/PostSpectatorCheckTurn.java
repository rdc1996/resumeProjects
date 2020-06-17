package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.model.PlayerColor;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.ui.GetGameRoute.*;

public class PostSpectatorCheckTurn implements Route {

    private GameLobby gameLobby;
    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());
    private PlayerColor lastColor;

    public PostSpectatorCheckTurn(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
        this.lastColor = PlayerColor.RED;
        LOG.config("PostSpectatorCheckTurn has been initialized");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.config("SpectatorCheckTurn was Invoked");
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);
        Game currentGame = gameLobby.getGame(currentPlayer.getRedPlayer().getGameID());
        PlayerColor currentColor = currentGame.getActiveColor();

        Message message;
        Game.Win winner = currentGame.getWinner();
        if (winner == Game.Win.INGAME) {
            if(lastColor == currentColor) {
                message = new Message(Boolean.toString(false), Message.Type.INFO);
            }
            else {
                message = new Message(Boolean.toString(true), Message.Type.INFO);
                lastColor = currentColor;
            }
        }
        else {
            message = new Message(Boolean.toString(true), Message.Type.INFO);
        }
        String temp;
        Gson gson = new GsonBuilder().create();
        temp = gson.toJson(message);
        return temp;
    }
}
