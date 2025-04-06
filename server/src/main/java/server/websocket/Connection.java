package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String playerName;
    public int gameId;
    public Session session;

    public Connection(String playerName, int gameId, Session session) {
        this.playerName = playerName;
        this.gameId = gameId;
        this.session = session;
    }

    public int getGameId() {
        return gameId;
    }

    public String getPlayerName(){
        return playerName;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public boolean isOpen() {
        return session.isOpen();
    }
}