package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySqlAuthDAO implements AuthDAO{
    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    private Map<String, UserData> usernames = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();

    public void insertAuth(AuthData auth, UserData user) throws DataAccessException{
        usernames.put(auth.username(), user);
        authTokens.put(auth.authToken(), auth);
    }

    public UserData getUserDataWithUsername(String username) {
        return usernames.get(username);
    }

    public AuthData getAuthDataWithAuthToken(String authToken) {
        return authTokens.get(authToken);
    }

    public void deleteAuth(AuthData auth, UserData user) {
        authTokens.remove(auth.authToken());
        usernames.remove(user.username());
    }

    public boolean isEmpty(){
        return usernames.isEmpty();
    }

    public void clear(){
        authTokens.clear();
        usernames.clear();
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS users (
        username VARCHAR(256) NOT NULL PRIMARY KEY,
        password VARCHAR(512) NOT NULL,
        email VARCHAR(256) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """,
            """
    CREATE TABLE IF NOT EXISTS auth (
        authToken VARCHAR(512) NOT NULL PRIMARY KEY,
        username VARCHAR(256) NOT NULL,
        FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }



}
