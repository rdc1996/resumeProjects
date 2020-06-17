package com.webcheckers.ui.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.AIPlayer;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.model.PlayerColor;
import com.webcheckers.ui.GetGameRoute;
import com.webcheckers.ui.TemplateEngineTester;
import com.webcheckers.viewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetGameRouteTest {

    private GetGameRoute CuT;
    private Request request;
    private Session session;
    private Response response;
    private TemplateEngine engine;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;
    private Player current;
    private Player other;

    @BeforeEach
    public void setUp() {
        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);

        current = new Player("Player1");
        other = new Player("Player2");
        gameLobby.addNewGame(current, other);
        playerLobby.addPlayer(current);
        playerLobby.addPlayer(other);

        when(session.attribute("currentUser")).thenReturn(current);
        when(request.queryParams("id")).thenReturn("Player2");
        CuT = new GetGameRoute(engine, playerLobby, gameLobby);
    }

    public void setUpBot() {
        gameLobby = new GameLobby();
        playerLobby = new PlayerLobby();
        request = mock(Request.class);
        session = mock(Session.class);
        when(request.session()).thenReturn(session);
        response = mock(Response.class);
        engine = mock(TemplateEngine.class);

        current = new Player("Player1");
        other = new AIPlayer();
        gameLobby.addNewGame(current, other);
        playerLobby.addPlayer(current);
        playerLobby.addPlayer(other);

        when(session.attribute("currentUser")).thenReturn(current);
        when(request.queryParams("id")).thenReturn("WhiteBot");
        CuT = new GetGameRoute(engine, playerLobby, gameLobby);
    }

    /**
     * Test that the Game view will create a new game if none exists.
     */
    @Test
    public void new_game() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        CuT.handle(request, response);

        testHelper.assertViewModelExists();
        testHelper.assertViewModelIsaMap();

        testHelper.assertViewModelAttribute(GetGameRoute.TITLE_ATTR, "Game");
        testHelper.assertViewModelAttribute("currentUser", current);
    }

    @Test
    public void receiveGame() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        gameLobby.addNewGame(other, current);
        current.setGameID(other.getName());
        other.setGameID(other.getName());
        current.setCurrentState(Player.currentState.CHALLENGE);
        other.setCurrentState(Player.currentState.INGAME);

        CuT.handle(request, response);

        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_ATTR, viewMode.PLAY);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER_ATTR, other);//center.getGameByID(current.getGameID()).getRedPlayer());
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER_ATTR, current);
        testHelper.assertViewModelAttribute(GetGameRoute.ACTIVE_COLOR_ATTR, "RED");
    }

    @Test
    public void playerChallengesPlayer() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        current.setCurrentState(Player.currentState.WAITING);
        other.setCurrentState(Player.currentState.WAITING);

        CuT.handle(request, response);

        assertTrue(current.getGameID().equals(current.getName()));
        assertTrue(other.getGameID().equals(current.getName()));
        assertTrue(gameLobby.getGame(current.getGameID()).getWhitePlayer() == other);

        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_ATTR, viewMode.PLAY);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER_ATTR, current);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER_ATTR, other);
        testHelper.assertViewModelAttribute(GetGameRoute.ACTIVE_COLOR_ATTR, "RED");
    }

    @Test
    public void inGameRed() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        gameLobby.addNewGame(current, other);
        current.setGameID(current.getName());
        other.setGameID(current.getName());
        current.setCurrentState(Player.currentState.INGAME);
        other.setCurrentState(Player.currentState.INGAME);

        CuT.handle(request, response);

        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_ATTR, viewMode.PLAY);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER_ATTR, current);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER_ATTR, other);
        testHelper.assertViewModelAttribute(GetGameRoute.ACTIVE_COLOR_ATTR, PlayerColor.RED);
    }

    @Test
    public void inGameWhite() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        gameLobby.addNewGame(other, current);
        current.setGameID(other.getName());
        other.setGameID(other.getName());
        current.setCurrentState(Player.currentState.INGAME);
        other.setCurrentState(Player.currentState.INGAME);

        CuT.handle(request, response);

        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_ATTR, viewMode.PLAY);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER_ATTR, other);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER_ATTR, current);
        testHelper.assertViewModelAttribute(GetGameRoute.ACTIVE_COLOR_ATTR, PlayerColor.RED);
    }

    @Test
    public void challengingBot() {
        setUpBot();

        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

        current.setCurrentState(Player.currentState.WAITING);
        other.setCurrentState(Player.currentState.WAITING);

        CuT.handle(request, response);

        assertTrue(current.getGameID().equals(current.getName()));
        assertTrue(other.getGameID().equals(current.getName()));
        assertTrue(gameLobby.getGame(current.getGameID()).getWhitePlayer() == other);

        testHelper.assertViewModelAttribute(GetGameRoute.VIEW_ATTR, viewMode.PLAY);
        testHelper.assertViewModelAttribute(GetGameRoute.RED_PLAYER_ATTR, current);
        testHelper.assertViewModelAttribute(GetGameRoute.WHITE_PLAYER_ATTR, other);
        testHelper.assertViewModelAttribute(GetGameRoute.ACTIVE_COLOR_ATTR, "RED");
    }

    @Test
    public void inGameWithBot() {
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        gameLobby.addNewGame(other, current);
        Game currentGame = gameLobby.getGame(current.getGameID());
        currentGame.setRedPiecesZero();
        current.setGameID(other.getName());
        other.setGameID(other.getName());
        current.setCurrentState(Player.currentState.INGAME);
        other.setCurrentState(Player.currentState.INGAME);

        CuT.handle(request, response);

        currentGame.setWhitePiecesZero();

        CuT.handle(request, response);
    }
}