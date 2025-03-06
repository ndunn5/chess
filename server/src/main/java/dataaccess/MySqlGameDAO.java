package dataaccess;

import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlGameDAO implements GameDAO {
    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    private Map<Integer, GameData> allGames = new HashMap<>();

    public void insertGame(GameData game) throws DataAccessException {
        allGames.put(game.gameID(), new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
    }

    public GameData getGame(int gameID){
        return allGames.get(gameID);
    }


    public void updateGame(GameData game) {
        if (allGames.containsKey(game.gameID())) {
            allGames.put(game.gameID(), game);
        }
    }

    public List<Map<String, Object>> returnAllGames() {
        List<Map<String, Object>> gamesList = new ArrayList<>();

        for (GameData game : allGames.values()) {
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("gameID", game.gameID());
            gameMap.put("whiteUsername", game.whiteUsername());
            gameMap.put("blackUsername", game.blackUsername());
            gameMap.put("gameName", game.gameName());
            gameMap.put("game", game.game());
            gamesList.add(gameMap);
        }

        return gamesList;
    }

    public void clear() throws DataAccessException{
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error clearing games table: " + ex.getMessage());
        }
    }

    public boolean isEmpty() {
        return allGames.isEmpty();
    }

    private final String[] createStatements = {
            """
    CREATE TABLE IF NOT EXISTS games (
      gameID INT NOT NULL AUTO_INCREMENT,
      whiteUsername VARCHAR(256),
      blackUsername VARCHAR(256),
      gameName VARCHAR(256) NOT NULL,
      game TEXT NOT NULL,
      PRIMARY KEY (gameID),
      INDEX(gameID)
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
