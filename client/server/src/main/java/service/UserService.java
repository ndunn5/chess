package service;

import dataaccess.UserDAO;
import model.GameData;


public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }
}

