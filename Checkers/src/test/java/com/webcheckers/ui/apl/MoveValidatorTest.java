package com.webcheckers.ui.apl;

import com.webcheckers.model.Board.BoardView;
import com.webcheckers.model.Board.Piece;
import com.webcheckers.model.Board.Row;
import com.webcheckers.model.Board.Space;
import com.webcheckers.model.Move;
import com.webcheckers.model.MoveValidator;
import com.webcheckers.model.PlayerColor;
import com.webcheckers.model.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class MoveValidatorTest {
    private MoveValidator CuT;

    @BeforeEach
    public void setup() {
        BoardView board = new BoardView();
        CuT = new MoveValidator(board);
    }

    public BoardView setUpForKing() {
        ArrayList<Row> newRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            newRows.add(new Row(i));
            for (int j = 0; j < 8; j++) {
                Space tempSpace;
                if (i%2 == 0) {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.WHITE);
                    }
                    else {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                }
                else {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                    else {
                        tempSpace = new Space(i,j, Space.COLOR.WHITE);
                    }
                }
                Row currentRow = newRows.get(i);
                if (i == 3 && j == 2) {
                    tempSpace.givePiece(new Piece(Piece.type.KING, Piece.color.RED));
                }
                if (i == 4 && j == 3) {
                    tempSpace.givePiece(new Piece(Piece.type.KING, Piece.color.WHITE));
                }
                currentRow.addSpace(tempSpace);
            }
        }
        return new BoardView(newRows);
    }

    public BoardView setUpForMultipleJump() {
        ArrayList<Row> newRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            newRows.add(new Row(i));
            for (int j = 0; j < 8; j++) {
                Space tempSpace;
                if (i%2 == 0) {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.WHITE);
                    }
                    else {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                }
                else {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                    else {
                        tempSpace = new Space(i,j, Space.COLOR.WHITE);
                    }
                }
                Row currentRow = newRows.get(i);
                if (i == 7 && j == 2) {
                    if (tempSpace.getColor() == Space.COLOR.BLACK) {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                    }
                }
                if (i == 6 && j == 1 || i == 4 && j == 1 || i == 2 && j == 3) {
                    if (tempSpace.getColor() == Space.COLOR.BLACK) {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                    }
                }
                currentRow.addSpace(tempSpace);
            }
        }
        return new BoardView(newRows);
    }

    public BoardView setUpForJump() {
        ArrayList<Row> newRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            newRows.add(new Row(i));
            for (int j = 0; j < 8; j++) {
                Space tempSpace;
                if (i%2 == 0) {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.WHITE);
                    }
                    else {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                }
                else {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                    else {
                        tempSpace = new Space(i,j, Space.COLOR.WHITE);
                    }
                }
                Row currentRow = newRows.get(i);
                if (canHaveStarterPiece(i, j)) {
                    if (i < 3) {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                    }
                    else {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                    }
                    if ((i == 5 && j == 0) ||(i == 2 && j == 7)) {
                        tempSpace.givePiece(null);
                    }
                }
                if (i == 3 && j == 2) {
                    tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                }
                if (i == 4 && j == 5) {
                    tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                }
                currentRow.addSpace(tempSpace);
            }
        }
        return new BoardView(newRows);
    }

    public boolean canHaveStarterPiece(int row, int col) {
        if (row <= 2 || row >= 5) {
            if (row%2 == 0) {
                return (col%2 != 0);
            }
            else {
                return (col%2 == 0);
            }
        }
        return false;
    }

    @Test
    public void testGenerateMoves() {
        CuT = new MoveValidator(setUpForJump());
        CuT.generateAllMoves();
        assertFalse(CuT.getJumpMoves().isEmpty());
        assertTrue(CuT.getSimpleMoves().isEmpty());
        CuT = new MoveValidator(new BoardView());
        CuT.generateAllMoves();
        assertTrue(CuT.getJumpMoves().isEmpty());
        assertFalse(CuT.getSimpleMoves().isEmpty());
    }

    @Test
    public void testCheckMove() {
        BoardView temp = new BoardView();
        CuT = new MoveValidator(temp);
        CuT.generateSimpleMoves();
        Move nullStart = new Move(new Position(4, 1), new Position(3, 2));
        Move oppositeColor = new Move(new Position(2, 1), new Position(3, 2));
        Move occupiedEnd = new Move(new Position(6, 1), new Position(5, 2));
        Move validStep = new Move(new Position(5, 0), new Position(4, 1));
        //Move invalidStep = new Move(new Position(3, 2), new Position(4, 3));
        assertEquals(CuT.checkMove(nullStart, temp).getText(), "Start space is unoccupied!");
        assertEquals(CuT.checkMove(oppositeColor, temp).getText(), "Can't move your opponent's pieces!");
        assertEquals(CuT.checkMove(occupiedEnd, temp).getText(), "Can't move to an occupied space!");
        assertEquals(CuT.checkMove(validStep, temp).getText(), "Valid move!");
        //assertEquals(CuT.checkMove(invalidStep).getText(), "You did something wrong");

        temp = setUpForJump();
        CuT = new MoveValidator(temp);
        CuT.generateJumpMoves();

        Move jumpAvailable = new Move(new Position(5, 2), new Position(4, 1));
        Move validJump = new Move(new Position(5, 4), new Position(3, 6));
        Move overNothing = new Move(new Position(5, 2), new Position(3, 0));
        Move jumpOverOwnPiece = new Move(new Position(6, 3), new Position(4, 1));
        Move invalidJump = new Move(new Position(5, 4), new Position(2, 7));
        assertEquals(CuT.checkMove(jumpAvailable, temp).getText(), "There is a jump move available.");
        assertEquals(CuT.checkMove(validJump, temp).getText(), "Valid jump!");
        assertEquals(CuT.checkMove(overNothing, temp).getText(), "You can't jump over nothing!");
        assertEquals(CuT.checkMove(jumpOverOwnPiece, temp).getText(), "You can't jump over your own pieces!");
        assertEquals(CuT.checkMove(invalidJump, temp).getText(), "Invalid move");

        CuT.makeMove(validJump, setUpForJump());
        CuT.makeMove(validStep, temp);

        ArrayList<Row> emptyRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            emptyRows.add(new Row(i));
            Row tempRow = emptyRows.get(i);
            for (int j = 0; j < 8; j++) {
                Space tempSpace;
                if (i%2 == 0) {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.WHITE);
                    }
                    else {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                }
                else {
                    if (j%2 == 0) {
                        tempSpace = new Space(i, j, Space.COLOR.BLACK);
                    }
                    else {
                        tempSpace = new Space(i,j, Space.COLOR.WHITE);
                    }
                }
                if (i == 1 && j == 0) {
                    tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                }
                tempRow.addSpace(tempSpace);
            }
        }

        BoardView empty = new BoardView(emptyRows);
        CuT = new MoveValidator(empty);
        Move moveToKing = new Move(new Position(1, 0), new Position(0, 1));
        CuT.makeMove(moveToKing, empty);
    }

    @Test
    public void kingInBounds() {
        BoardView temp = setUpForKing();
        CuT = new MoveValidator(temp);
        Space redKing = temp.getSpace(new Position(3, 2));
        Space whiteKing = temp.getSpace(new Position(4, 3));

        assertTrue(CuT.pieceCanJump(redKing, temp));
        assertTrue(CuT.pieceCanJump(whiteKing, temp));
        assertTrue(CuT.pieceCanSimple(redKing));
        assertTrue(CuT.pieceCanSimple(whiteKing));
        assertTrue(CuT.inBounds(new Position(3, 2)));
        assertTrue(CuT.inBounds(new Position(4, 5)));
        assertFalse(CuT.inBounds(new Position(9, 6)));
    }

    @Test
    public void multipleJumps() {
        BoardView temp = setUpForMultipleJump();
        CuT = new MoveValidator(temp);

        Deque<Move> pendingMoves = new LinkedList<>();
        pendingMoves.offerLast(new Move(new Position(7, 2), new Position(5, 0)));
        assertTrue(CuT.hasAnotherJump(pendingMoves, temp));

        pendingMoves.offerLast(new Move(new Position(5, 0), new Position(3, 2)));
        assertTrue(CuT.hasAnotherJump(pendingMoves, temp));

        pendingMoves.offerLast(new Move(new Position(3, 2), new Position(1, 4)));
        assertFalse(CuT.hasAnotherJump(pendingMoves, temp));

        assertSame(2, CuT.findLongestJump());
    }

    @Test
    public void activeColors() {
        assertSame(PlayerColor.RED, CuT.getActiveColor());
        CuT.swtichActiveColor();
        assertSame(PlayerColor.WHITE, CuT.getActiveColor());

        assertSame(Piece.color.RED, CuT.playerColorToPieceColor(PlayerColor.RED));
        assertSame(Piece.color.WHITE, CuT.playerColorToPieceColor(PlayerColor.WHITE));
    }
}