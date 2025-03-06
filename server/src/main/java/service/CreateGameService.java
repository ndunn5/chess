package service;

import chess.ChessGame;
import dataaccess.InMemoryAuthDAO;
import dataaccess.InMemoryGameDAO;
import model.AuthData;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;
import java.util.Random;

public class CreateGameService {

    InMemoryGameDAO gameDAO;
    InMemoryAuthDAO authDAO;
    int gameID = 0;
    Random random = new Random();

    public CreateGameService(InMemoryGameDAO gameDAO, InMemoryAuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest){
        if(authDAO.getAuthDataWithAuthToken(createGameRequest.authToken()) == null){
            return new CreateGameResult("Error: unauthorized");
        }
        try{
            AuthData authData = authDAO.getAuthDataWithAuthToken(createGameRequest.authToken());
            gameID = random.nextInt(Integer.MAX_VALUE);
            gameDAO.insertGame(new GameData(gameID, null, null, createGameRequest.gameName(), new ChessGame()));
            return new CreateGameResult(gameID);
        }
        catch (Exception e){
            return new CreateGameResult(e.getMessage());
        }
    }
}
