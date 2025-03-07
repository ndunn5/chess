package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    public void insertAuth(AuthData auth, UserData user) throws DataAccessException;
    public UserData getUserDataWithUsername(String username) throws DataAccessException;
    public AuthData getAuthDataWithAuthToken(String authToken) throws DataAccessException;
    public void deleteAuth(AuthData auth, UserData user) throws DataAccessException;
    public boolean isEmpty() throws DataAccessException;
    public void clear() throws DataAccessException;
}
