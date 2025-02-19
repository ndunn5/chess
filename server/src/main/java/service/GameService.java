package service;

import dataaccess.GameDAO;
import model.GameData;


public class GameService {
    private GameDAO gameDAO;

    public GameService(GameDAO gameDAO){
        this.gameDAO = gameDAO;
    }
}

