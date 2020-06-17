package com.webcheckers.ui.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.PostSpectatorCheckTurn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostSpectatorCheckTurnTest {

    private PostSpectatorCheckTurn CuT;
    private Request request;
    private Session session;
    private Response response;
    private GameLobby gameLobby;
    private PlayerLobby playerLobby;
    private Player current;
    private Player other;

    @BeforeEach
    public void setup() {
        //create mock request and session
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);

        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();
        current = new Player("Player1");
        other = new Player("Player2");
        gameLobby.addNewGame(current, other);
        playerLobby.addPlayer(current);
        playerLobby.addPlayer(other);


        CuT = new PostSpectatorCheckTurn(gameLobby);
    }

    @Test
    public void testHandle() {
        try {
            CuT.handle(request, response);
        }
        catch(java.lang.Exception exception) {
            return;
        }
    }
}
