package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {
    private int nextGameID = 1;
    private Map<Integer, GameData> allGames = new HashMap<>();

    public int insertGame(GameData game) throws DataAccessException{
        int thisGameID = nextGameID;
        allGames.put(thisGameID, new GameData(thisGameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
        nextGameID ++;
        return thisGameID;
    }

    public GameData getGame(int gameID){
        return allGames.get(gameID);
    }

    public boolean deleteGame(int gameID){
        if(allGames.containsKey(gameID)){
            allGames.remove(gameID);
            return true;
        }
        else{
            return false;
        }
    }

    public void updateGame(GameData game){
        if (allGames.containsKey(game.gameID())){
            allGames.put(game.gameID(), game);
        }
    }

    public Map<Integer, GameData> returnAllGames(){
        return allGames;
    }

    public void clear(){
        allGames.clear();
    }

}
