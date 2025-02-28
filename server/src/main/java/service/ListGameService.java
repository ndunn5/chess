package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.ListGamesRequest;
import model.ListGamesResult;

public class ListGameService {

    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ListGameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest){
        if(authDAO.getAuthDataWithAuthToken(listGamesRequest.authToken()) == null){
            return new ListGamesResult("Error: unauthorized");
        }
        AuthData authData = authDAO.getAuthDataWithAuthToken(listGamesRequest.authToken());
        try{
            return new ListGamesResult(gameDAO.returnAllGames());
        } catch (Exception e){
            return new ListGamesResult(e.getMessage());
        }
    }
}
