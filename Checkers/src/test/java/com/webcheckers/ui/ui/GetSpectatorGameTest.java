package com.webcheckers.ui.ui;


import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.GetSpectatorGame;
import com.webcheckers.ui.TemplateEngineTester;
import com.webcheckers.viewMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import spark.Request;
import spark.Response;
import spark.Session;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class GetSpectatorGameTest {
    private GetSpectatorGame CuT;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;
    private TemplateEngine engine;
    private Request request;
    private Response response;
    private Player current;
    private Player other;

    @BeforeEach
    public void setup(){
        engine = mock(TemplateEngine.class);
        playerLobby = mock(PlayerLobby.class);
        gameLobby = mock(GameLobby.class);
        CuT = new GetSpectatorGame(engine, playerLobby,gameLobby);
        request = mock(Request.class);
        response = mock(Response.class);
        current = new Player("Player1");
        other = new Player("Player2");
    }

    @Test
    public void handleTest(){
        final TemplateEngineTester testHelper = new TemplateEngineTester();
        when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());
        try {
            CuT.handle(request, response);
        }catch(Exception e){
            System.out.println("Exception occurred");
        }
//        testHelper.assertViewModelAttribute(GetSpectatorGame.TITLE_ATTR,"Game");
//        testHelper.assertViewModelAttribute("currentUser",current);
//        testHelper.assertViewModelAttribute(GetSpectatorGame.VIEW_ATTR, viewMode.SPECTATOR);
//        testHelper.assertViewModelAttribute(GetSpectatorGame.RED_PLAYER_ATTR, other);
//        testHelper.assertViewModelAttribute(GetSpectatorGame.WHITE_PLAYER_ATTR, current);
//        testHelper.assertViewModelAttribute(GetSpectatorGame.ACTIVE_COLOR_ATTR,"RED");
//        testHelper.assertViewModelAttribute(GetSpectatorGame.BOARD_ATTR, gameLobby.getGame("Game").getBoard());
    }
}
