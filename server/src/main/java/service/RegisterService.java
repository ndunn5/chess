package service;

import dataaccess.DataAccessException;
import model.RegisterRequest;
import model.RegisterResult;
import model.UserData;
import model.AuthData;
import dataaccess.InMemoryUserDAO;
import dataaccess.InMemoryAuthDAO;


public class RegisterService {

    RegisterRequest registerRequest;
    private InMemoryUserDAO userDAO;
    private InMemoryAuthDAO authDAO;


    public RegisterService(InMemoryUserDAO userDAO, InMemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if (registerRequest.username() == null || registerRequest.username().isBlank() ||
                registerRequest.password() == null || registerRequest.password().isBlank() ||
                registerRequest.email() == null || registerRequest.email().isBlank()) {
            return new RegisterResult("Error: bad request");
        }

        try {
            if (userDAO.getUser(registerRequest.username()) != null)
            {
                return new RegisterResult("Error: already taken");
            }

            UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDAO.insertUser(userData);

            String authToken = AuthService.generateAuthToken();
            AuthData authData = new AuthData(authToken, registerRequest.username());
            authDAO.insertAuth(authData, userData);

            return new RegisterResult(registerRequest.username(), authToken);
        } catch (DataAccessException e) {
            return new RegisterResult(e.getMessage());
        }
    }
}

