package dataaccess;

import model.UserData;

public interface UserDAO {
    public void insertUser(UserData user) throws DataAccessException;
    public UserData getUser(String username);
    public void clear() throws DataAccessException;
    public boolean isEmpty();
}
