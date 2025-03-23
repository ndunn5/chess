import chess.*;
import exception.ResponseException;
import ui.Repl;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            ServerFacade serverFacade = new ServerFacade(serverUrl);
            serverFacade.handleClearDatabase();
        } catch (ResponseException ex){
            System.out.println("An error occurred while clearing the database: " + ex.getMessage());
        }

        new Repl(serverUrl).run();
    }
}
