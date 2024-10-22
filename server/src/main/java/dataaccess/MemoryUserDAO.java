package dataaccess;

import model.UserData;

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

    @Override
    public void deleteUser(UserData user) throws DataAccessException {
        //find the user to remove if it's not null it will be removed
        UserData removeUser = users.get(user.username());
        if(removeUser == null) {
            throw new DataAccessException("The user already doesn't exist");
        }
        users.remove(removeUser);
    }

    @Override
    public void clear() {
        users.clear();
    }
}
