package com.webcheckers.ui.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Move;
import com.webcheckers.model.Player;
import com.webcheckers.model.Position;
import com.webcheckers.ui.PostBackupMove;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import spark.Session;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostBackupMoveRouteTest {

    private PostBackupMove CuT;
    private GameLobby gameLobby;
    private Game game;
    private Request request;
    private Response response;
    private Session session;

    @BeforeEach
    public void setUp() {
        gameLobby = new GameLobby();
        Player tempRed = new Player("Red");
        Player tempWhite = new Player("White");
        gameLobby.addNewGame(tempRed, tempWhite);
        game = gameLobby.getGame(tempRed.getGameID());

        request = mock(Request.class);
        response = mock(Response.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);

        when(session.attribute("currentUser")).thenReturn(tempRed);

        CuT = new PostBackupMove(gameLobby);
    }

    @Test
    public void success() {
        Move tempMove = new Move(new Position(5, 0), new Position(4, 1));
        game.pushMove(tempMove);

        CuT.handle(request, response);
    }

    @Test
    public void failure() {
        CuT.handle(request, response);
    }
}
