package ui;

import chess.ChessBoard;
import exception.ResponseException;
import model.LogoutRequest;
import model.LogoutResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GamePlay {

    private final ServerFacade server;
    private final String serverUrl;

    public GamePlay(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case ("logout") -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void showBoard(ChessBoard boardState, String whiteOrBlack){
        System.out.print("This is where the board will be printed. It needs to take in an object in and also white orientation or black orientation");
    }



    public String help() {
        return """
                - create <NAME> - a game
                - list - games 
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

    public String logout() throws ResponseException {
        try {
            LogoutResult logoutResult = server.handleLogout(new LogoutRequest(PreLoginClient.getAuthToken()));
            Repl.updateState(State.SIGNEDOUT);
            return "You signed out";
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }
}
