package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.model.*;
import com.webcheckers.application.GameLobby;
import com.webcheckers.application.Message;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Board.BoardView;
import com.webcheckers.viewMode;
import spark.*;
import spark.TemplateEngine;

import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

public class GetGameRoute implements Route {

    private static final Logger LOG = Logger.getLogger(GetGameRoute.class.getName());

    private final TemplateEngine templateEngine;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;

    // Attributes for the view
    public static final String TITLE_ATTR = "title";
    public static final String CURRENT_USER_ATTR = "currentUser";
    public static final String VIEW_ATTR = "viewMode";
    public static final String WHITE_PLAYER_ATTR = "whitePlayer";
    public static final String RED_PLAYER_ATTR = "redPlayer";
    public static final String ACTIVE_COLOR_ATTR = "activeColor";
    public static final String MESSAGE_ATTR = "message";
    public static final String BOARD_ATTR = "board";
    public static final Message YOU_WIN =
            new Message("You win! Please redirect to the home page.", Message.Type.INFO);
    public static final Message YOU_LOSE =
            new Message("You lose. Better luck next time! Please redirect to the home page.", Message.Type.ERROR);

    public static final Message OCCUPIED_GAME = Message.info("Sorry, the selected player was in a game");

    /**
     * The constructor for the {@code GET /game} route handler.
     *
     * @param templateEngine
     *    The {@link TemplateEngine} used for rendering page HTML.
     */
    public GetGameRoute(final TemplateEngine templateEngine, PlayerLobby playerLobby, GameLobby gameLobby) {
        // validation
        Objects.requireNonNull(templateEngine, "templateEngine must not be null");
        //
        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.gameLobby = gameLobby;
        // The view model for the game view
        LOG.config("GetGameRoute is initialized.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetGameRoute is invoked.");
        Map<String, Object> vm = new HashMap<>();

        Gson gson = new Gson();

        // Get the current user
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);

        // Insert the title
        vm.put(TITLE_ATTR, "Game");
        // Insert the current player
        vm.put(CURRENT_USER_ATTR, currentPlayer);

        // If someone is trying to play the current player
        // Another user clicked on this one's name
        if (currentPlayer.getCurrentState() == Player.currentState.CHALLENGE) {
            Game currentGame = gameLobby.getGame(currentPlayer.getGameID());
            currentPlayer.setCurrentState(Player.currentState.INGAME);
            // Insert the current view mode
            vm.put(VIEW_ATTR, viewMode.PLAY);
            // Insert the Red player
            vm.put(RED_PLAYER_ATTR, currentGame.getRedPlayer());
            // Insert the White player
            vm.put(WHITE_PLAYER_ATTR, currentPlayer);
            // Insert the active color
            vm.put(ACTIVE_COLOR_ATTR, "RED");
            // Inserts the temporary board for the white player.
            vm.put(BOARD_ATTR, currentGame.getBoard().getWhiteView());
        }
        // Current player is challenging someone else
        // This user clicked another user name
        else if (currentPlayer.getCurrentState() == Player.currentState.WAITING){
            String name = request.queryParams("id");
            Player opponent = playerLobby.getPlayer(name);
            currentPlayer.setCurrentState(Player.currentState.INGAME);
            if (opponent.getName().equals("WhiteBot")) {
                gameLobby.addNewBotGame(currentPlayer, opponent);
                Game currentGame = gameLobby.getGame(currentPlayer.getName());
                vm.put(VIEW_ATTR, viewMode.PLAY);
                vm.put(RED_PLAYER_ATTR, currentPlayer);
                vm.put(WHITE_PLAYER_ATTR, opponent);
                vm.put(BOARD_ATTR, currentGame.getBoard());
                vm.put(ACTIVE_COLOR_ATTR, "RED");
                currentPlayer.setColor(PlayerColor.RED);
                opponent.setColor(PlayerColor.WHITE);
                return templateEngine.render(new ModelAndView(vm, "game.ftl"));
            }
            if (opponent.getCurrentState() == Player.currentState.INGAME) {
                Player red = gameLobby.getGame(opponent.getGameID()).getRedPlayer();
                gameLobby.addSpectator(red, currentPlayer);
                Game currentGame = gameLobby.getGame(opponent.getGameID());
                currentPlayer.setCurrentState(Player.currentState.INGAME);
                vm.put(VIEW_ATTR, viewMode.SPECTATOR);
                vm.put(RED_PLAYER_ATTR, currentGame.getRedPlayer());
                vm.put(WHITE_PLAYER_ATTR, currentGame.getWhitePlayer());
                vm.put(ACTIVE_COLOR_ATTR, currentGame.getActiveColor());
                vm.put(BOARD_ATTR, currentGame.getBoard());
            }
            else {
                gameLobby.addNewGame(currentPlayer, opponent);
                Game currentGame = gameLobby.getGame(currentPlayer.getName());
                opponent.setCurrentState(Player.currentState.CHALLENGE);
                // Insert the current view mode
                vm.put(VIEW_ATTR, viewMode.PLAY);
                // Insert the Red player
                vm.put(RED_PLAYER_ATTR, currentPlayer);
                // Insert the White player
                vm.put(WHITE_PLAYER_ATTR, opponent);
                // Insert the active color
                vm.put(ACTIVE_COLOR_ATTR, "RED");
                //Set player colors for game reference
                currentPlayer.setColor(PlayerColor.RED);
                opponent.setColor(PlayerColor.WHITE);
                //set active color for game reference
                LOG.config(currentGame.getActiveColor().toString());
                // Inserts the temporary board for the white player.
                vm.put(BOARD_ATTR, currentGame.getBoard());
            }
        }
        else {
            Game currentGame = gameLobby.getGame(currentPlayer.getRedPlayer().getGameID());
            BoardView currentBoard = currentGame.getBoard();
            // check to see if currentPlayer is a spectator
            if(currentPlayer != currentPlayer.getRedPlayer()) {
                vm.put(VIEW_ATTR, viewMode.SPECTATOR);
            }
            else {
                vm.put(VIEW_ATTR, viewMode.PLAY);
            }
            vm.put(RED_PLAYER_ATTR, currentGame.getRedPlayer());
            vm.put(WHITE_PLAYER_ATTR, currentGame.getWhitePlayer());
            vm.put(ACTIVE_COLOR_ATTR, currentGame.getActiveColor());
            if (currentPlayer == currentGame.getRedPlayer()) {
                vm.put(BOARD_ATTR, currentGame.getBoard());
            }
            else {
                vm.put(BOARD_ATTR, currentGame.getBoard().getWhiteView());
            }
            if (!currentBoard.hasRedPiecesLeft()) {
                currentGame.setWinner(Game.Win.WHITE);
                currentGame.setLoser(currentGame.getRedPlayer());
                Player winner = currentGame.getWhitePlayer();
                currentGame.setGameOver(winner.getName() + " has captured all pieces.");
            }
            if (!currentBoard.hasWhitePiecesLeft()) {
                currentGame.setWinner(Game.Win.RED);
                currentGame.setLoser(currentGame.getWhitePlayer());
                Player winner = currentGame.getRedPlayer();
                currentGame.setGameOver(winner.getName() + " has captured all pieces.");
            }
            Game.Win winner = currentGame.getWinner();
            if (winner != Game.Win.INGAME) {
                final Map<String, Object> modeOptions = new HashMap<>(2);
                modeOptions.put("isGameOver", true);
                modeOptions.put("gameOverMessage", currentGame.getGameOver());
                vm.put("modeOptionsAsJSON", gson.toJson(modeOptions));
                if (winner == currentGame.playerColorToWinColor(currentPlayer.getColor())) {
                    vm.put(MESSAGE_ATTR, YOU_WIN);
                }
                else {
                    vm.put(MESSAGE_ATTR, YOU_LOSE);
                }
            }
        }
        //updating board depending on whose turn it is
        return templateEngine.render(new ModelAndView(vm, "game.ftl"));
    }
}
