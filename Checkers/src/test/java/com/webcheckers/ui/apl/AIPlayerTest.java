package com.webcheckers.ui.apl;
import com.webcheckers.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class AIPlayerTest extends PlayerTest{
    private MoveValidator mv;
    private AIPlayer CuT;
    private Game currentGame;

   /* @BeforeEach
    public void setup(){
        //super();
    } */

@Test
public void turnTest(){
    mv = mock(MoveValidator.class);
    currentGame = mock(Game.class);
    mv = currentGame.getMoveValidator();
    Position start1 = new Position(4,7);
    Position end1 = new Position(5,8);
    Move temp = new Move(start1, end1);
    List <Move> validMoves = currentGame.getValidMoves();
    CuT = new AIPlayer();
    //test nullhood
//    if(!(mv.isValidJump(temp,currentGame.getBoard()))) {
//        assertNull(validMoves, currentGame.getRedPlayer().getName() + "has blocked all pieces");
//        assertEquals(currentGame.getWinner(),Game.Win.RED);
//        //assertEquals(currentGame.getLoser(),);
//    }
    //not null

}




















}
