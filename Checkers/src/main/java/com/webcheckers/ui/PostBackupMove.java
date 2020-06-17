package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.ui.GetGameRoute.CURRENT_USER_ATTR;


public class PostBackupMove implements Route {

    private GameLobby gameLobby;
    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());

    public PostBackupMove(GameLobby gameLobby){
        this.gameLobby = gameLobby;
        LOG.config("PostCheckTurn has been initialized");
    }


    @Override
    public Object handle(Request request, Response response) {
        LOG.config("CheckTurn was Invoked");
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);
        Game currentGame = gameLobby.getGame(currentPlayer.getGameID());
        if (currentGame.removeMove()){
            Message message;
            message = new Message("Backup Move Successful", Message.Type.INFO);
            String temp;
            Gson gson = new GsonBuilder().create();
            temp = gson.toJson(message);
            return temp;
        } else {
            Message message;
            message = new Message("Backup Move Unsuccessful", Message.Type.ERROR);
            String temp;
            Gson gson = new GsonBuilder().create();
            temp = gson.toJson(message);
            return temp;
        }

    }

}
