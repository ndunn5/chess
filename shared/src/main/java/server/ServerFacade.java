package server;
import com.google.gson.Gson;
import extramodel.ClearDatabaseResult;
import extramodel.JoinGameRequest;
import model.ClearDatabaseRequest;
import exception.ErrorResponse;
import exception.ResponseException;
import model.*;
import java.io.*;
import java.net.*;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void handleClearDatabase() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, ClearDatabaseResult.class, null);
    }

    public RegisterResult handleRegister(RegisterRequest registerRequest) throws ResponseException {
        var path = "/user";
        try{
            RegisterResult registerResult = this.makeRequest("POST", path, registerRequest, RegisterResult.class, null);
            return registerResult;
        } catch (ResponseException e){
            throw new ResponseException(400, e.getMessage());
        }
    }

    public LoginResult handleLogin(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        try{
            LoginResult loginResult = this.makeRequest("POST", path, loginRequest, LoginResult.class, null);

            return loginResult;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public LogoutResult handleLogout(LogoutRequest logoutRequest) throws ResponseException {
        var path = "/session";
        try{
            LogoutResult logoutResult = this.makeRequest("DELETE", path, logoutRequest, LogoutResult.class, logoutRequest.authToken());
            return logoutResult;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public ListGamesResult handleListGames(ListGamesRequest listGamesRequest) throws ResponseException {
        var path = "/game";
        try{
            ListGamesResult listGamesResult = this.makeRequest("GET", path, null, ListGamesResult.class, listGamesRequest.authToken());
            return listGamesResult;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public JoinGameResult handleJoinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        var path = "/game";
        try{
            JoinGameResult joinGameResult = this.makeRequest("PUT", path, joinGameRequest, JoinGameResult.class, joinGameRequest.getAuthToken());
            return joinGameResult;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    public CreateGameResult handleCreateGame(CreateGameRequest createGameRequest) throws ResponseException {
        var path = "/game";
        try{
            CreateGameResult createGameResult = this.makeRequest("POST", path, createGameRequest, CreateGameResult.class, createGameRequest.authToken());
            return createGameResult;
        } catch (ResponseException e) {
            throw new ResponseException(400, e.getMessage());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeHeader(authToken, http);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error message: " + ex.getMessage());
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private static void writeHeader(String authToken, HttpURLConnection http) throws IOException {
        if (authToken != null && !authToken.isEmpty()) {
            http.addRequestProperty("Authorization", authToken);
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }
}
