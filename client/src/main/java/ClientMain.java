import exception.ResponseException;
import ui.Repl;
import server.ServerFacade;



public class ClientMain {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
//        ServerFacade serverFacade = new ServerFacade(serverUrl);
//
//        try{
//            serverFacade.handleClearDatabase();
//        } catch (ResponseException e) {
//            throw new RuntimeException(e);
//        }
        new Repl(serverUrl).run();
    }
}
