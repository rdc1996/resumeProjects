package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.model.Game;
import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Player;
import com.webcheckers.application.Message;
import com.webcheckers.model.PlayerColor;
import spark.*;
import java.util.logging.Logger;
import static com.webcheckers.ui.GetGameRoute.CURRENT_USER_ATTR;

/**
 * This action tells the server that this player is resigning the game.
 * If it's this user's turn then the is only enabled in the Empty Turn state.
 * Once the user makes a valid move then this button is disabled.
 * The user may backup from the move to go back to the Empty Turn state,
 * which will reenable the Resign button.
 *
 * The response body must be a message that has type INFO if the action was successful or ERROR
 * if it was unsuccessful. When successful the client-side code will send the user back to the Home page.
 */
public class PostResignGameRoute implements Route {

    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());
    private GameLobby gameLobby;

    public PostResignGameRoute(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
        LOG.config("PostResigngame is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.config("ResignGame was Invoked");
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);
        Game currentGame = gameLobby.getGame(currentPlayer.getGameID());
//        Player opponent = currentGame.getOpponent(currentPlayer);
        gameLobby.resigned(currentGame, currentPlayer);
        //set the winner so that they get the win screen called by the checkTurnRoute
//        if (opponent.getColor().equals(PlayerColor.RED)) {
//            currentGame.setWinner(Game.Win.RED);
//        } else {
//            currentGame.setWinner(Game.Win.WHITE);
//        }
        Gson gson = new Gson();
        Message resignedMessage = new Message("You successfully resigned", Message.Type.INFO);
        return gson.toJson(resignedMessage);
    }
}
