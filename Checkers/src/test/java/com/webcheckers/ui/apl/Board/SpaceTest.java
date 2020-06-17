package com.webcheckers.ui.apl.Board;

import com.webcheckers.model.Board.Space;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpaceTest {

    private int row1 = 2;
    private int cell1 = 2;
    private Space space1 = new Space(row1, cell1, null);

    private int row2 = 2;
    private int cell2 = 3;
    private Space space2 = new Space(row2, cell2, null);

    private int row3 = 3;
    private int cell3 = 2;
    private Space space3 = new Space(row3, cell3, null);

    private int row4 = 3;
    private int cell4 = 3;
    private Space space4 = new Space(row4, cell4, null);

    @Test
    public void testValid() {
        assertFalse(space1.isValid());
        assertTrue(space2.isValid());
        assertTrue(space3.isValid());
        assertFalse(space4.isValid());
    }

}
