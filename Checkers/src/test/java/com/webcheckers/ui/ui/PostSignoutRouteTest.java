package com.webcheckers.ui.ui;

import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.ui.GetGameRoute;
import com.webcheckers.ui.PostSignoutRoute;
import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostSignoutRouteTest {
    private PostSignoutRoute CuT;
    private Request request;
    private Response response;
    private Session session;
    private TemplateEngine engine;
    private Player player;
    private PlayerLobby playerLobby;

    @BeforeEach
    public void setup() {
        //create mock request and session
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);

        //mock classes of what signout route modifies
        playerLobby = mock(PlayerLobby.class);
        player = mock(Player.class);
        //add player to lobby
        playerLobby.addPlayer(player);
        CuT = new PostSignoutRoute(playerLobby);
    }

    @Test
    public void handleTest(){
        CuT.handle(request, response);
        //check to see that player was removed from lobby
        assertFalse(playerLobby.containsPlayerName(player.getName()));
        //user is not signed in once returning to homescreen
        assertSame(session.attribute(GetGameRoute.CURRENT_USER_ATTR), null);
    }
}
