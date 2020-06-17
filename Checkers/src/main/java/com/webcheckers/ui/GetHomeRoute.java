package com.webcheckers.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.webcheckers.application.GameLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import spark.*;

import com.webcheckers.application.Message;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 */
public class GetHomeRoute implements Route {
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  public static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");
  public static final String TITLE_MESSAGE = "Welcome!";
  static final String NUMPLAYERS = "playerNumber";

  private final TemplateEngine templateEngine;
  private final PlayerLobby playerLobby;
  private final GameLobby gameLobby;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine
   *   the HTML template rendering engine
   */
  public GetHomeRoute(final TemplateEngine templateEngine, PlayerLobby playerLobby, GameLobby gameLobby) {
    this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
    this.playerLobby = playerLobby;
    this.gameLobby = gameLobby;
    //
    LOG.config("GetHomeRoute is initialized.");
  }

  /**
   * Render the WebCheckers Home page.
   *
   * @param request
   *   the HTTP request
   * @param response
   *   the HTTP response
   *
   * @return
   *   the rendered HTML for the Home page
   */
  @Override
  public Object handle(Request request, Response response) {
    LOG.finer("GetHomeRoute is invoked.");

    // Get the current user
    Session httpSession = request.session();
    Player currentPlayer = httpSession.attribute(GetGameRoute.CURRENT_USER_ATTR);

    Map<String, Object> vm = new HashMap<>();

    // Add the title to the view model
    vm.put(GetGameRoute.TITLE_ATTR, TITLE_MESSAGE);
    // Add the current user to the view model
    vm.put(GetGameRoute.CURRENT_USER_ATTR, currentPlayer);
    // display a user message in the Home page
    vm.put(GetGameRoute.MESSAGE_ATTR, WELCOME_MSG);

    // Add the number of players to the view
    if (playerLobby.numberOfPlayers() >= 1) {
        vm.put(NUMPLAYERS, String.valueOf(playerLobby.numberOfPlayers()));
        vm.put("playerList", playerLobby.getPlayers());
    }

    if (currentPlayer != null) {
      if (currentPlayer.getCurrentState() == Player.currentState.CHALLENGE) {
        response.redirect(WebServer.GAME_URL);
      }
      if (currentPlayer.getCurrentState() == Player.currentState.INGAME) {
        currentPlayer.setCurrentState(Player.currentState.WAITING);
      }
      if (gameLobby.getGame(currentPlayer.getGameID()) != null) {
        Game currentGame = gameLobby.getGame(currentPlayer.getGameID());
        Player opponent = currentGame.getOpponent(currentPlayer);
        if (currentGame.getWinner() != Game.Win.INGAME && opponent.getCurrentState() != Player.currentState.INGAME) {
          gameLobby.gameOver(currentGame, currentGame.getGameOver());
        }
      }
    }

    return templateEngine.render(new ModelAndView(vm , "home.ftl"));
  }


}
