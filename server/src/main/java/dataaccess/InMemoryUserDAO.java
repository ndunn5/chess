package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserDAO implements UserDAO{
    private Map<String, UserData> users = new HashMap<>();

    public void insertUser(UserData user) throws DataAccessException{
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public void clear(){
        users.clear();
    }

    public boolean isEmpty(){
        return users.isEmpty();
    }


}
