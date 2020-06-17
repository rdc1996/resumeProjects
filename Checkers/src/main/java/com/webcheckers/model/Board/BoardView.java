package com.webcheckers.model.Board;

import com.webcheckers.model.Position;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The data structure to represent the board
 */
public class BoardView implements Iterable{

    // The rows in the board starting from top to bottom
    private ArrayList<Row> rows;

    // The number of red pieces left on the board.
    private int redPieces;

    // The number of white pieces left on the board.
    private int whitePieces;

    public BoardView() {
        this.rows = new ArrayList<>();
        initBoard();
        updatePieces();
    }

    public BoardView(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public BoardView(BoardView lastBoard) {
        rows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            rows.add(new Row(i));
            Row lastRow = lastBoard.getRow(i);
            Row newRow = rows.get(i);
            for (int j = 0; j < 8; j++) {
                Space lastSpace = lastRow.getSpace(j);
                Space newSpace = new Space(lastSpace);
                newRow.addSpace(newSpace);
            }
        }
    }

    /**
     * Below are going to be all the preset Board views that we use for the demo
     * Depending on the name of the RedPlayer, a different board view will be used
     * when we initialize the game.
     *
     * Ready-Made-Boards:
     *      Ending a game
     *      Multiple Jump Moves
     *      King'ing a piece
     *      Normal Board to show AI play
     */
    public BoardView(String Demo, int boardPreset) {
        rows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Row newRow = new Row(i);
            rows.add(newRow);
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
                // If username is "1" the board will be set for the final move and win
                if(boardPreset == 1) {
                    if (i == 2 && j == 1) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                        }
                    }
                    if (i == 1 && j == 2) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                        }
                    }
                }
                // preset for losing, move piece so that White AI can capture
                else if (boardPreset == 2){
                    if (i == 5 && j == 4) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                        }
                    }
                    if (i == 3 && j == 2) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                        }
                    }
                }
                // multiple jump move board
                else if(boardPreset == 3){
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
                }
                // board set for kinging a piece and showing its movement, AI can do it as well
                else if(boardPreset == 4){
                    if (i == 1 && j == 2) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                        }
                    }
                    if (i == 6 && j == 5) {
                        if (tempSpace.getColor() == Space.COLOR.BLACK) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                        }
                    }
                }
                // the board will be normal if none of the presets are used
                else {
                    if (canHaveStarterPiece(i, j)) {
                        if (i < 3) {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                        }
                        else {
                            tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                        }
                    }
                }
                newRow.addSpace(tempSpace);
            }
        }
        updatePieces();
    }

    /**
     * Update the number of pieces on each team.
     *
     * Mainly used for testing but has some other applications.
     */
    public void updatePieces() {
        int tempWhite = 0;
        int tempRed = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Row tempRow = rows.get(i);
                Space tempSpace = tempRow.getSpace(j);
                Piece tempPiece = tempSpace.getPiece();
                if (tempSpace.getPiece() != null) {
                    if (tempPiece.getColor() == Piece.color.WHITE) {
                        tempWhite++;
                    }
                    else {
                        tempRed++;
                    }
                }
            }
        }
        whitePieces = tempWhite;
        redPieces = tempRed;
    }

    /**
     * Initialize the Board with the proper spaces and pieces.
     */
    public void initBoard() {
        for (int i = 0; i < 8; i++) {
            rows.add(new Row(i));
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
                Row currentRow = rows.get(i);
                if (canHaveStarterPiece(i, j)) {
                    if (i < 3) {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                    }
                    else {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                    }
                }
                currentRow.addSpace(tempSpace);
            }
        }
    }

    /**
     * Creates the white players view of the board, made for testing.
     */
    public void initWhiteBoard() {
        for (int i = 0; i < 8; i++) {
            rows.add(new Row(i));
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
                Row currentRow = rows.get(i);
                if (canHaveStarterPiece(i, j)) {
                    if (i < 3) {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.RED));
                    }
                    else {
                        tempSpace.givePiece(new Piece(Piece.type.SINGLE, Piece.color.WHITE));
                    }
                }
                currentRow.addSpace(tempSpace);
            }
        }
    }

    /**
     * Determine if the space at the given coordinates is supposed to have a piece at the start of a game.
     *
     * @param row the row of the given piece
     * @param col the column of the given piece
     * @return boolean representing if the piece should have a piece
     */
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

    /**
     * Obtain the view for the red player.
     *
     * @return the board representing the view for the red player
     */
    public BoardView getRedView() {
        return new BoardView(this.rows);
    }

    /**
     * Obtain the view for the white player by getting the inverse of the red player view.
     *
     * @return the board representing the view for the white player
     */
    public BoardView getWhiteView() {
        ArrayList<Row> newBoardRows = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Row newRow = new Row(i);
            newBoardRows.add(newRow);
            Row oldRow = rows.get(7 - i);
            for (int j = 0; j < 8; j++) {
                Space tempSpace = oldRow.duplicateSpace(7 - j);
                newRow.addSpace(tempSpace);
            }
        }
        return new BoardView(newBoardRows);
    }

    /**
     * Gets the row at the specified index.
     *
     * @param index the index of the row
     * @return the row at the given index
     */
    public Row getRow(int index) {
        return rows.get(index);
    }

    /**
     * Get the space at the given position.
     *
     * @param position the position of the space
     * @return the space at the position
     */
    public Space getSpace(Position position) {
        int row = position.getRow();
        int col = position.getCell();
        return rows.get(row).getSpace(col);
    }

    /**
     * Subtract the given number of white pieces from the board.
     *
     * @param subtract the number of pieces to remove
     */
    public void subtractWhitePieces(int subtract) {
        if (whitePieces >= subtract) {
            whitePieces -= subtract;
        }
        else {
            whitePieces = 0;
        }
    }

    /**
     * Subtract the given number of red pieces from the board.
     *
     * @param subtract the number of pieces to remove
     */
    public void subtractRedPieces(int subtract) {
        if (redPieces >= subtract) {
            redPieces -= subtract;
        }
        else {
            redPieces = 0;

        }
    }

    /**
     * Check if the red player has more than zero pieces.
     *
     * @return whether there are red pieces left
     */
    public boolean hasRedPiecesLeft() {
        return redPieces > 0;
    }

    /**
     * Check if the white player has more than zero pieces.
     *
     * @return whether there are white pieces left
     */
    public boolean hasWhitePiecesLeft() {
        return whitePieces > 0;
    }

    /**
     * Iterator the rows in the board.
     *
     * @return the iterator
     */
    @Override
    public Iterator<Row> iterator() {
        return new Iterator<Row>() {

            int current;

            @Override
            public boolean hasNext() {
                if (current < rows.size()) {
                    return true;
                }
                return false;
            }

            @Override
            public Row next() {
                Row tempRow = rows.get(current);
                current++;
                return tempRow;
            }
        };
    }
}