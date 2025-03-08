package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.DatabaseHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import model.UserData;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() {
        try {
            DatabaseHelper.configureDatabase(createStatements);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error with GameDAO constructor", ex);
        }
    }

    private Map<Integer, GameData> allGames = new HashMap<>();

    public void insertGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, game.gameID());
            preparedStatement.setString(2, game.whiteUsername());
            preparedStatement.setString(3, game.blackUsername());
            preparedStatement.setString(4, game.gameName());
            Gson gson = new Gson();
            String gameJson = gson.toJson(game.game());
            preparedStatement.setString(5, gameJson);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error inserting game: " + ex.getMessage());
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?;";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, gameID);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    Gson gson = new Gson();

                    return new GameData(resultSet.getInt("gameID"), resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"), resultSet.getString("gameName"),
                            gson.fromJson(resultSet.getString("game"), ChessGame.class));
                }
                else{
                    return null;
                }
            }
        }
        catch (SQLException | DataAccessException ex) {
            throw new DataAccessException("Error getting game from gameID: " + ex.getMessage());
        }
    }


    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)){

            preparedStatement.setString(1, game.whiteUsername());
            preparedStatement.setString(2, game.blackUsername());
            preparedStatement.setString(3, game.gameName());
            preparedStatement.setString(4, new Gson().toJson(game.game()));
            preparedStatement.setInt(5, game.gameID());
            preparedStatement.executeUpdate();
        } catch(Exception ex){
            throw new DataAccessException("Error updating game: " + ex.getMessage());
        }
    }

    public List<Map<String, Object>> returnAllGames() throws DataAccessException {
        List<Map<String, Object>> gamesList = new ArrayList<>();
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()){
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("gameID", resultSet.getInt("gameID"));
                    tempMap.put("whiteUsername", resultSet.getString("whiteUsername"));
                    tempMap.put("blackUsername", resultSet.getString("blackUsername"));
                    tempMap.put("gameName", resultSet.getString("gameName"));
                    tempMap.put("game", resultSet.getString("game"));
                    gamesList.add(tempMap);
                }
            return gamesList;
        } catch (Exception ex) {
            throw new DataAccessException("Error returning all games: " + ex.getMessage());
        }
    }

        public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing games table: " + ex.getMessage());
        }
    }

    public boolean isEmpty() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM games";

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

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS games (
      gameID INT NOT NULL,
      whiteUsername VARCHAR(256),
      blackUsername VARCHAR(256),
      gameName VARCHAR(256) NOT NULL,
      game TEXT NOT NULL,
      PRIMARY KEY (gameID),
      INDEX(gameID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    """
    };

}
