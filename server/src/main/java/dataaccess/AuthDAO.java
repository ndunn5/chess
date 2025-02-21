package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private Map<String, AuthData> authTokens = new HashMap<>();

    public void insertAuth(AuthData auth) throws DataAccessException{
        authTokens.put(auth.authToken(), auth);
    }

    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public Map<String, AuthData> getAllAuthTokens(){
        return authTokens;
    }

    public boolean deleteAuth(String authToken) {
        if(authTokens.containsKey(authToken)){
            authTokens.remove((authToken));
            return true;
        }
        else{
            return false;
        }
    }

    public void clear(){
        authTokens.clear();
    }
}
