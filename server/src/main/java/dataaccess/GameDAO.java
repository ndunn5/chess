package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    public void insertGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public void updateGame(GameData game) throws DataAccessException;
    public List<Map<String, Object>> returnAllGames() throws DataAccessException;
    public void clear() throws DataAccessException;
    public boolean isEmpty() throws DataAccessException;
}
