package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private Map<String, UserData> users = new HashMap<>();

    public void insertUser(UserData user) {
        users.put(user.username(), user);
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
}
