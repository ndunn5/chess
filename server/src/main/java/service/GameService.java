package service;

import dataaccess.InMemoryGameDAO;


public class GameService {
    private InMemoryGameDAO gameDAO;

    public GameService(InMemoryGameDAO gameDAO){
        this.gameDAO = gameDAO;
    }
}

