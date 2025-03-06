package service;

import dataaccess.InMemoryAuthDAO;
import dataaccess.InMemoryGameDAO;
import model.AuthData;
import model.ListGamesRequest;
import model.ListGamesResult;

public class ListGameService {

    private InMemoryGameDAO gameDAO;
    private InMemoryAuthDAO authDAO;

    public ListGameService(InMemoryGameDAO gameDAO, InMemoryAuthDAO authDAO){
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
