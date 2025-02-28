package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.JoinGameResult;

public class JoinGameService {
    GameDAO gameDAO;
    AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest){
        if (authDAO.getAuthDataWithAuthToken(joinGameRequest.getAuthToken()) == null) {
            return new JoinGameResult("Error: unauthorized");
        }
        if (joinGameRequest.getGameID() <= 0){
            return new JoinGameResult("Error: bad request");
        }
        GameData gameData = gameDAO.getGame(joinGameRequest.getGameID());
        if (gameData == null) {
            return new JoinGameResult("Error: bad request");
        }
        try {
            if(joinGameRequest.getPlayerColor().equals("WHITE")){
                if(gameData.whiteUsername() != null){
                    return new JoinGameResult("Error: already taken");
                }
                gameData = new GameData(gameData.gameID(), authDAO.getAuthDataWithAuthToken(joinGameRequest.getAuthToken()).username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            }else{
                if(gameData.blackUsername() != null){
                    return new JoinGameResult("Error: already taken");
                }
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername() , authDAO.getAuthDataWithAuthToken(joinGameRequest.getAuthToken()).username(), gameData.gameName(), gameData.game());
            }
            gameDAO.updateGame(gameData);
            return new JoinGameResult();
        } catch (Exception e) {
            return new JoinGameResult(e.getMessage());
        }
    }
}
