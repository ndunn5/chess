package server;

import service.ClearDatabaseResult;
import spark.*;
import static spark.Spark.*;
import service.ClearService;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import service.ClearDatabaseRequest;

public class Server {

    private ClearService clearService;

    public Server() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        delete("/db", (request, response) ->{
            ClearDatabaseRequest clearDatabaseRequest = new ClearDatabaseRequest();
            ClearDatabaseResult result = clearService.clear(clearDatabaseRequest);

            response.status(result.getStatusCode());
            if (result.getStatusCode() == 200) {
                response.body("{}");
            } else {
                response.body("{\"message\": \"" + result.getErrorMessage() + "\"}");
            }
            return response.body();
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
