package dataaccess;

import model.AuthData;
import model.UserData;

public interface AuthDAO {
    public void insertAuth(AuthData auth, UserData user) throws DataAccessException;
    public UserData getUserDataWithUsername(String username);
    public AuthData getAuthDataWithAuthToken(String authToken);
    public void deleteAuth(AuthData auth, UserData user);
    public boolean isEmpty() throws DataAccessException;
    public void clear() throws DataAccessException;
}
