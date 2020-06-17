package com.webcheckers.ui.apl.Board;

import static org.junit.jupiter.api.Assertions.*;
import com.webcheckers.model.Board.BoardView;
import com.webcheckers.model.Board.Piece;
import com.webcheckers.model.Board.Row;
import com.webcheckers.model.Board.Space;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class BoardViewTest {

    /**
     * The component-under-test (CuT).
     *
     * <p>
     * This is a stateless component so we only need one.
     */
    private BoardView CuT;

    // mock objects
    private ArrayList<Row> rows;

    /**
     * Setup new mock objects for each test.
     */
    @BeforeEach
    public void setup() {
        rows = new ArrayList<>();

        CuT = new BoardView(rows);
        CuT.initBoard();
    }

    @Test
    public void createBoard() {
        assertNotNull(rows);
        for (int i = 0; i < rows.size(); i++) {
            assertNotNull(rows.get(i));
        }
        CuT.iterator();
    }

    @Test
    public void whiteViewTest() {
        BoardView whiteView = new BoardView(new ArrayList<>());
        whiteView.initWhiteBoard();

        CuT = CuT.getWhiteView();

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).getSize(); j++) {
                Piece CuTPiece = rows.get(i).getSpace(j).getPiece();
                Piece whiteViewPiece = whiteView.getRow(i).getSpace(j).getPiece();
                Space CuTSPace = rows.get(i).getSpace(j);
                Space whiteViewSpace = whiteView.getRow(i).getSpace(j);
                if (CuTPiece != null) {
                    assertNotNull(whiteViewPiece);
                }
                else {
                    assertEquals(null, whiteViewPiece);
                }
                assertEquals(CuTSPace.getColor(), whiteViewSpace.getColor());
            }
        }
    }

    @Test
    public void redViewTest() {
        BoardView redView = new BoardView();

        CuT = CuT.getRedView();

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(i).getSize(); j++) {
                Piece CuTPiece = rows.get(i).getSpace(j).getPiece();
                Piece redViewPiece = redView.getRow(i).getSpace(j).getPiece();
                Space CuTSpace = rows.get(i).getSpace(j);
                Space redViewSpace = redView.getRow(i).getSpace(j);
                if (CuTPiece != null) {
                    assertNotNull(redViewPiece);
                }
                else {
                    assertEquals(null, redViewPiece);
                }
                assertEquals(CuTSpace.getColor(), redViewSpace.getColor());
            }
        }
    }

    @Test
    public void demoBoards() {
        CuT = new BoardView("String", 1);
        CuT = new BoardView("String", 2);
        CuT = new BoardView("String", 3);
        CuT = new BoardView("String", 4);
    }
}
