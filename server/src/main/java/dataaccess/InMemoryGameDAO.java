package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.util.ArrayList;


public class InMemoryGameDAO implements GameDAO{
    private Map<Integer, GameData> allGames = new HashMap<>();

    public void insertGame(GameData game) throws DataAccessException{
        allGames.put(game.gameID(), new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
    }

    public GameData getGame(int gameID){
        return allGames.get(gameID);
    }


    public void updateGame(GameData game){
        if (allGames.containsKey(game.gameID())){
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

    public void clear(){
        allGames.clear();
    }

    public boolean isEmpty(){
        return allGames.isEmpty();
    }

}
