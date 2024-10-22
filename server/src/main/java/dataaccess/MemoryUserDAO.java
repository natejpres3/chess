package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryUserDAO implements IUserDAO{
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(users.containsKey(user.username())) {
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(),user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if(user == null) {
            throw new DataAccessException("User does not exist");
        }
        return user;
    }

    public boolean validateAuthToken(String username, String password) throws DataAccessException{
        boolean userThere = false;
        if(users.containsKey(username)) {
            userThere = true;
            if(users.get(username).password().equals(password)) {
                return true;
            }
        }
        if(userThere) {
            return false;
        } else {
            throw new DataAccessException("User does not exist");
        }
    }

    public Collection<UserData> listUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clear() {
        users.clear();
    }
}
