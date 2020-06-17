package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.TemplateEngine;
import static org.mockito.Mockito.mock;

public class WebserverTest {

    WebServer CuT;

    private TemplateEngine templateEngine;
    private Gson gson;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;

    /**
     * create mock object
     */
    @BeforeEach
    public void setup() {
        templateEngine = mock(TemplateEngine.class);
        gson = new Gson();
        playerLobby = new PlayerLobby();
        gameLobby = new GameLobby();

        CuT = new WebServer(templateEngine, gson, playerLobby, gameLobby);
    }

    @Test
    public void testInitialize(){
        CuT.initialize();
    }

}
