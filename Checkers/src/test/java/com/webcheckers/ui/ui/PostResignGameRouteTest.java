package com.webcheckers.ui.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.PostResignGameRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostResignGameRouteTest {

    private PostResignGameRoute CuT;

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
        gameLobby.addNewGame(current, opponent);

        gson = new Gson();
        response = mock(Response.class);
        request = mock(Request.class);
        session = mock(Session.class);

        when(request.session()).thenReturn(session);
        when(session.attribute("currentUser")).thenReturn(current);

        CuT = new PostResignGameRoute(gameLobby);
    }

    @Test
    public void handleTest(){
        String json = (String)CuT.handle(request, response);
        Message mess = gson.fromJson(json, Message.class);

        Message testMessage = new Message("You successfully resigned", Message.Type.INFO);

        assertEquals(mess.getText(), testMessage.getText());
    }
}
