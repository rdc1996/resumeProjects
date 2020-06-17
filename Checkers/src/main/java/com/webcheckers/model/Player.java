package com.webcheckers.model;

public class Player {

    // Name of the player.
    private String name;

    // The current state that the player is in.
    private currentState currentState;

    // The id of the game that this player is in.
    private String gameID;

    private PlayerColor color;

    // Used by a spectator player to get game
    private Player redPlayer = this;

    // The different states that the player can be in.
    public enum currentState {
        WAITING, INGAME, CHALLENGE
    }

    public Player() {
        this.name = "WhiteBot";
    }

    public Player(String name){
        this.name = name;
    }

    /**
     * Get the name of the current player.
     *
     * @return the name of the player
     */
    public String getName(){
        return name;
    }

    /**
     * Get the current state of the player.
     *
     * @return the current state of the player
     */
    public currentState getCurrentState() {
        return this.currentState;
    }

    /**
     * Set the current state of the player.
     *
     * @param currentState what the players state should be set to
     */
    public void setCurrentState(currentState currentState) {
        this.currentState = currentState;
    }

    /**
     * Set the players game id.
     *
     * @param gameID what the players game id should be set to
     */
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    /**
     * Get this players game id.
     *
     * @return the game id of the player
     */
    public String getGameID() {
        return this.gameID;
    }

    /**
     * Get the player's color.
     *
     * @return their color
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Set the player's color.
     *
     * @param color the color being set
     */
    public void setColor(PlayerColor color) {
        this.color = color;
    }

    /**
     * Get the redPlayer for a spectator.
     *
     * @return redPlayer used to put game in gameLobby
     */
    public Player getRedPlayer() {
        return redPlayer;
    }

    /**
     * Set the red player for a spectated game.
     *
     * @param redPlayer initial redPlayer in a spectated game
     */
    public void setRedPlayer(Player redPlayer) {
        this.redPlayer = redPlayer;
    }
}