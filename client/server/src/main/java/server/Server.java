package server;

import model.RegisterResult;
import service.RegisterService;
import spark.*;
import static spark.Spark.*;

import service.ClearService;
import model.ClearDatabaseRequest;
import model.ClearDatabaseResult;

import model.RegisterRequest;


import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;


public class Server {

    private ClearService clearService;
    private RegisterService registerService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
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

        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
