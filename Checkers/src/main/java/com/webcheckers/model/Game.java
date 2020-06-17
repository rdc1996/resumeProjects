package com.webcheckers.model;

import com.webcheckers.model.Board.BoardView;
import com.webcheckers.application.Message;
import com.webcheckers.model.Board.Space;

import java.util.*;

public class Game {

    public enum Win {
        RED, WHITE, INGAME
    }

    // The loser of the game.
    private Player loser;

    // The red player in the game.
    private Player redPlayer;

    // The white player in the game.
    private Player whitePlayer;

    // List of spectators to the game
    private ArrayList<Player> spectators = new ArrayList<Player>();

    // The board for the game.
    private BoardView board;

    // The player color whose turn it currently is.
    private PlayerColor activeColor;

    // The moveValidator that creates all of the moves.
    private MoveValidator moveValidator;

    // The winner of the game.
    private Win winner;

    // The stack of moves for the current turn.
    private Deque<Move> moves = new LinkedList<>();

    // The moves that can be done by the player.
    private List<Move> validMoves = new ArrayList<>();

    // String holding why the game was ended.
    private String gameOver;


    public Game(Player redPlayer, Player whitePlayer) {
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        try {
            board = new BoardView("demo", Integer.parseInt(redPlayer.getName()));
        } catch(NumberFormatException e){
            board = new BoardView();
        }

        activeColor = PlayerColor.RED;
        moveValidator = new MoveValidator(board);
        this.winner = Win.INGAME;
    }

    public Game(Player redPlayer, Player whitePlayer, BoardView board) {
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        this.board = board;
        activeColor = PlayerColor.RED;
        moveValidator = new MoveValidator(board);
        this.winner = Win.INGAME;
    }

    public void setRedPiecesZero() {
        board.subtractRedPieces(12);
    }

    public void setWhitePiecesZero() {
        board.subtractWhitePieces(12);
    }

    /**
     * Get the red player in the game.
     *
     * @return The red player
     */
    public Player getRedPlayer() {
        return redPlayer;
    }

    /**
     * Get the white player in the game.
     *
     * @return The white player
     */
    public Player getWhitePlayer() {
        return whitePlayer;
    }

    /**
     * Get the spectators in the game.
     *
     * @return List of spectators
     */
    public ArrayList<Player> getSpectators() {
        return spectators;
    }

    /**
     * Set the red player to be the given player.
     *
     * @param redPlayer the player to be set as the red player
     */
    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }

    /**
     * Set the white player to be the given player.
     *
     * @param whitePlayer the player to be set as the white player
     */
    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    /**
     * Add spectators in the game.
     *
     * @param spectator a spectator player
     */
    public void addSpectator(Player spectator) {
        this.spectators.add(spectator);
    }

    /**
     * Get the board.
     *
     * @return the board
     */
    public BoardView getBoard() {
        return board;
    }

    /**
     * Get the current active color.
     *
     * @return the active color
     */
    public PlayerColor getActiveColor() {
        return activeColor;
    }

    /**
     * Push a move onto the end of the queue.
     *
     * @param move the move being pushed
     */
    public void pushMove(Move move) {
        if (activeColor == PlayerColor.WHITE && !whitePlayer.getName().equals("WhiteBot")) {
            move.flipMove();
        }
        moves.offerLast(move);
    }

    /**
     * Removes a move from the queue
     *
     * @return returns false if there is nothing to pop off, true if it was successful
     */
    public boolean removeMove(){
        if(moves.size()==0){
            return false;
        } else {
            moves.removeLast();
            return true;
        }
    }

    /**
     * Set the active color and switch it in the move moveValidator.
     *
     * @return the active color
     */
    public void switchActiveColor() {
        if (this.activeColor == PlayerColor.RED) {
            activeColor = PlayerColor.WHITE;
        }
        else {
            activeColor = PlayerColor.RED;
        }
        moveValidator.swtichActiveColor();
        if (isBotPlayerTurn()) {
            AIPlayer currentBot = (AIPlayer)whitePlayer;
            if (board.hasWhitePiecesLeft()) {
                currentBot.turn(this);
            }
        }
    }

    /**
     * Checks to see if it is the Bot's turn.
     *
     * @return true if it is the Bot's turn
     */
    public boolean isBotPlayerTurn() {
        if (whitePlayer.getName().equals("WhiteBot") && activeColor == PlayerColor.WHITE) {
            return true;
        }
        return false;
    }

    /**
     * Get the winner of the game.
     *
     * @return the winner
     */
    public Win getWinner() {
        return winner;
    }

    /**
     * Set the winner of the game.
     *
     * @param winner the winner
     */
    public void setWinner(Win winner) {
        this.winner = winner;
    }

    /**
     * Get the opponent of the current player.
     *
     * @param player the current player
     * @return the opponent
     */
    public Player getOpponent(Player player) {
        if (player == redPlayer) {
            return whitePlayer;
        }
        else {
            return redPlayer;
        }
    }

    /**
     * Checks all the pending moves and validates the latest one.
     *
     * @param move the move being validated
     * @return a message of whether the move has been approved
     */
    public Message validateMove(Move move) {
        validMoves = moveValidator.generateAllMoves();
        BoardView tempBoard = new BoardView(board);
        for (Move m: moves) {
            moveValidator.makeMove(m,tempBoard);
        }
        return moveValidator.checkMove(move, tempBoard);
    }

    /**
     * Convert the given player color to a win color.
     *
     * @param color the player's color
     * @return the equivalent win color
     */
    public Game.Win playerColorToWinColor(PlayerColor color) {
        if (color == PlayerColor.RED) {
            return Win.RED;
        }
        else {
            return Win.WHITE;
        }
    }

    /**
     * Handles what happens when a player resigns.
     *
     * @param loser the loser of the game
     */
    public void resigned(Player loser) {
        Player winner = getOpponent(loser);
        loser.setCurrentState(Player.currentState.WAITING);
        if (winner == whitePlayer) {
            this.winner = Win.WHITE;
            setLoser(redPlayer);
        }
        else {
            this.winner = Win.RED;
            setLoser(whitePlayer);
        }
        setGameOver(loser.getName() + " has resigned from the game.");
        switchActiveColor();
    }

    /**
     * Checks if the current player is the active player.
     *
     * @param player the current player
     * @return boolean if they are equivalent
     */
    public boolean isCurrentPlayer(Player player) {
        return (player.getColor() == activeColor);
    }

    /**
     * Handles what happens when a move gets submitted.
     *
     * @return a message saying if the moves were successfully submitted
     */
    public Message submit() {
        if (moveValidator.hasAnotherJump(moves, board)) {
            return new Message("Can't end turn when there is still a jump available", Message.Type.ERROR);
        }
        if (moves != null) {
            while (!moves.isEmpty()) {
                Move currentMove = moves.pop();
                if (activeColor == PlayerColor.WHITE && !whitePlayer.getName().equals("WhiteBot")) {
                    currentMove.flipMove();
                }
                moveValidator.makeMove(currentMove, board);
            }
        }
        switchActiveColor();
        return new Message("Moves have successfully been submitted.", Message.Type.INFO);
    }

    /**
     * Get all of the valid moves that the Bot can make.
     *
     * @return the list of moves that the Bot can make
     */
    public List<Move> getValidMoves() {
        validMoves = moveValidator.generateAllMoves();
        if (validMoves == null || validMoves.isEmpty()) {
            return null;
        }
        return validMoves;
    }

    /**
     * Get the move validator.
     *
     * @return the move validator for the game
     */
    public MoveValidator getMoveValidator() {
        return moveValidator;
    }

    /**
     * Generate more jumps after all of the moves have been applied.
     *
     * @return the new list of moves
     */
    public List<Move> generateMoreJumpe() {
        BoardView tempBoard = new BoardView(board);
        for (Move m: moves) {
            moveValidator.makeMove(m,tempBoard);
        }
        Move lastMove = moves.peekLast();
        Position endPosition = lastMove.getEnd();
        Space endSpace = tempBoard.getSpace(endPosition);
        List<Move> newJumpMoves;
        newJumpMoves = moveValidator.generateJumpMovesOnSpace(endSpace, tempBoard);
        if (newJumpMoves != null && !newJumpMoves.isEmpty()) {
            return newJumpMoves;
        }
        return null;
    }

    /**
     * Set the loser of the game.
     *
     * @param player the loser of the game
     */
    public void setLoser(Player player) {
        this.loser = player;
    }

    /**
     * Get the loser of the game.
     *
     * @return the loser of the game
     */
    public Player getLoser() {
        return loser;
    }

    /**
     * Set the game over string for the game.
     *
     * @param gameOver the string to be shown in the game view
     */
    public void setGameOver(String gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Get the game over string for the game.
     *
     * @return the game over string to be shown in the game view
     */
    public String getGameOver() {
        return gameOver;
    }

    /**
     * End the current game.
     *
     * @param reason the reason that the game is ending
     */
    public void endGame(String reason) {
        setGameOver(reason);
        redPlayer.setGameID(null);
        whitePlayer.setGameID(null);
        redPlayer.setColor(null);
        whitePlayer.setColor(null);
        redPlayer.setCurrentState(Player.currentState.WAITING);
        whitePlayer.setCurrentState(Player.currentState.WAITING);
        winner = null;
    }
}