package com.webcheckers.ui.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.*;
import com.webcheckers.ui.PostValidateMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostValidateMoveTest {

    private PostValidateMove CuT;

    private GameLobby gameLobby;
    private PlayerLobby playerLobby;
    private Gson gson;
    private Request request;
    private Response response;
    private Session session;
    private Player current;
    private Player opponent;
    private Move move;
    private Game currentGame;

    @BeforeEach
    public void setup(){
        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();
        current = new Player("current");
        opponent = new Player("opponent");
        current.setColor(PlayerColor.WHITE);
        opponent.setColor(PlayerColor.RED);
        gameLobby.addNewGame(current, opponent);

        gson = new Gson();

        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);

        when(request.session()).thenReturn(session);
        when(session.attribute("currentUser")).thenReturn(current);

        move = new Move(new Position(2, 1), new Position(3, 0));
        String moveString = gson.toJson(move);
        when(request.queryParams("actionData")).thenReturn(moveString);

        CuT = new PostValidateMove(gameLobby, gson);
    }

    @Test
    public void handleTest(){
        String message = (String) CuT.handle(request, response);
        Message mess = gson.fromJson(message, Message.class);

        assertEquals(mess.getText(), "Valid move!");
    }


}
