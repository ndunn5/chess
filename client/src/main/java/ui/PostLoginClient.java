package ui;

import chess.ChessBoard;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import extramodel.JoinGameRequest;
import model.*;
import server.ServerFacade;
import ui.websocket.GameHandler;
import ui.websocket.GameUI;
import ui.websocket.WebSocketFacade;
import websocket.commands.ConnectMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PostLoginClient {
    private final ServerFacade server;
    private final String serverUrl;
    private Map<Integer, Map<String, Object>> screenIDToGameDetails = new HashMap<>();
    private GamePlay gamePlay;
    private GameHandler gameHandler;
    public String currentColor = "WHITE";

    public PostLoginClient(String serverUrl, GameHandler gameHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameHandler = gameHandler;
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
                case "observe" -> observeGame(params);
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
                return String.format("created game: %s", params[0]);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String listGames() throws ResponseException {
        try {
            ListGamesResult listGamesResult = server.handleListGames(new ListGamesRequest(PreLoginClient.getAuthToken()));
            if (listGamesResult.games().isEmpty()) {
                return "No current games. Please Create a Game.";
            }
            String returnString = "";
            int screenID = 1;
            for (Map<String, Object> game : listGamesResult.games()) {
                String gameName = (String) game.get("gameName");
                String whiteUser = (String) game.get("whiteUsername");
                if (whiteUser == null) {
                    whiteUser = "You can join as White";
                }
                String blackUser = (String) game.get("blackUsername");
                if (blackUser == null) {
                    blackUser = "You can join as Black";
                }
                returnString += screenID + "\t" + gameName + "\n" +
                        "\tWhite User: " + whiteUser + "\n" +
                        "\tBlack User: " + blackUser + "\n"
                ;
                screenIDToGameDetails.put(screenID, game);
                screenID++;
            }
            return returnString;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public String getCurrentColor(){
        return currentColor;
    }

    public String joinGame(String... params) throws ResponseException {
        if (screenIDToGameDetails.isEmpty()) {
            return "please list games first";
        }
        if (params.length == 2) {
            try {
                int screenID = Integer.parseInt(params[0]);
                Map<String, Object> gameDetails = screenIDToGameDetails.get(screenID);
                if (gameDetails == null) {
                    return "Invalid ID number";
                }
                Number gameIDObj = (Number) gameDetails.get("gameID");
                int gameID = gameIDObj.intValue();
                String currentBoard = (String) gameDetails.get("game");
                currentColor = params[1];


                JoinGameRequest joinGameRequest = new JoinGameRequest(params[1], gameID);
                joinGameRequest.addAuthToken(PreLoginClient.getAuthToken());
                JoinGameResult joinGameResult = server.handleJoinGame(joinGameRequest);

                WebSocketFacade ws = new WebSocketFacade(serverUrl, gameHandler);

                ws.connect(new ConnectMessage(joinGameRequest.getAuthToken(), gameID,params[1]));
                Repl.updateState(State.GAMEPLAY);
                return "";
//                return gameHandler.showBoard(currentBoard, params[1]);
            } catch (ResponseException e) {
                throw new ResponseException(400, e.getMessage());
            }
        }
        throw new ResponseException(400, "Expected: <ID> <WHITE|BLACK>");
    }





    public String observeGame(String... params) throws ResponseException {
        if (screenIDToGameDetails.isEmpty()) {
            return "please list games first";
        }
        if (params.length == 1) {
            int screenID = Integer.parseInt(params[0]);
            Map<String, Object> gameDetails = screenIDToGameDetails.get(screenID);
            if (gameDetails == null) {
                return "Invalid ID number";
            }
            Number gameIDObj = (Number) gameDetails.get("gameID");
            int gameID = gameIDObj.intValue();
            String currentBoard = (String) gameDetails.get("game");

            Repl.updateState(State.GAMEPLAY);
            //convert currentBoard to real board here


            Gson gson = new Gson();
            JsonObject jsonObject = JsonParser.parseString(currentBoard).getAsJsonObject();
            JsonObject boardObject = jsonObject.getAsJsonObject("board");
            ChessBoard board = gson.fromJson(boardObject, ChessBoard.class);

            return gameHandler.showBoard(board, "WHITE");
        }
        throw new ResponseException(400, "Expected: <ID>");
    }
}
