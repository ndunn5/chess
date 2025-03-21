package ui;

import chess.ChessGame;
import exception.ResponseException;
import extramodel.JoinGameRequest;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private Map<Integer, Integer> listGameIdsToRealGameIDs = new HashMap<>();

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
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
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

    public String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            try {
                CreateGameResult createGameResult = server.handleCreateGame(new CreateGameRequest(PreLoginClient.getAuthToken(), params[0]));
                return String.format("created game, %s", params[0]);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {//if its empty then lets print there arent any games
        try {
            ListGamesResult listGamesResult = server.handleListGames(new ListGamesRequest(PreLoginClient.getAuthToken()));
            String returnString = "";
            int screenID = 1;
            for (Map<String, Object> game : listGamesResult.games()) {
                String gameName = (String) game.get("gameName");
                returnString += screenID + "\t" + gameName + "\n";
                Number gameIDObj = (Number) game.get("gameID");
                int gameID = gameIDObj.intValue();
                listGameIdsToRealGameIDs.put(screenID, gameID);
                screenID++;
            }
            return returnString;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public String joinGame(String... params) throws ResponseException { //if I havent listed them then handle that
        if (params.length == 2) {
            try {
                int screenID = Integer.parseInt(params[0]);
                int gameID = listGameIdsToRealGameIDs.get(screenID);
                JoinGameRequest joinGameRequest = new JoinGameRequest(params[1], gameID);
                joinGameRequest.addAuthToken(PreLoginClient.getAuthToken());
                JoinGameResult joinGameResult = server.handleJoinGame(joinGameRequest);
                return String.format("joined game: %s", gameID);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <ID> <WHITE|BLACK>");
    }
}
