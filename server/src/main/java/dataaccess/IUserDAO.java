package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.List;

public interface IUserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean validateAuthToken(String username, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}
