package server;

import model.*;
import service.*;
import spark.*;
import static spark.Spark.*;


import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;


public class Server {

    private ClearService clearService;
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGameService listGameService;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.listGameService = new ListGameService(gameDAO, authDAO);
        this.createGameService = new CreateGameService(gameDAO, authDAO);
        this.joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        delete("/db", (request, response) ->{
            ClearDatabaseRequest clearDatabaseRequest = new ClearDatabaseRequest();
            ClearDatabaseResult result = clearService.clear(clearDatabaseRequest);

            response.status(result.getStatusCode());
            if (result.getStatusCode() == 200) {
                response.body("{}"); //return new Gson().toJson(pet)
            } else {
                response.body("{\"message\": \"" + result.getErrorMessage() + "\"}");
            }
            return response.body();
        });

        post("/user", (request, response) ->{
            RegisterRequest registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class);
            RegisterResult result = registerService.register(registerRequest);

            if(result.message() == null){
                response.status(200);
            }
            else{
                switch(result.message()){
                    case "Error: bad request":
                        response.status(400);
                        break;
                    case "Error: already taken" :
                        response.status(403);
                        break;
                    default:
                        response.status(500);
                }
            }
            return new Gson().toJson(result);
        });

        post("/session", (request, response) ->{
            LoginRequest loginRequest = new Gson().fromJson(request.body(), LoginRequest.class);
            LoginResult result = loginService.login(loginRequest);

            if (result.message() == null){
                response.status(200);
            }
            else{
                switch(result.message()){
                    case("Error: unauthorized"):
                        response.status(401);
                        break;
                    default:
                        response.status(500);
                }
            }
            return new Gson().toJson(result);
        });


        delete("/session", (request, response) ->{
            String authToken = request.headers("authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult result = logoutService.logout(logoutRequest);

            if (result.message() == null){
                response.status(200);
                return "{}";
            }
            if(result.message().equals("Error: unauthorized")){
                response.status(401);
            }else {
                response.status(500);
            }
            return new Gson().toJson(result);
        });

        get("/game", (request, response) ->{
            String authToken = request.headers("authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
//            ListGamesRequest listGamesRequest = new Gson().fromJson(request.body(), ListGamesRequest.class);
            ListGamesResult listGamesResult = listGameService.listGames(listGamesRequest);

            if(listGamesResult.message() == null){
                response.status(200);
            } else if (listGamesResult.message().equals("Error: unauthorized")){
                response.status(401);
            } else{
                response.status(500);
            }
            return new Gson().toJson(listGamesResult);
        });

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

        put("/game", (request, response) ->{
            String authToken = request.headers("authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            joinGameRequest.addAuthToken(authToken);
            JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest);
            if(joinGameResult.message() == null){
                response.status(200);
                response.body("{}");
            }else{
                switch (joinGameResult.message()){
                    case("Error: bad request"):
                        response.status(400);
                        break;
                    case("Error: unauthorized"):
                        response.status(401);
                        break;
                    case("Error: already taken"):
                        response.status(403);
                        break;
                    default:
                        response.status(500);
                }
            }
            return new Gson().toJson(joinGameResult);
        });




        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
