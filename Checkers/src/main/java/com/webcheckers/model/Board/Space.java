package com.webcheckers.model.Board;

/**
 * Data structure to represent the spaces in the board.
 */
public class Space {

    // The row that this space sits in
    private int myRow;
    // The column that this space sits in
    private int cellIdx;
    // The piece that is on this space, can be null
    private Piece myPiece;
    // The color of that space for testing.
    private COLOR color;

    // The type of color that spaces can be.
    public enum COLOR {
        WHITE, BLACK
    }

    public Space(int myRow, int cellIdx, COLOR color) {
        this.myRow = myRow;
        this.cellIdx = cellIdx;
        this.color = color;
    }

    public Space(int myRow, int cellIdx, COLOR color, Piece myPiece) {
        this.myRow = myRow;
        this.cellIdx = cellIdx;
        this.color = color;
        this.myPiece = myPiece;
    }

    public Space(Space copySpace) {
        myRow = copySpace.getMyRow();
        cellIdx = copySpace.getCellIdx();
        Piece copyPiece = copySpace.getPiece();
        if (copyPiece != null) {
            myPiece = copyPiece.copy();
        }
        else {
            myPiece = null;
        }
    }

    /**
     * Get the row that this space is in.
     *
     * @return the row number
     */
    public int getMyRow() {
        return myRow;
    }

    /**
     * Get the cell column of this space.
     *
     * @return the column as an integer
     */
    public int getCellIdx() {
        return cellIdx;
    }

    /**
     * Determines if this space is valid to move to given that there are no jumps possible.
     *
     * @return boolean representing if the space is valid to move to
     */
    public boolean isValid() {
        if (myPiece == null) {
            if (myRow%2 == 0) {
                if (cellIdx%2 == 0) {
                    return false;
                }
                else {
                    return true;
                }
            }
            else {
                if (cellIdx%2 == 0) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * If this space does not already have a piece, give it one.
     *
     * @param piece the piece being given to the space
     */
    public void givePiece(Piece piece) {
        this.myPiece = piece;
    }


    /**
     * Get the piece that is currently on this space.
     *
     * @return the piece that is on the space currently
     */
    public Piece getPiece() {
        return myPiece;
    }

    /**
     * Get the color of this space.
     *
     * @return the color of the space
     */
    public COLOR getColor() {
        return color;
    }
}
