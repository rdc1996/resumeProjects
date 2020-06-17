package com.webcheckers.ui;

import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import spark.*;
import java.util.logging.Logger;

public class PostSignoutRoute implements Route{

    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    private final PlayerLobby playerLobby;

    public PostSignoutRoute(PlayerLobby playerLobby) {
        this.playerLobby = playerLobby;
        //
        LOG.config("GetSignoutRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response){
        LOG.finer("GetSingoutRoute is invoked.");

        // Get the current player and remove them from the lobby
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(GetGameRoute.CURRENT_USER_ATTR);
        playerLobby.removePlayer(currentPlayer);

        // Change current player attribute to null and redirect to home page
        httpSession.attribute(GetGameRoute.CURRENT_USER_ATTR, null);
        response.redirect(WebServer.HOME_URL);
        return null;
    }

}
