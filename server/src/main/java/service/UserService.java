package service;

import dataaccess.InMemoryUserDAO;


public class UserService {
    private InMemoryUserDAO userDAO;

    public UserService(InMemoryUserDAO userDAO){
        this.userDAO = userDAO;
    }
}

