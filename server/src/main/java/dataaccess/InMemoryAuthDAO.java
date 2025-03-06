package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthDAO implements AuthDAO{

    private Map<String, UserData> usernames = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();

    public void insertAuth(AuthData auth, UserData user) throws DataAccessException{
        usernames.put(auth.username(), user);
        authTokens.put(auth.authToken(), auth);
    }

    public UserData getUserDataWithUsername(String username) {
        return usernames.get(username);
    }

    public AuthData getAuthDataWithAuthToken(String authToken) {
        return authTokens.get(authToken);
    }

    public void deleteAuth(AuthData auth, UserData user) {
        authTokens.remove(auth.authToken());
        usernames.remove(user.username());
    }

    public boolean isEmpty(){
        return usernames.isEmpty();
    }

    public void clear(){
        authTokens.clear();
        usernames.clear();
    }
}
