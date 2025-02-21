package service;
import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;

public class ClearService {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public boolean clear(){
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
            return true;
        } catch (Exception e){
            System.out.print("Error with clearing data. Message: " + e.getMessage());
            return false;
        }
    }
}
