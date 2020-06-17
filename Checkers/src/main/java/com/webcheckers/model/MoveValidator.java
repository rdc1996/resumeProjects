package com.webcheckers.model;

import com.webcheckers.model.Board.BoardView;
import com.webcheckers.model.Board.Piece;
import com.webcheckers.model.Board.Row;
import com.webcheckers.model.Board.Space;
import com.webcheckers.application.Message;

import java.lang.reflect.Array;
import java.util.*;

public class MoveValidator {

    // The board that represents the game.
    private BoardView board;

    // The list of simple moves that can be made.
    private List<Move> simpleMoves;

    // The list of jump moves that can be made.
    private List<Move> jumpMoves;

    // The color of the active player.
    private PlayerColor activeColor;

    public MoveValidator(BoardView board) {
        this.board = board;
        this.activeColor = PlayerColor.RED;
        this.simpleMoves = new ArrayList<>();
        this.jumpMoves = new ArrayList<>();
    }

    /**
     * Get the list of simple moves.
     *
     * @return the list of simple moves
     */
    public List<Move> getSimpleMoves() {
        return simpleMoves;
    }

    /**
     * Get the list of jump moves.
     *
     * @return the list of jump moves
     */
    public List<Move> getJumpMoves() {
        return jumpMoves;
    }

    /**
     * Determine whether the given position is inbounds.
     *
     * @param position the position being checked
     * @return boolean representing if the given position is in bounds
     */
    public boolean inBounds(Position position) {
        int row = position.getRow();
        int col = position.getCell();
        return (row < 8 && row >= 0 && col < 8 && col >= 0);
    }

    /**
     * Generate all of the moves that can be made.
     *
     * @return the list of moves
     */
    public List<Move> generateAllMoves() {
        simpleMoves.clear();
        jumpMoves.clear();
        generateJumpMoves();
        if (!jumpMoves.isEmpty()) {
            return jumpMoves;
        }
        generateSimpleMoves();
        if (simpleMoves.isEmpty()) {
            return null;
        }
        return simpleMoves;
    }

    /**
     * Check the list of simple moves for an equivalent move.
     *
     * @param move the move to check for in the simple moves
     * @return boolean representing if there is an equivalent move
     */
    public boolean checkEquivalentMoveSimple(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();
        int rowStart = start.getRow();
        int cellStart = start.getCell();
        int rowEnd = end.getRow();
        int cellEnd = end.getCell();
        for (Move m: simpleMoves) {
            int row1 = m.getStart().getRow();
            int cell1 = m.getStart().getCell();
            int row2 = m.getEnd().getRow();
            int cell2 = m.getEnd().getCell();
            if (rowStart == row1 && cellStart == cell1 && rowEnd == row2 && cellEnd == cell2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a piece can make a step move.
     *
     * @param space the space that is being checked
     * @return boolean representing if the piece can make a step move
     */
    public boolean pieceCanSimple(Space space) {
        Piece testPiece = space.getPiece();
        int row = space.getMyRow();
        int col = space.getCellIdx();
        Position startPosition = new Position(row, col);
        Position leftFrontPosition = new Position(row - 1, col - 1);
        Position rightFrontPosition = new Position(row - 1, col + 1);
        Position leftBackPosition = new Position(row + 1, col - 1);
        Position rightBackPosition = new Position(row + 1, col + 1);
        if (testPiece == null) {
            return false;
        }
        if (testPiece.getType() == Piece.type.SINGLE) {
            if (testPiece.getColor() == Piece.color.RED) {
                return ((inBounds(leftFrontPosition) && isValidSimple(new Move(startPosition, leftFrontPosition))) ||
                        (inBounds(rightFrontPosition) && isValidSimple(new Move(startPosition, rightFrontPosition))));
            }
            else {
                return ((inBounds(leftBackPosition) && isValidSimple(new Move(startPosition, leftBackPosition))) ||
                        (inBounds(rightBackPosition) && isValidSimple(new Move(startPosition, rightBackPosition))));
            }
        }
        else {
            return ((inBounds(leftFrontPosition) && isValidSimple(new Move(startPosition, leftFrontPosition))) ||
                    (inBounds(rightFrontPosition) && isValidSimple(new Move(startPosition, rightFrontPosition))) ||
                    (inBounds(leftBackPosition) && isValidSimple(new Move(startPosition, leftBackPosition))) ||
                    (inBounds(rightBackPosition) && isValidSimple(new Move(startPosition, rightBackPosition))));
        }
    }

    /**
     * Determines if a piece can jump
     *
     * @param space the space being checked
     * @param board the board that the space is on
     * @return boolean representation of if the piece can jump
     */
    public boolean pieceCanJump(Space space, BoardView board) {
        Piece testPiece = space.getPiece();
        int row = space.getMyRow();
        int col = space.getCellIdx();
        Position startPosition = new Position(row, col);
        Position leftFrontJumpPos = new Position(row - 2, col - 2);
        Position rightFrontJumpPos = new Position(row - 2, col + 2);
        Position leftBackJumpPos = new Position(row + 2, col - 2);
        Position rightBackJumpPos = new Position(row + 2, col + 2);
        if (testPiece == null) {
            return false;
        }
        if (testPiece.getType() == Piece.type.SINGLE) {
            if (testPiece.getColor() == Piece.color.RED) {
                return ((inBounds(leftFrontJumpPos) && isValidJump(new Move(startPosition, leftFrontJumpPos), board)) ||
                        (inBounds(rightFrontJumpPos) && isValidJump(new Move(startPosition, rightFrontJumpPos), board)));
            }
            else {
                return ((inBounds(leftBackJumpPos) && isValidJump(new Move(startPosition, leftBackJumpPos), board)) ||
                        (inBounds(rightBackJumpPos) && isValidJump(new Move(startPosition, rightBackJumpPos), board)));
            }
        }
        else {
            return ((inBounds(leftFrontJumpPos) && isValidJump(new Move(startPosition, leftFrontJumpPos), board)) ||
                    (inBounds(rightFrontJumpPos) && isValidJump(new Move(startPosition, rightFrontJumpPos), board)) ||
                    (inBounds(leftBackJumpPos) && isValidJump(new Move(startPosition, leftBackJumpPos), board)) ||
                    (inBounds(rightBackJumpPos) && isValidJump(new Move(startPosition, rightBackJumpPos), board)));
        }
    }

    /**
     * Determines if the given color can make a jump move.
     *
     * @param color the player color being checked
     * @return boolean representation of if the given color can jump
     */
    public boolean hasJumpMove(PlayerColor color) {
        Piece.color playerColor = playerColorToPieceColor(color);
        for (int i = 7; i >= 0; i--) {
            Row checkRow = board.getRow(i);
            for (int j = 0; j < 8; j++) {
                Space checkSpace = checkRow.getSpace(j);
                Piece testPiece = checkSpace.getPiece();
                if (testPiece != null) {
                    if (pieceCanJump(checkSpace, board) && testPiece.getColor() == playerColor && !testPiece.isBeenJumped()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if there is another jump move after all of the pending moves have been applied.
     *
     * @param pendingMoves the moves that need to be applied
     * @return boolean representation of if there is another jump move
     */
    public boolean hasAnotherJump(Deque<Move> pendingMoves, BoardView board) {
        BoardView tempBoard = new BoardView(board);
        for (Move m: pendingMoves) {
            makeMove(m, tempBoard);
        }
        Move lastMove = pendingMoves.peekLast();
        if (lastMove != null && moveIsJump(lastMove)) {
            Space endSpace = tempBoard.getSpace(lastMove.getEnd());
            Piece movingPiece = endSpace.getPiece();
            if (movingPiece != null) {
                if (movingPiece.getColor() == Piece.color.RED && Piece.type.SINGLE == movingPiece.getType()) {
                    if (endSpace.getMyRow() < 2) {
                        return false;
                    }
                }
                else if (movingPiece.getColor() == Piece.color.WHITE && Piece.type.SINGLE == movingPiece.getType()) {
                    if (endSpace.getMyRow() > 5) {
                        return false;
                    }
                }
                else {
                    if (endSpace.getMyRow() % 7 == 0) {
                        return false;
                    }
                }
            }
            return pieceCanJump(endSpace, tempBoard);
        }
        return false;
    }

    /**
     * Generate all of the simple moves.
     *
     * @return the list of simple moves
     */
    public List<Move> generateSimpleMoves() {
        List<Move> tempMoves;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Row currentRow = board.getRow(i);
                Space testSpace = currentRow.getSpace(j);
                tempMoves = generateSimpleMovesOnSpace(testSpace);
                if (tempMoves != null) {
                    if (!tempMoves.isEmpty()) {
                        simpleMoves.addAll(tempMoves);
                    }
                }
            }
        }
        return simpleMoves;
    }

    /**
     * Generate all of the simple moves on the given space.
     *
     * @param space the space in question
     * @return the list of moves this space can make
     */
    public List<Move> generateSimpleMovesOnSpace(Space space) {
        if (!pieceCanSimple(space)) {
            return null;
        }
        Piece testPiece = space.getPiece();
        if (testPiece == null || testPiece.getColor() != playerColorToPieceColor(activeColor)) {
            return null;
        }
        int row = space.getMyRow();
        int col = space.getCellIdx();
        List<Move> tempMoves = new ArrayList<>();
        for (int x = -1 ; x < 2 ; x += 2) {
            for (int y = -1 ; y < 2 ; y += 2) {
                Move move = new Move(new Position(row, col), new Position(row + y, col + x));
                if (isValidSimple(move)) {
                    tempMoves.add(move);
                }
            }
        }
        return tempMoves;
    }

    /**
     * Checks if the given move is a valid step move.
     *
     * @param move the move being tested
     * @return boolean representation of if the piece if a valid step
     */
    public boolean isValidSimple(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();
        if (inBounds(start) && inBounds(end)) {
            Space startSpace = board.getRow(start.getRow()).getSpace(start.getCell());
            Piece pieceMoving = startSpace.getPiece();
            if (pieceMoving != null) {
                Piece endPiece = board.getRow(end.getRow()).getSpace(end.getCell()).getPiece();
                if (pieceMoving.getType() != Piece.type.KING) {
                    if (pieceMoving.getColor() == Piece.color.WHITE) {
                        if (start.getRow() >= end.getRow() || start.getCell() == end.getCell()) {
                            return false;
                        }
                    }
                    else {
                        if (start.getRow() <= end.getRow() || start.getCell() == end.getCell()) {
                            return false;
                        }
                    }
                }
                if (endPiece == null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generate the possible jump moves.
     *
     * @return The list of jump moves
     */
    public List<Move> generateJumpMoves() {
        List<Move> tempMoves;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Row currentRow = board.getRow(i);
                Space testSpace = currentRow.getSpace(j);
                tempMoves = generateJumpMovesOnSpace(testSpace, board);
                if (tempMoves != null) {
                    if (!tempMoves.isEmpty()) {
                        jumpMoves.addAll(tempMoves);
                    }
                }
            }
        }
        return jumpMoves;
    }

    /**
     * Generate the jump moves on the given space.
     *
     * @param space the space in question
     * @return the list of moves that the space can make
     */
    public List<Move> generateJumpMovesOnSpace(Space space, BoardView board) {
        if (!pieceCanJump(space, board)) {
            return null;
        }
        Piece piece = space.getPiece();
        if (piece == null || piece.getColor() != playerColorToPieceColor(activeColor)) {
            return null;
        }
        int row = space.getMyRow();
        int col = space.getCellIdx();
        List<Move> tempMoves = new ArrayList<>();
        for (int x = -1 ; x < 2 ; x += 2) {
            for (int y = -1 ; y < 2 ; y += 2) {
                 Move move = new Move( new Position(row, col), new Position(row + (2 * y), col + (2 * x)));
                if (isValidJump(move, board)) {
                    tempMoves.add( move );
                }
            }
        }
        return tempMoves;
    }

    /**
     * Checks if the move given is a valid jump.
     *
     * @param move the move being checked
     * @param board the board that the move is being checked on
     * @return boolean representation of if the given move is a valid jump
     */
    public boolean isValidJump(Move move, BoardView board) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Position middle = getMiddle(start, end);
        if (middle != null) {
            Space middleSpace = board.getSpace(middle);
            Space startSpace = board.getSpace(start);
            Space endSpace = board.getSpace(end);
            Piece middlePiece = middleSpace.getPiece();
            Piece startPiece = startSpace.getPiece();
            if (middlePiece != null && startPiece != null) {
                if (startPiece.getColor() != middlePiece.getColor()) {
                    if (endSpace.getPiece() == null) {
                        if (startPiece.getType() != Piece.type.KING) {
                            if (startPiece.getColor() == Piece.color.RED && startSpace.getMyRow() < endSpace.getMyRow()) {
                                return false;
                            }
                            if (startPiece.getColor() == Piece.color.WHITE && startSpace.getMyRow() > endSpace.getMyRow()) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Get the middle position.
     *
     * @param start the start
     * @param end the end
     * @return the middle of the start and the end
     */
    public Position getMiddle(Position start, Position end) {
        if (inBounds(start) && inBounds(end)) {
            double startRow = (double) start.getRow();
            double startCol = (double) start.getCell();
            double endRow = (double) end.getRow();
            double endCol = (double) end.getCell();
            double middleRow = (startRow + endRow) / 2;
            double middleCol = (startCol + endCol) / 2;
            if (Math.floor(middleRow) == middleRow && Math.floor(middleCol) == middleCol) {
                return new Position((int) middleRow, (int) middleCol);
            }
        }
        return null;
    }

    /**
     * Switches the active color.
     */
    public void swtichActiveColor() {
        if (activeColor == PlayerColor.RED) {
            activeColor = PlayerColor.WHITE;
        }
        else {
            activeColor = PlayerColor.RED;
        }
    }

    /**
     * Converts the given player color to piece color.
     *
     * @param color the player color
     * @return the piece representation of the player color
     */
    public Piece.color playerColorToPieceColor(PlayerColor color) {
        if (color == PlayerColor.RED) {
            return Piece.color.RED;
        }
        else {
            return Piece.color.WHITE;
        }
    }

    /**
     * Checks the given move to see if it is valid.
     *
     * @param move the move being checked
     * @param board the board that the move is being checked on
     * @return the message representing if the move is valid
     */
    public Message checkMove(Move move, BoardView board) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Space startSpace = board.getSpace(start);
        Space endSpace = board.getSpace(end);
        Piece startPiece = startSpace.getPiece();
        Piece endPiece = endSpace.getPiece();
        if (startPiece == null) {
            return new Message("Start space is unoccupied!", Message.Type.ERROR);
        }
        if (startPiece.getColor() != playerColorToPieceColor(activeColor)) {
            return new Message("Can't move your opponent's pieces!", Message.Type.ERROR);
        }
        if (endPiece != null) {
            return new Message("Can't move to an occupied space!", Message.Type.ERROR);
        }
        if (moveIsStep(move)) {
            if (hasJumpMove(activeColor)) {
                return new Message("There is a jump move available.", Message.Type.ERROR);
            }
            if (checkEquivalentMoveSimple(move) && isValidSimple(move)) {
                return new Message("Valid move!", Message.Type.INFO);
            }
            else {
                return new Message("You did something wrong", Message.Type.ERROR);
            }
        }
        else if (moveIsJump(move)) {
            if (isValidJump(move, board)) {
                updateMiddlePiece(move);
                return new Message("Valid jump!", Message.Type.INFO);
            }
            else {
                Position middle = getMiddle(start, end);
                Space middleSpace = board.getSpace(middle);
                Piece middlePiece = middleSpace.getPiece();
                if (middlePiece == null) {
                    return new Message("You can't jump over nothing!", Message.Type.ERROR);
                }
                else if (middlePiece.getColor() == playerColorToPieceColor(activeColor)) {
                    return new Message("You can't jump over your own pieces!", Message.Type.ERROR);
                }
                else {
                    return new Message("You did something wrong", Message.Type.ERROR);
                }
            }
        }
        else {
            return new Message("Invalid move", Message.Type.ERROR);
        }
    }

    /**
     * Find the longest number of jumps available at the moment.
     *
     * @return the longest number of jumps
     */
    public int findLongestJump() {
        int maxLength = 0;
        List<Move> possibleJumps = generateJumpMoves();
        if (!possibleJumps.isEmpty()) {
            maxLength++;
            for (Move jump : possibleJumps) {
                int jumpCount = longestJumpOnSpace(jump, board, 1);
                if (jumpCount > maxLength) {
                    maxLength = jumpCount;
                }
            }
        }
        return maxLength;
    }

    /**
     * Recursive method for finding the longest number of jumps on a given space.
     *
     * @param jump the jump that is being applied
     * @param board the board that the moves are being applied to
     * @param max the longest jump resulting from the given move
     * @return the longest number of jumps that space can make
     */
    public int longestJumpOnSpace(Move jump, BoardView board, int max) {
        int count;
        BoardView tempBoard = new BoardView(board);
        makeMove(jump, tempBoard);
        Position endPosition = jump.getEnd();
        Space endSpace = tempBoard.getSpace(endPosition);
        List<Move> nextJumps = generateJumpMovesOnSpace(endSpace, tempBoard);
        if (nextJumps != null && !nextJumps.isEmpty()) {
            for (Move nextJump : nextJumps) {
                makeMove(nextJump, tempBoard);
                count = longestJumpOnSpace(nextJump, tempBoard, max++);
                if (count > max) {
                    max = count;
                }
            }
        }
        return max;
    }

    /**
     * Update the middle piece after it has been jumped.
     *
     * @param move the jump move
     */
    public void updateMiddlePiece(Move move) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Position middle = getMiddle(start, end);
        Space middleSpace = board.getSpace(middle);
        Piece middlePiece = middleSpace.getPiece();
        if (middlePiece != null) {
            middlePiece.setBeenJumped(true);
        }
    }

    /**
     * Make the given move on the given board.
     *
     * @param move the move being made
     * @param board the board that the move is made on
     */
    public void makeMove(Move move, BoardView board) {
        Position start = move.getStart();
        Position end = move.getEnd();
        Space startSpace = board.getSpace(start);
        Piece movingPiece = startSpace.getPiece();
        Space endSpace = board.getSpace(end);
        if (movingPiece != null) {
            if (moveIsJump(move)) {
                Position middle = getMiddle(start, end);
                Space middleSpace = board.getSpace(middle);
                if (middleSpace != null) {
                    if (movingPiece.getColor() == Piece.color.RED) {
                        board.subtractWhitePieces(1);
                    }
                    else {
                        board.subtractRedPieces(1);
                    }
                    middleSpace.givePiece(null);
                }
            }
            if (endSpace.getMyRow() % 7 == 0 && movingPiece.getType() == Piece.type.SINGLE) {
                endSpace.givePiece(new Piece(Piece.type.KING, movingPiece.getColor()));
            } else {
                endSpace.givePiece(movingPiece);
            }
            startSpace.givePiece(null);
        }
    }

    /**
     * Check to see if the move is a jump.
     *
     * @param move the move being tested
     * @return boolean representation of whether the move is a jump
     */
    public boolean moveIsJump(Move move) {
        int startRow = move.getStart().getRow();
        int startCol = move.getStart().getCell();
        int endRow = move.getEnd().getRow();
        int endCol = move.getEnd().getCell();
        return (Math.abs(startRow - endRow) == 2 && Math.abs(startCol - endCol) == 2);
    }

    /**
     * Check to see if the move is a step.
     *
     * @param move the move being tested
     * @return boolean representation of whether the move is a step
     */
    public boolean moveIsStep(Move move) {
        int startRow = move.getStart().getRow();
        int startCol = move.getStart().getCell();
        int endRow = move.getEnd().getRow();
        int endCol = move.getEnd().getCell();
        return (Math.abs(startRow - endRow) == 1 && Math.abs(startCol - endCol) == 1);
    }

    /**
     * Get the current active color.
     *
     * @return the active color
     */
    public PlayerColor getActiveColor() {
        return activeColor;
    }
}