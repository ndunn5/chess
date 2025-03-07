package dataaccess;

import model.UserData;
import java.sql.*;
import service.DatabaseHelper;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        DatabaseHelper.configureDatabase(createStatements);
    }

    public UserData getUser(String username) throws DataAccessException {
        return DatabaseHelper.getUserDataWithUsernameHelper(username);
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";
        String resetAutoIncrement = "ALTER TABLE users AUTO_INCREMENT = 1";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
            try(PreparedStatement resetStatement = conn.prepareStatement(resetAutoIncrement)){
                resetStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing users table: " + ex.getMessage());
        }
    }

    public boolean isEmpty() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM users";

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


    public void insertUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, user.password());
            preparedStatement.setString(3, user.email());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error inserting user: " + ex.getMessage());
        }
    }



    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL UNIQUE,
              `password` VARCHAR(512) NOT NULL,
              `email` VARCHAR(256) NOT NULL UNIQUE,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}