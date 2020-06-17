package com.webcheckers.model.Board;

/**
 * Data structure representing the piece.
 */
public class Piece {

    // The different types of pieces that can be on the board
    public enum type {
        SINGLE, KING
    }

    // The colors that the piece can be
    public enum color {
        RED, WHITE
    }

    // The type of this piece
    private type myType;
    // The color of this piece
    private color myColor;

    private boolean beenJumped;

    public Piece(type myType, color myColor) {
        this.myType = myType;
        this.myColor = myColor;
        beenJumped = false;
    }

    /**
     * Get the type of this piece.
     *
     * @return the type of this piece
     */
    public type getType() {
        return myType;
    }

    /**
     * Get the color of this piece.
     *
     * @return the color of this piece
     */
    public color getColor() {
        return myColor;
    }

    public boolean isBeenJumped() {
        return beenJumped;
    }

    public void setBeenJumped(boolean beenJumped) {
        this.beenJumped = beenJumped;
    }

    public Piece copy() {
        return new Piece(myType, myColor);
    }
}
