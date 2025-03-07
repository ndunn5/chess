package service;

import dataaccess.AuthDAO;
import model.LoginRequest;
import model.UserData;
import model.AuthData;
import model.LoginResult;


public class LoginService {

    AuthDAO authDAO;

    public LoginService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

//    public LoginResult login(LoginRequest loginRequest){
//        if(authDAO.getUserDataWithUsername(loginRequest.username()) == null){
//            return new LoginResult("Error: unauthorized");
//        }
//        UserData userData = authDAO.getUserDataWithUsername(loginRequest.username());
//        if (!userData.password().equals(loginRequest.password())){
//            return new LoginResult("Error: unauthorized");
//        }
//        try{
//            String authToken = AuthService.generateAuthToken();
//            AuthData authData = new AuthData(authToken, loginRequest.username());
//            authDAO.insertAuth(authData, userData);
//            return new LoginResult(loginRequest.username(), authToken);
//        }
//        catch(Exception e){
//            return new LoginResult(e.getMessage());
//        }
//    }

    public LoginResult login(LoginRequest loginRequest){
        if(authDAO.getUserDataWithUsername(loginRequest.username()) == null){
            return new LoginResult("Error: unauthorized");
        }
        UserData userData = authDAO.getUserDataWithUsername(loginRequest.username());
        if (!HasherHelper.verifyPassword(loginRequest.password(), authDAO.getUserDataWithUsername(loginRequest.username()).password())) {
            return new LoginResult("Error: unauthorized");
        }
        try{
            String authToken = AuthService.generateAuthToken();
            AuthData authData = new AuthData(authToken, loginRequest.username());
            authDAO.insertAuth(authData, userData);
            return new LoginResult(loginRequest.username(), authToken);
        }
        catch(Exception e){
            return new LoginResult(e.getMessage());
        }
    }
}
