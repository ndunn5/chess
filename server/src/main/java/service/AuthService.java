package service;

import dataaccess.InMemoryAuthDAO;

import java.util.UUID;



public class AuthService {
    private InMemoryAuthDAO authDAO;

    public AuthService(InMemoryAuthDAO authDAO){
        this.authDAO = authDAO;
    }

    public static String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
