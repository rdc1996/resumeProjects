package com.webcheckers.ui;

import com.webcheckers.application.GameLobby;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.viewMode;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class GetSpectatorGame implements Route {

    private static final Logger LOG = Logger.getLogger(GetSpectatorGame.class.getName());

    private final TemplateEngine templateEngine;
    private PlayerLobby playerLobby;
    private GameLobby gameLobby;

    public static final String TITLE_ATTR = "title";
    public static final String CURRENT_USER_ATTR = "currentUser";
    public static final String VIEW_ATTR = "viewMode";
    public static final String WHITE_PLAYER_ATTR = "whitePlayer";
    public static final String RED_PLAYER_ATTR = "redPlayer";
    public static final String ACTIVE_COLOR_ATTR = "activeColor";
    public static final String BOARD_ATTR = "board";

    /**
     * The constructor for the {@code GET /game} route handler.
     *
     * @param templateEngine
     *    The {@link TemplateEngine} used for rendering page HTML.
     */
    public GetSpectatorGame(final TemplateEngine templateEngine, PlayerLobby playerLobby, GameLobby gameLobby) {
        // validation
        Objects.requireNonNull(templateEngine, "templateEngine must not be null");
        //
        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.gameLobby = gameLobby;
        // The view model for the game view
        LOG.config("GetSpectatorGame is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        LOG.finer("GetSpectatorGame is invoked.");
        Map<String, Object> vm = new HashMap<>();

        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_USER_ATTR);

        Game currentGame = gameLobby.getGame(currentPlayer.getGameID());

        // Insert the title
        vm.put(TITLE_ATTR, "Game");
        // Insert the current player
        vm.put(CURRENT_USER_ATTR, currentPlayer);
        // Insert the current view mode
        vm.put(VIEW_ATTR, viewMode.SPECTATOR);
        // Insert the Red player
        vm.put(RED_PLAYER_ATTR, currentGame.getRedPlayer());
        // Insert the White player
        vm.put(WHITE_PLAYER_ATTR, currentGame.getWhitePlayer());
        // Insert the active player
        vm.put(ACTIVE_COLOR_ATTR, currentGame.getActiveColor());
        // Insert the board
        vm.put(BOARD_ATTR, currentGame.getBoard());

        return templateEngine.render(new ModelAndView(vm, "game.ftl"));
    }
}
