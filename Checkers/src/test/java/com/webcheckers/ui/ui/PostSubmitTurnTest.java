package com.webcheckers.ui.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import com.webcheckers.model.Position;
import com.webcheckers.ui.GetGameRoute;
import com.webcheckers.ui.WebServer;
import spark.Request;
import spark.Response;
import java.util.logging.Logger;
import com.webcheckers.ui.PostSubmitTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostSubmitTurnTest {

    private PostSubmitTurn CuT;

    private GameLobby gameLobby;
    private PlayerLobby playerLobby;
    private Gson gson;
    private Response response;
    private Request request;
    private Session session;
    private Player current;
    private Player opponent;

    @BeforeEach
    public void setup(){
        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();

        current = new Player("current");
        opponent = new Player("opponent");

        gson = new Gson();
        response = mock(Response.class);
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        CuT = new PostSubmitTurn(gameLobby, gson);
    }

    @Test
    public void handleTest(){
        when(session.attribute("currentUser")).thenReturn(current);
        gameLobby.addNewGame(current, opponent);
        Game currentGame = gameLobby.getGame(current.getGameID());

        Move testMove = new Move(new Position(5, 0), new Position(4, 1));
        currentGame.pushMove(testMove);

        String json = (String)CuT.handle(request, response);
        Message testMessage = new Message("Moves have successfully been submitted.", Message.Type.INFO);

        assertEquals(gson.fromJson(json, Message.class).getText(), testMessage.getText());
    }


}
