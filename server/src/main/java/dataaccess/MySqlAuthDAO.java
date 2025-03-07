package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    public boolean isEmpty() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM auth";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error checking isEmpty:" + ex.getMessage());
        }
        return false;
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing auth table: " + ex.getMessage());
        }
    }

    private final String[] createStatements = {
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
