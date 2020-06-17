package com.webcheckers.model;

import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer() {
        super();
    }

    /**
     * Method to make the AI Player take their turn.
     *
     * @param currentGame the current game being played
     */
    public void turn(Game currentGame) {
        MoveValidator validator = currentGame.getMoveValidator();

        List<Move> validMoves = currentGame.getValidMoves();
        if (validMoves == null || validMoves.isEmpty()) {
            currentGame.setGameOver(currentGame.getRedPlayer().getName() + " has blocked all pieces.");
            currentGame.setLoser(currentGame.getWhitePlayer());
            currentGame.setWinner(Game.Win.RED);
            return;
        }
        int randomNumber = (int)(Math.random()*(validMoves.size() - 1));

        if (validMoves != null && !validMoves.isEmpty()) {
            Move tempMove = validMoves.get(randomNumber);
            currentGame.pushMove(tempMove);
            if (validator.moveIsJump(tempMove)) {
                validMoves = currentGame.generateMoreJumpe();
                while (validMoves != null) {
                    randomNumber = (int)(Math.random()*(validMoves.size() - 1));
                    tempMove = validMoves.get(randomNumber);
                    currentGame.pushMove(tempMove);
                    validMoves = currentGame.generateMoreJumpe();
                }
            }
        }
        if(currentGame.getSpectators().size() > 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentGame.submit();
    }
}
