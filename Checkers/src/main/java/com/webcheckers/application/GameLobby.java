package com.webcheckers.application;

import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import java.util.HashMap;

public class GameLobby {

    // The map of games currently in progress.
    private HashMap<String, Game> games;

    public GameLobby() {
        games = new HashMap<>();
    }

    /**
     * Create a new game with the given players and add them to the map.
     *
     * @param redPlayer   the red player in the game
     * @param whitePlayer the white player in the game
     */
    public void addNewGame(Player redPlayer, Player whitePlayer) {
        Game tempGame = new Game(redPlayer, whitePlayer);
        games.put(redPlayer.getName(), tempGame);
        redPlayer.setGameID(redPlayer.getName());
        whitePlayer.setGameID(redPlayer.getName());
    }

    /**
     * Create a new game with the given redPlayer and botPlayer.
     *
     * @param redPlayer the human playing
     * @param botPlayer the bot playing
     */
    public void addNewBotGame(Player redPlayer, Player botPlayer) {
        Game tempGame = new Game(redPlayer, botPlayer);
        games.put(redPlayer.getName(), tempGame);
        redPlayer.setGameID(redPlayer.getName());
    }

    public void addSpectator(Player redPlayer, Player spectator) {
        Game game = getGame(redPlayer.getName());
        spectator.setRedPlayer(redPlayer);
        game.addSpectator(spectator);
        spectator.setGameID(spectator.getName());
    }

    /**
     * Get the game with the given key.
     *
     * @param name the key of the game being gotten
     * @return the game with the given key
     */
    public Game getGame(String name) {
        return games.get(name);
    }

    /**
     * End the game.
     *
     * @param game the game that is ending
     * @param reason the reason the game has ended
     */
    public void gameOver(Game game, String reason) {
        String gameID = game.getRedPlayer().getGameID();
        game.endGame(reason);
        games.remove(gameID);
    }

    /**
     * Method that handles what happens when a player resigns from a game.
     *
     * @param game the game that has been resigned
     * @param loser the person resigning from the game
     */
    public void resigned(Game game, Player loser) {
        game.resigned(loser);
    }
}