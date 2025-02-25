package server;

import spark.*;
import static spark.Spark.*;
import service.ClearService;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

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
            boolean sucess = clearService.clear();
            if (sucess) {
                response.status(200);
                return "{}";
            } else {
                response.status(500);
                return "{ \"message\": \"Error: (description of error)\" }";
            }
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
