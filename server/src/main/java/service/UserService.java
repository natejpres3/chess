package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws DataAccessException {
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            throw new DataAccessException("");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData loginUser(UserData userData) throws DataAccessException{
        boolean validAuthToken;
        try {
            validAuthToken = userDAO.validateAuthToken(userData.username(),userData.password());
        } catch (DataAccessException e) {
            throw new DataAccessException("");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken,userData.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public void logoutUser(String authToken) throws DataAccessException {
        //find the user using the authToken and remove it from database. Otherwise throw an error.
        try {
            authDAO.getAuthData(authToken);
        } catch(DataAccessException e) {
            throw new DataAccessException("");
        }
        authDAO.deleteAuthToken(authToken);
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
