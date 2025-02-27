package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDAO {

    private Map<String, String> usernames = new HashMap<>();
    private Map<String, String> authTokens = new HashMap<>();

    public void insertAuth(AuthData auth) throws DataAccessException{
        usernames.put(auth.username(), auth.authToken());
        authTokens.put(auth.authToken(), auth.username());
    }

    public String getAuthTokenWithUsername(String username) {
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

    public void clear(){
        authTokens.clear();
    }
}
