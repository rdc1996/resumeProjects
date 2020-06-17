package com.webcheckers.ui.apl;
import com.webcheckers.model.Position;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class PositionTest {
    private Position CuT;
    private int row;
    private int cell;

    @BeforeEach
    public void setup(){
        CuT = new Position(row,cell);
    }

   /* @Test
    public void testValidPositions(){
    } */
}
