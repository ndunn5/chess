package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private Map<String, UserData> users = new HashMap<>();

    public void insertUser(UserData user) throws DataAccessException{
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }

    public Map<String, UserData> getAllUsers(){
        return users;
    }

    public boolean deleteUser(String username) {
        if(users.containsKey(username)){
            users.remove((username));
            return true;
        }
        else{
            return false;
        }
    }

    public void clear(){
        users.clear();
    }


}
