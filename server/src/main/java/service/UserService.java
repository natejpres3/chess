package service;

import dataaccess.DataAccessException;
import dataaccess.IAuthDAO;
import dataaccess.IUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class UserService {
    private final IUserDAO userDAO;
    private final IAuthDAO authDAO;

    public UserService(IUserDAO userDAO, IAuthDAO authDAO) {
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

//    public void createUser(UserData user) {
//        memoryUserDAO.createUser(user);
//    }
}
