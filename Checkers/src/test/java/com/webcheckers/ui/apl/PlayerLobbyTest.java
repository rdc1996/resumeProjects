package com.webcheckers.ui.apl;

import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerLobbyTest {
    private PlayerLobby CuT;
    private List<Player> players;


    @BeforeEach
    public void setup() {
        CuT = new PlayerLobby();
    }

    @Test
    public void makePlayerTest() {
        assertNotNull(CuT);
    }

    @Test
    public void playersTest() {
        Player player = new Player("John Doe");
        CuT.addPlayer(player);
        assertNotNull(CuT.getPlayers());
        assertEquals(player, CuT.getPlayers().get(0));
        assertTrue(CuT.containsPlayerName("John Doe"));
        assertEquals(player, CuT.getPlayer("John Doe"));
        assertEquals(1, CuT.numberOfPlayers());
        CuT.removePlayer(player);
        assertEquals(0, CuT.numberOfPlayers());
    }

    @Test
    public void hasSpecialCharacterTest() {
        assertTrue(CuT.hasSpecialCharacter("F*ck"));
        assertFalse(CuT.hasSpecialCharacter("Fick"));
    }

    @Test
    public void isJustSpacesTest() {
        assertTrue(CuT.isJustSpaces(" "));
        assertFalse(CuT.isJustSpaces("space"));
    }
}
