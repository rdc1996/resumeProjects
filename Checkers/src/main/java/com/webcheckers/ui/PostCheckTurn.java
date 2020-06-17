package com.webcheckers.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webcheckers.model.AIPlayer;
import com.webcheckers.model.Board.BoardView;
import com.webcheckers.model.Game;
import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Player;
import com.webcheckers.model.PlayerColor;
import com.webcheckers.application.Message;
import spark.*;
import java.util.logging.Logger;
import static com.webcheckers.ui.GetGameRoute.*;

public class PostCheckTurn implements Route {

    private GameLobby gameLobby;
    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());

    public PostCheckTurn(GameLobby gameLobby){
        this.gameLobby = gameLobby;
        LOG.config("PostCheckTurn has been initialized");
    }

   @Override
    public Object handle(Request request, Response response){
        LOG.config("CheckTurn was Invoked");
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);
        PlayerColor currentColor = currentPlayer.getColor();
        Game currentGame = gameLobby.getGame(currentPlayer.getGameID());
        //Player opponent = currentGame.getOpponent(currentPlayer);
        boolean isPlaying = currentGame.isCurrentPlayer(currentPlayer);

        Message message;
        Game.Win winner = currentGame.getWinner();
        if (winner == Game.Win.INGAME) {
            message = new Message(Boolean.toString(isPlaying), Message.Type.INFO);
        }
        else {
            if (winner == currentGame.playerColorToWinColor(currentColor)) {
                message = new Message("You win! Please redirect to the home page.", Message.Type.INFO);
            }
            else {
                message = new Message("You lose!", Message.Type.INFO);
            }
        }

        String temp;
        Gson gson = new GsonBuilder().create();
        temp = gson.toJson(message);
        return temp;
    }
}
