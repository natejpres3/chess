package dataaccess;

import model.UserData;

import java.util.List;

public interface IUserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(UserData user) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
    void clear() throws DataAccessException;
}
