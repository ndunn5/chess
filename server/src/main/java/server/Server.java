package server;

import model.*;
import extramodel.ClearDatabaseResult;
import extramodel.JoinGameRequest;
import service.*;
import spark.*;
import static spark.Spark.*;

import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;

public class Server {

    private final ClearService clearService;
    private final RegisterService registerService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final ListGameService listGameService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();
        //replace these with SQL DAOs

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.listGameService = new ListGameService(gameDAO, authDAO);
        this.createGameService = new CreateGameService(gameDAO, authDAO);
        this.joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        port(desiredPort);
        staticFiles.location("web");

        delete("/db", (request, response) -> handleClearDatabase(response));
        post("/user", (request, response) -> handleRegister(request, response));
        post("/session", (request, response) -> handleLogin(request, response));
        delete("/session", (request, response) -> handleLogout(request, response));
        get("/game", (request, response) -> handleListGames(request, response));
        put("/game", (request, response) -> handleJoinGame(request, response));


        post("/game", (request, response) ->{
            String authToken = request.headers("authorization");
            String gameName = new Gson().fromJson(request.body(), CreateGameRequest.class).gameName();
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResult createGameResult = createGameService.createGame(createGameRequest);

            if(createGameResult.gameID() != -1){
                response.status(200);
            }
            else if (createGameResult.message().equals("Error: unauthorized")){
                response.status(401);
                return "{\"message\": \"Error: unauthorized\"}";
            }else{
                response.status(500);
                return "{\"message\": \"Description of error\"}";
            }
            return new Gson().toJson(createGameResult);
        });


        init();
        awaitInitialization();
        return port();
    }

    private String handleClearDatabase(Response response) {
        ClearDatabaseResult result = clearService.clear(new ClearDatabaseRequest());
        response.status(result.getStatusCode());
        return result.getStatusCode() == 200 ? "{}" : "{\"message\": \"" + result.getErrorMessage() + "\"}";
    }

    private String handleRegister(Request request, Response response) {
        RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
        RegisterResult result = registerService.register(registerRequest);
        response.status(getStatusCode(result.message()));
        return new Gson().toJson(result);
    }

    private String handleLogin(Request request, Response response) {
        LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
        LoginResult result = loginService.login(loginRequest);
        response.status(getStatusCode(result.message()));
        return new Gson().toJson(result);
    }

    private String handleLogout(Request request, Response response) {
        LogoutRequest logoutRequest = new LogoutRequest(request.headers("authorization"));
        LogoutResult result = logoutService.logout(logoutRequest);
        response.status(getStatusCode(result.message()));
        return result.message() == null ? "{}" : new Gson().toJson(result);
    }

    private String handleListGames(Request request, Response response) {
        ListGamesRequest listGamesRequest = new ListGamesRequest(request.headers("authorization"));
        ListGamesResult result = listGameService.listGames(listGamesRequest);
        response.status(getStatusCode(result.message()));
        return new Gson().toJson(result);
    }



    private String handleJoinGame(Request request, Response response) {
        String authToken = request.headers("authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
        joinGameRequest.addAuthToken(authToken);
        JoinGameResult result = joinGameService.joinGame(joinGameRequest);
        response.status(getStatusCode(result.message()));
        return result.message() == null ? "{}" : new Gson().toJson(result);
    }

    private int getStatusCode(String message) {
        return switch (message) {
            case null -> 200;
            case "Error: bad request" -> 400;
            case "Error: unauthorized" -> 401;
            case "Error: already taken" -> 403;
            default -> 500;
        };
    }

    public void stop() {
        Spark.stop();
        awaitStop();
    }
}
