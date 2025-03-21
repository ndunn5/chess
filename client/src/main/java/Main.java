import chess.*;
import ui.Repl;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            new ServerFacade(serverUrl).handleClearDatabase();
            System.out.println("Database reset successfully.");
        } catch (Exception e) {
            System.out.println("Failed to clear database: " + e.getMessage());
        }
        new Repl(serverUrl).run();
    }
}
