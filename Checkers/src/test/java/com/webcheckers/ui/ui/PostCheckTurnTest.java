package com.webcheckers.ui.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.model.PlayerColor;
import com.webcheckers.ui.PostCheckTurn;
import spark.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostCheckTurnTest {

    private PostCheckTurn CuT;

    private GameLobby gameLobby;
    private PlayerLobby playerLobby;
    private Request request;
    private Response response;
    private Session session;
    private Player current;
    private Player opponent;
    private Game currentGame;
    private Gson gson = new Gson();

    @BeforeEach
    public void setup(){
        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();

        current = new Player("current");
        current.setColor(PlayerColor.RED);
        opponent = new Player("opponent");
        opponent.setColor(PlayerColor.WHITE);

        gameLobby.addNewGame(current, opponent);
        currentGame = gameLobby.getGame(current.getGameID());

        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        when(session.attribute("currentUser")).thenReturn(current);

        CuT = new PostCheckTurn(gameLobby);
    }

    @Test
    public void inGame(){
        String message = (String) CuT.handle(request,response);
        Message mess = gson.fromJson(message, Message.class);

        assertEquals("true", mess.getText());
        assertEquals(Game.Win.INGAME, currentGame.getWinner());
    }

    @Test
    public void notInGameWinner() {
        currentGame.setWinner(Game.Win.RED);

        String message = (String) CuT.handle(request,response);
        Message mess = gson.fromJson(message, Message.class);

        assertEquals("You win! Please redirect to the home page.", mess.getText());
        assertEquals(Game.Win.RED, currentGame.getWinner());
    }

    @Test
    public void notInGameLoser() {
        currentGame.setWinner(Game.Win.WHITE);

        String message = (String) CuT.handle(request,response);
        Message mess = gson.fromJson(message, Message.class);

        assertEquals("You lose!", mess.getText());
        assertEquals(Game.Win.WHITE, currentGame.getWinner());
    }
}
