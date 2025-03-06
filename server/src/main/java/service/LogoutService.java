package service;

import dataaccess.InMemoryAuthDAO;
import model.AuthData;
import model.LogoutRequest;
import model.LogoutResult;
import model.UserData;

public class LogoutService {
    InMemoryAuthDAO authDAO;

    public LogoutService(InMemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public LogoutResult logout(LogoutRequest logoutRequest){
        if(authDAO.getAuthDataWithAuthToken(logoutRequest.authToken()) == null){
            return new LogoutResult("Error: unauthorized");
        }
        AuthData authData = authDAO.getAuthDataWithAuthToken(logoutRequest.authToken());
        try{
            UserData userData = authDAO.getUserDataWithUsername(authData.username());
            authDAO.deleteAuth(authData, userData);
            return new LogoutResult(null);
        }
        catch(Exception e){
            return new LogoutResult(e.getMessage());
        }
    }
}
