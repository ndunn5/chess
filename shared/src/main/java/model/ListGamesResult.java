package model;

import java.util.HashMap;
import java.util.Map;

public record ListGamesResult (Map<Integer, GameData> allGames, String message) {
    public ListGamesResult(Map<Integer, GameData> allGames){
        this(allGames, null);
    }
    public ListGamesResult(String message){
        this(null, message);
    }
}
