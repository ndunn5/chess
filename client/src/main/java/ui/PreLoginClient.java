package ui;

import model.LoginRequest;
import model.LoginResult;
import model.RegisterRequest;
import model.RegisterResult;
import server.ServerFacade;
import exception.ResponseException;

import java.util.Arrays;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken = null;
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
            try{
                LoginResult loginResult = server.handleLogin(new LoginRequest(params[0], params[1]));
                state = State.SIGNEDIN;
                visitorName = params[0];
                authToken = loginResult.authToken();
                return String.format("You signed in as %s.", visitorName);
            }
            catch (ResponseException e){
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        String signedInState = checkSignedOut();
        if (signedInState != null) {
            return signedInState;
        }
        if (params.length == 3) {
            try{
                RegisterResult registerResult = server.handleRegister(new RegisterRequest(params[0], params[1], params[2]));
                state = State.SIGNEDIN;
                visitorName = params[0];
                authToken = registerResult.authToken();
                return String.format("You signed in as %s.", visitorName);
            } catch (ResponseException e){
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String getAuthToken(){
        return authToken;
    }
}

