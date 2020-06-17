package com.webcheckers.model;

public class Move {

    // The start and end positions for the move being made.
    private Position start;
    private Position end;

    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get the start position for the move being made.
     *
     * @return the start position of the move
     */
    public Position getStart() {
        return start;
    }

    /**
     * Get the end position for the move being made.
     *
     * @return the end position of the move
     */
    public Position getEnd() {
        return end;
    }

    /**
     * Flips the move for the white player.
     */
    public void flipMove() {
        this.start = new Position(7 - this.getStart().getRow(), 7 - this.getStart().getCell());
        this.end = new Position(7 - this.getEnd().getRow(), 7 - this.getEnd().getCell());
    }
}
