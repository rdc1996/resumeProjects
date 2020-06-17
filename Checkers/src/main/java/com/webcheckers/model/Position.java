package com.webcheckers.model;

public class Position {

    // Row and column for the position on the board.
    private int row;
    private int cell;

    public Position(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    /**
     * Get the column of the position on the board.
     *
     * @return the column of the position
     */
    public int getCell() {
        return cell;
    }

    /**
     * Get the row of the position on the board.
     *
     * @return the row of the position
     */
    public int getRow() {
        return row;
    }
}
