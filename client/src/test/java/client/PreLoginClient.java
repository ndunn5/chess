package client;

import server.ServerFacade;

public class PreLoginClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

}
