package service;

import dataaccess.InMemoryUserDAO;
import dataaccess.InMemoryGameDAO;
import dataaccess.InMemoryAuthDAO;
import model.ClearDatabaseRequest;
import extramodel.ClearDatabaseResult;

public class ClearService {
    private InMemoryUserDAO userDAO;
    private InMemoryGameDAO gameDAO;
    private InMemoryAuthDAO authDAO;

    public ClearService(InMemoryUserDAO userDAO, InMemoryGameDAO gameDAO, InMemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ClearDatabaseResult clear(ClearDatabaseRequest request){
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
            return new ClearDatabaseResult(200);
        } catch (Exception e){
            System.out.print("Error with clearing data. Message: " + e.getMessage());
            return new ClearDatabaseResult(500, e.getMessage());
        }
    }
}
