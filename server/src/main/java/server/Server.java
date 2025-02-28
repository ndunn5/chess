package server;

import model.*;
import service.LoginService;
import service.RegisterService;
import spark.*;
import static spark.Spark.*;

import service.ClearService;


import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;


public class Server {

    private ClearService clearService;
    private RegisterService registerService;
    private LoginService loginService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(authDAO);
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

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
