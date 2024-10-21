package dataaccess;

import model.UserData;

import java.util.List;

public interface IUserDAO {
    void addUser(UserData user) throws DataAccessException;
    UserData getUserByUsername(String username) throws DataAccessException;
    void updateUser(UserData user) throws DataAccessException;
    void deleteUser(UserData user) throws DataAccessException;
    List<UserData> getAllUsers() throws DataAccessException;
}
