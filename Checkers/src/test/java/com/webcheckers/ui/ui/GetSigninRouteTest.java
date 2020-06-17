package com.webcheckers.ui.ui;


import com.webcheckers.ui.GetSigninRoute;
import com.webcheckers.ui.TemplateEngineTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("UI-Tier")
public class GetSigninRouteTest {

    /**
     * components that will be tested
     */
    private GetSigninRoute CuT;
    private Request request;
    private Response response;
    private Session session;
    private TemplateEngine engine;

    /**
     * Setup new mock objects for each test.
     */
    @BeforeEach
    public void setup() {
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);


        // create a unique CuT for each test
        CuT = new GetSigninRoute(engine);
    }

    @Test
    public void testSession() {
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
        //   * model contains all necessary View-Model data
        testHelper.assertViewModelAttribute("title", "Welcome!");
        testHelper.assertViewModelAttribute("message", GetSigninRoute.SIGNIN_MSG);
        //   * test view name
        testHelper.assertViewName("signin.ftl");
    }
}
