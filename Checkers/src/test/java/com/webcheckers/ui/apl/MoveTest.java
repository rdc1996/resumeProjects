package com.webcheckers.ui.apl;

import com.webcheckers.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import com.webcheckers.model.Move;

public class MoveTest {

    private Position start1 = new Position(2, 2);
    private Position end1 = new Position(1, 3);
    private Move move1 = new Move(start1, end1);

    private Position start2 = new Position(5, 5);
    private Position end2 = new Position(4, 4);
    private Move move2 = new Move(start2, end2);

    @Test
    public void testGetStart() {
        assertEquals(move1.getStart(), start1);
        assertEquals(move2.getStart(), start2);
    }

    @Test
    public void testGetEnd() {
        assertEquals(move1.getEnd(), end1);
        assertEquals(move2.getEnd(), end2);
    }

    @Test
    public void testInverse() {
        move1.flipMove();
        assertEquals(move1.getStart().getRow(), 5);
        assertEquals(move1.getStart().getCell(), 5);
        assertEquals(move1.getEnd().getRow(), 6);
        assertEquals(move1.getEnd().getCell(), 4);
    }

}
