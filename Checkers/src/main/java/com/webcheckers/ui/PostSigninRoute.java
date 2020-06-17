package com.webcheckers.ui;
import com.webcheckers.model.Player;
import com.webcheckers.application.PlayerLobby;
import com.webcheckers.application.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.TemplateEngine;
import spark.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class PostSigninRoute implements Route {
    private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

    private static final Message WELCOME_MSG = Message.info("Welcome to the world of online Checkers.");
    public static final String USERNAME = "userName";
    public static final String INVALID =  "invalidSignin";

    private final PlayerLobby playerLobby;
    private final TemplateEngine templateEngine;

    public PostSigninRoute(final TemplateEngine templateEngine, PlayerLobby playerLobby) {
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine is required");
        this.playerLobby = playerLobby;
        //
        LOG.config("PostSigninRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response){
        LOG.finer("PostSinginRoute is invoked.");
        Session httpSession = request.session();

        Map<String, Object> vm = new HashMap<>();
        vm.put("title", "Welcome!");

        final String username = request.queryParams(USERNAME);

        if (username.length() > 0) {
            if (playerLobby.containsPlayerName(username)) {
                vm.put(INVALID, "That name is already in use, please pick another one.");
                return templateEngine.render(new ModelAndView(vm, "signin.ftl"));
            }
            if (playerLobby.hasSpecialCharacter(username)) {
                vm.put(INVALID, "Your username may not contain special characters.");
                return templateEngine.render(new ModelAndView(vm, "signin.ftl"));
            }
            if (playerLobby.isJustSpaces(username)) {
                vm.put(INVALID, "Name must use alphanumeric characters.");
                return templateEngine.render((new ModelAndView(vm, "signin.ftl")));
            }
            else {
                Player newUser = new Player(username);
                playerLobby.addPlayer(newUser);
                vm.put(GetGameRoute.CURRENT_USER_ATTR, newUser);
                newUser.setCurrentState(Player.currentState.WAITING);
                httpSession.attribute(GetGameRoute.CURRENT_USER_ATTR, newUser);
                response.redirect(WebServer.HOME_URL);
                return templateEngine.render(new ModelAndView(vm, "home.ftl"));
            }
        }
        else {
            vm.put("invalidSignin", "Enter a valid name");
            return templateEngine.render(new ModelAndView(vm, "signin.ftl"));
        }
    }
}