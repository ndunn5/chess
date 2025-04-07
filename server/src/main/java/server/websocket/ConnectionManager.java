package server.websocket;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

//sessionMap: map<GameID, Set<Session>>
//addSessionToGame(gameID, session): void
//removeSessionFromGame(gameID, session): void
//getSessionForGame(gameID): Set<Session>


public class ConnectionManager {
//    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();


    public void addSessionToGame(int gameID, Connection connection){
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(connection);
    }

    public void removeSessionFromGame(int gameID, Connection connection){
        Set<Connection> conectionSet = connections.get(gameID);
        if(conectionSet != null){
            conectionSet.remove(connection);
            if (conectionSet.isEmpty()){
                connections.remove(gameID);
            }
        }
    }

    public Set<Connection> getSessionForGameID(int gameID){
        return connections.get(gameID);
    }
}
