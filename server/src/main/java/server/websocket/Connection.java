package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class Connection {
    public String playerName;
    public int gameId;
    public Session session;
    public String authToken;

    public Connection(String playerName, int gameId, String authToken, Session session) {
        this.playerName = playerName;
        this.gameId = gameId;
        this.session = session;
        this.authToken = authToken;
    }

    public int getGameId() {
        return gameId;
    }

    public String getAuthToken(){
        return authToken;
    }


    public void sendMessage(ServerMessage message){
        try{
            String json = new Gson().toJson(message);
            session.getRemote().sendString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}