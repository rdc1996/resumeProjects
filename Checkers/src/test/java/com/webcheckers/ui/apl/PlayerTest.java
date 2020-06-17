package com.webcheckers.ui.apl;

import com.webcheckers.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player CuT;
    //player tags
    private String name;

    @BeforeEach
    public void setup() {
        name = "John Doe";
        CuT = new Player(name);
    }

    @Test
    public void makePlayerTest() {
        assertNotNull(CuT);
    }

    @Test
    public void nameTest() {
        assertEquals(name, CuT.getName());
    }

    @Test
    public void currentStateTest() {
        CuT.setCurrentState(Player.currentState.WAITING);
        assertEquals(Player.currentState.WAITING, CuT.getCurrentState());

        CuT.setCurrentState(Player.currentState.INGAME);
        assertEquals(Player.currentState.INGAME, CuT.getCurrentState());

        CuT.setCurrentState(Player.currentState.CHALLENGE);
        assertEquals(Player.currentState.CHALLENGE, CuT.getCurrentState());
    }
}
