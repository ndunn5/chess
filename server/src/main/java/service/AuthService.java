package service;

import dataaccess.AuthDAO;
import model.AuthData;
import java.util.UUID;


public class AuthService {
    private AuthDAO authDAO;

    public AuthService(AuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

}
