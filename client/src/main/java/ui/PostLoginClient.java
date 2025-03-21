package ui;

import exception.ResponseException;
import model.LoginRequest;
import model.LoginResult;
import model.LogoutRequest;
import model.LogoutResult;
import server.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;


    public PostLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    private String checkSignedIn() {
        if (Repl.getState() != State.SIGNEDIN) {
            return "you have to be signed in for this command";
        } else {
            return null;
        }
    }


    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (Repl.getState() == State.SIGNEDIN) {
            return """
                    - create <NAME> - a game
                    - list - games 
                    - join <ID> [WHITE|BLACK] - a game
                    - observe <ID> - a game
                    - logout - when you are done
                    - quit - playing chess
                    - help - with possible commands
                    """;
        } else { //this is when the user isn't signed out
            return """
                    you gotta be signed in to be in this client!
                    """;
        }
    }

    public String logout() throws ResponseException {
        String signedInState = checkSignedIn();
        if (signedInState != null) {
            return signedInState;
        }
        try {
            LogoutResult logoutResult = server.handleLogout(new LogoutRequest(PreLoginClient.getAuthToken()));
            Repl.updateState(State.SIGNEDOUT);
            return "You signed out";
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }
}
