package dataaccess;

import model.UserData;

import java.util.List;
import java.util.HashMap;

public class InMemoryUserDAO implements IUserDAO{
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
    public void updateUser(UserData user) throws DataAccessException {

    }

    @Override
    public void deleteUser(UserData user) throws DataAccessException {

    }

    @Override
    public List<UserData> getAllUsers() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() throws DataAccessException {

    }
}
