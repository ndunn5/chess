package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    public void insertGame(GameData game) throws DataAccessException;
    public GameData getGame(int gameID);
    public void updateGame(GameData game);
    public List<Map<String, Object>> returnAllGames();
    public void clear() throws DataAccessException;
    public boolean isEmpty();
}
