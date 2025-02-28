package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ListGamesResult (List<Map<String, Object>> games, String message) {
    public ListGamesResult(List<Map<String, Object>> games){
        this(games, null);
    }
    public ListGamesResult(String message){
        this(null, message);
    }
}
