package ui;

import model.LoginRequest;
import model.RegisterRequest;
import server.ServerFacade;
import exception.ResponseException;

import java.util.Arrays;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                case "login" -> login(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String checkSignedOut() {
        if (state != state.SIGNEDOUT) {
            return "you have to be signed out for this command";
        } else {
            return null;
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    - login <USERNAME> <PASSWORD> - to play chess
                    - quit - playing chess
                    - help - with possible commands
                    """;
        } else { //this is when the user isn't signed out
            return """
                    you gotta be signed out to be in this client!
                    """;
        }
    }

    public String login(String... params) throws ResponseException {
        String signedInState = checkSignedOut();
        if (signedInState != null) {
            return signedInState;
        }
        if (params.length == 2) {
            String response = server.handleLogin(new LoginRequest(params[0], params[1]));
            if (response.startsWith("Error")) {
                throw new ResponseException(400, response);
            }
            state = State.SIGNEDIN;
            visitorName = params[0];
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        String signedInState = checkSignedOut();
        if (signedInState != null) {
            return signedInState;
        }
        if (params.length == 3) {
            String response = server.handleRegister(new RegisterRequest(params[0], params[1], params[2]));
            if (response.startsWith("Error")) {
                throw new ResponseException(400, response);
            }
            state = State.SIGNEDIN;
            visitorName = params[0];
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }
}

