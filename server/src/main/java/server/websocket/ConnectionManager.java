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
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();


    public void addSessionToGame(int gameID, Session session){
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSessionFromGame(int gameID, Session session){
        Set<Session> sessions = connections.get(gameID);
        if(sessions != null){
            sessions.remove(session);
            if (sessions.isEmpty()){
                connections.remove(gameID);
            }
        }
    }

    public Set<Session> getSessionForGameID(int gameID){
        return connections.get(gameID);
    }
}
