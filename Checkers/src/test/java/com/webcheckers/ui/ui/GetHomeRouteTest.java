package com.webcheckers.ui.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.GetHomeRoute;
import com.webcheckers.ui.TemplateEngineTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.*;

import javax.swing.text.DefaultEditorKit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetHomeRouteTest {

    /**
     * The component-under-test (CuT).
     *
     * <p>
     * This is a stateless component so we only need one.
     */
    private GetHomeRoute CuT;

    // mock objects
    private Request request;
    private Session session;
    private TemplateEngine engine;
    private Response response;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;
    private Player current;

    /**
     * Setup new mock objects for each test.
     */
    @BeforeEach
    public void setup() {
        playerLobby = new PlayerLobby();
        gameLobby = new GameLobby();
        current = new Player("current");

        request = mock(Request.class);
        session = mock(Session.class);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);
        when(request.session()).thenReturn(session);
        when(session.attribute("currentUser")).thenReturn(current);

        CuT = new GetHomeRoute(engine, playerLobby, gameLobby);
    }

    /**
     * Test that CuT shows the Home view when the session is brand new.
     */
    @Test
    public void testBase() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute("title", "Welcome!");
        testHelper.assertViewModelAttribute("message", GetHomeRoute.WELCOME_MSG);
        testHelper.assertViewModelAttribute("currentUser", current);

        testHelper.assertViewName("home.ftl");
    }

    @Test
    public void moreThanOne() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        playerLobby.addPlayer(current);
        CuT.handle(request, response);

        testHelper.assertViewModelAttribute("playerNumber", "1");
    }

    @Test
    public void redirect() {
        current.setCurrentState(Player.currentState.CHALLENGE);

        CuT.handle(request, response);
    }
}
