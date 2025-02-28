package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDAO {

    private Map<String, UserData> usernames = new HashMap<>();
    private Map<String, String> authTokens = new HashMap<>();

    public void insertAuth(AuthData auth, UserData user) throws DataAccessException{
        usernames.put(auth.username(), user);
        authTokens.put(auth.authToken(), auth.username());
    }

    public UserData getUserDataWithUsername(String username) {
        return usernames.get(username);
    }

    public String getUsernameWithAuthToken(String authToken) {
        return authTokens.get(authToken);
    }

    public boolean deleteAuth(String authToken) {
        String returnUsername = authTokens.remove(authToken);
        if (returnUsername != null) {
            usernames.remove(returnUsername);
            return true;
        }
        return false;
    }

    public boolean isEmpty(){
        return usernames.isEmpty();
    }

    public void clear(){
        authTokens.clear();
        usernames.clear();
    }
}
