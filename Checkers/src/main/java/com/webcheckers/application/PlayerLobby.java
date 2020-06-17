package com.webcheckers.application;
import com.webcheckers.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerLobby  {

    // The list of players in the lobby.
    private List<Player> players;

    public PlayerLobby(){
        players = new ArrayList<>();
    }

    /**
     * Add the given player to the list of players.
     *
     * @param player the player to be added to the list
     */
    public void addPlayer(Player player){
        players.add(player);
    }

    /**
     * Get the list of players in the lobby.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Check to see if the provided name is already taken.
     *
     * @param name the name being checked
     * @return boolean representing whether the name is taken
     */
    public boolean containsPlayerName(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check to see if the given name has special characters in it.
     *
     * @param name the name being checked
     * @return boolean representing whether the name has special characters
     */
    public boolean hasSpecialCharacter(String name) {
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    /**
     * Check to see if the name is just white space
     *
     * @param name the name being checked
     * @return boolean representing whether the name is just white space
     */
    public boolean isJustSpaces(String name) {
        for (int i = 0; i < name.length(); i++) {
            char nameSub = name.charAt(i);
            if (nameSub != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove a player from the list of players.
     *
     * @param currentPlayer the player being removed
     */
    public void removePlayer(Player currentPlayer) {
        players.remove(currentPlayer);
    }

    /**
     * Get the number of players in the lobby.
     *
     * @return the number of players
     */
    public int numberOfPlayers() {
        return players.size();
    }

    /**
     * Get the player from the lobby that has the given name.
     *
     * @param name the name of the player being retrieved
     * @return the player from the list with the given name.
     */
    public Player getPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return new Player(name);
    }
}
