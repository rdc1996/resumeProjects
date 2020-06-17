package com.webcheckers.ui.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.GetGameRoute;
import com.webcheckers.ui.GetSpectatorStopWatching;
import com.webcheckers.ui.TemplateEngineTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetSpectatorStopWatchingTest {
    private GetSpectatorStopWatching CuT;
    private Request request;
    private Session session;
    private Response response;
    private TemplateEngine engine;
    private GameLobby gameLobby;
    private Player current;

    @BeforeEach
    public void setup(){
        gameLobby = new GameLobby();
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);
        current = new Player("player");

        when(session.attribute("currentUser")).thenReturn(current);
        CuT = new GetSpectatorStopWatching(engine, gameLobby);
    }

    @Test
    public void testHomeScreen(){
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        CuT.handle(request, response);
        testHelper.assertViewModelAttribute("title", "Welcome!");

    }

    @Test
    public void testUserStateIsWaiting(){
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);
        assert(current.getCurrentState().equals(Player.currentState.WAITING));
    }

}
