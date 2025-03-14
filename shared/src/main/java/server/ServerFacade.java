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
        this.makeRequest("DELETE", path, null, ClearDatabaseResult.class);
    }

    public void handleRegister(RegisterRequest registerRequest) throws ResponseException {
        var path = "/user";
        this.makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public void handleLogin(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public void handleLogout(LogoutRequest logoutRequest) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, logoutRequest, LogoutResult.class);
    }

    public void handleListGames(ListGamesRequest listGamesRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("GET", path, listGamesRequest, ListGamesResult.class);
    }

    public void handleJoinGame(JoinGameRequest joinGameRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, joinGameRequest, JoinGameResult.class);
    }

    public void handleCreateGame(CreateGameRequest createGameRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("POST", path, createGameRequest, CreateGameResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
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
