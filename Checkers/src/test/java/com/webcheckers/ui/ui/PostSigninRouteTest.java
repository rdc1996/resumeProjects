package com.webcheckers.ui.ui;

import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.ui.GetGameRoute;
import com.webcheckers.ui.PostSigninRoute;
import com.webcheckers.ui.TemplateEngineTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Tag("UI-Tier")
public class PostSigninRouteTest {
    private static final String VALID = "Username123";
    private static final String INVALID_SP_CH ="_Username!"; //invalid because of special characters
    private static final String INVALID_NOTHING = "";        // blank name was entered
    private static final String INVALID_SPACE = "    ";      // Username only contains spaces
    private static final String INVALID_DUP = "Username123"; // Username taken


    /**
     * components that will be tested
     */
    private PostSigninRoute CuT;
    private Request request;
    private Response response;
    private Session session;
    private TemplateEngine engine;
    private PlayerLobby playerLobby;


    /**
     * Setup new mock object for each test
     */
    @BeforeEach
    public void setup(){
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        engine = mock(TemplateEngine.class);
        response = mock(Response.class);
        playerLobby = new PlayerLobby();


        CuT = new PostSigninRoute(engine, playerLobby);
    }

    /**
     * Test that the signin action works for valid username
     */
    @Test
    public void setInvalidNothing(){
        //Arrange the test scenario: The user entered no name
        when(request.queryParams(PostSigninRoute.USERNAME)).thenReturn(INVALID_NOTHING);

        // To analyze what the Route created in the View-Model map you need
        // to be able to extract the argument to the TemplateEngine.render method.
        // Mock up the 'render' method by supplying a Mockito 'Answer' object
        // that captures the ModelAndView data passed to the template engine
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the results:
        //   * model is a non-null Map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        //correct message was shown
        testHelper.assertViewModelAttribute(PostSigninRoute.INVALID, "Enter a valid name");

        //user should be taken to signin page to try again
        testHelper.assertViewName("signin.ftl");
    }

    @Test
    public void setInvalidSpCh(){
        //Arrange the test scenario: The user entered special characters
        when(request.queryParams(PostSigninRoute.USERNAME)).thenReturn(INVALID_SP_CH);

        // To analyze what the Route created in the View-Model map you need
        // to be able to extract the argument to the TemplateEngine.render method.
        // Mock up the 'render' method by supplying a Mockito 'Answer' object
        // that captures the ModelAndView data passed to the template engine
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the results:
        //   * model is a non-null Map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(PostSigninRoute.INVALID, "Your username may not contain special characters.");
        testHelper.assertViewName("signin.ftl");

    }
    @Test
    public void setInvalidSpace(){
        //Arrange the test scenario: The user entered only spaces
        when(request.queryParams(PostSigninRoute.USERNAME)).thenReturn(INVALID_SPACE);

        // To analyze what the Route created in the View-Model map you need
        // to be able to extract the argument to the TemplateEngine.render method.
        // Mock up the 'render' method by supplying a Mockito 'Answer' object
        // that captures the ModelAndView data passed to the template engine
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // Analyze the results:
        //   * model is a non-null Map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(PostSigninRoute.INVALID, "Name must use alphanumeric characters.");
        testHelper.assertViewName("signin.ftl");

    }
    @Test
    public void setInvalidDup(){
        //adding original player
        Player player = new Player(INVALID_DUP);
        playerLobby.addPlayer(player);

        //Arrange the test scenario: The user entered a name that was already taken
        when(request.queryParams(PostSigninRoute.USERNAME)).thenReturn(INVALID_DUP);

        // To analyze what the Route created in the View-Model map you need
        // to be able to extract the argument to the TemplateEngine.render method.
        // Mock up the 'render' method by supplying a Mockito 'Answer' object
        // that captures the ModelAndView data passed to the template engine
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);


        // Analyze the results:
        //   * model is a non-null Map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(PostSigninRoute.INVALID, "That name is already in use, please pick another one.");
        testHelper.assertViewName("signin.ftl");
    }

    @Test
    public void setValid(){
        //Arrange the test scenario: username was valid
        when(request.queryParams(PostSigninRoute.USERNAME)).thenReturn(VALID);

        // To analyze what the Route created in the View-Model map you need
        // to be able to extract the argument to the TemplateEngine.render method.
        // Mock up the 'render' method by supplying a Mockito 'Answer' object
        // that captures the ModelAndView data passed to the template engine
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        // Invoke the test
        CuT.handle(request, response);

        // View model exists and is a map
        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        //check that player was put into playerLobby
        assert(playerLobby.containsPlayerName(VALID));

        // get the player the the lobby and make sure it is in the VMA
        Player newUser = playerLobby.getPlayer(VALID);
        testHelper.assertViewModelAttribute(GetGameRoute.CURRENT_USER_ATTR, newUser);
        //user was taken back to the home screen
        testHelper.assertViewName("home.ftl");
    }


}
