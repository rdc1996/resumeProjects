package com.webcheckers.ui.apl;

import com.webcheckers.model.Game;
import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class GameLobbyTest {
    private Player mockWP;
    private Player mockRP;
    private GameLobby CuT;
    private Game mockGame;

    @BeforeEach
    public void setup(){
        mockRP = mock(Player.class);
        mockWP = mock(Player.class);
        CuT = new GameLobby();
    }

    @Test
    public void makeGameLobbyTest(){
        assertNotNull(CuT);
    }

    @Test
    public void createGameTest(){
        //ensure that adding a game does so
        CuT.addNewGame(mockRP, mockWP);
        //Tests getGame
        mockGame = CuT.getGame(mockRP.getGameID());
        assertNotNull(mockGame);
    }

    @Test
    public void checkPlayerRoles(){
        CuT.addNewGame(mockRP, mockWP);
        mockGame = CuT.getGame(mockRP.getGameID());
        //Checks that the roles of player match their expected colors
        assertSame(mockGame.getRedPlayer(), mockRP, "Red Player was not given red role");
        assertSame(mockGame.getWhitePlayer(), mockWP, "White Player was not given white role");
    }

    @Test
    public void endGameTestWhiteWin(){
        CuT.addNewGame(mockRP, mockWP);
        mockGame = CuT.getGame(mockRP.getGameID());
        //Check when the red Player loses
        CuT.gameOver(mockGame, "testing");
        //game should have been removed from Map
        Game nullGame = CuT.getGame(mockRP.getGameID());
        assertNull(nullGame, "Game still exists");
        assertSame(mockGame.getWinner(), null, "Winner is not White");
    }

    @Test
    public void endGameTestRedWin(){
        CuT.addNewGame(mockRP, mockWP);
        mockGame = CuT.getGame(mockRP.getGameID());
        //Check when the red Player loses
        CuT.gameOver(mockGame, "testing");
        //game should have been removed from Map
        Game nullGame = CuT.getGame(mockRP.getGameID());
        assertNull(nullGame, "Game still exists");
        assertSame(mockGame.getWinner(), null, "Winner is not Red");
    }





}
