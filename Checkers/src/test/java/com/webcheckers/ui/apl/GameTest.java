package com.webcheckers.ui.apl;

import com.webcheckers.model.*;
import com.webcheckers.model.Board.BoardView;
import com.webcheckers.model.Board.Piece;
import com.webcheckers.model.Board.Row;
import com.webcheckers.model.Board.Space;
import com.webcheckers.model.PlayerColor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    private Game CuT;
    private BoardView board;

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

    @Test
    public void testPlayers () {
        Player redPlayer = new Player("human");
        AIPlayer whitePlayer = new AIPlayer();
        CuT = new Game(redPlayer, whitePlayer);

        CuT.setRedPlayer(redPlayer);
        CuT.setWhitePlayer(whitePlayer);

        assertSame(redPlayer, CuT.getRedPlayer());
        assertSame(whitePlayer, CuT.getWhitePlayer());

        assertFalse(CuT.isCurrentPlayer(redPlayer));
        assertFalse(CuT.isCurrentPlayer(whitePlayer));

        assertFalse(CuT.isBotPlayerTurn());
        CuT.switchActiveColor();
        CuT.pushMove(new Move(new Position(2, 1), new Position(3, 2)));

        assertTrue(CuT.removeMove());
        assertFalse(CuT.removeMove());

        CuT.switchActiveColor();

        assertSame(whitePlayer, CuT.getOpponent(redPlayer));
        assertSame(Game.Win.RED, CuT.playerColorToWinColor(PlayerColor.RED));
        assertSame(Game.Win.WHITE, CuT.playerColorToWinColor(PlayerColor.WHITE));

        CuT.resigned(whitePlayer);
        CuT.setLoser(whitePlayer);
        assertSame(whitePlayer, CuT.getLoser());
    }

    @Test
    public void testValidMoves() {
        Player redPlayer = new Player("human");
        AIPlayer whitePlayer = new AIPlayer();
        CuT = new Game(redPlayer, whitePlayer);

        CuT.setRedPlayer(redPlayer);
        CuT.setWhitePlayer(whitePlayer);

        MoveValidator temp = CuT.getMoveValidator();
        assertNotNull(temp);

        List<Move> tempMoves = CuT.getValidMoves();

        board = setUpForMultipleJump();
        CuT = new Game(redPlayer, whitePlayer, board);
        CuT.pushMove(new Move(new Position(7, 2), new Position(5, 0)));

        assertNotNull(CuT.generateMoreJumpe());

        board = setUpForKing();
        CuT = new Game(redPlayer, whitePlayer, board);
        CuT.pushMove(new Move(new Position(3, 2), new Position(5, 4)));

        assertNull(CuT.generateMoreJumpe());
    }

}
