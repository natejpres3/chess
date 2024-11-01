package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.UUID;


public class UserService {
    private final MySQLUserDAO userDAO;
    private final MySQLAuthDAO authDAO;

    public UserService(MySQLUserDAO userDAO, MySQLAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData userData) throws DataAccessException,BadRequestException {
        if(userData.username() == null || userData.password() == null) {
            throw new BadRequestException("");
        }
        if(userDAO.getUser(userData.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        userDAO.createUser(userData);

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public AuthData loginUser(UserData userData) throws DataAccessException{
        boolean validAuthToken;
        try {
            validAuthToken = validateAuthToken(userData);
            if(!validAuthToken) {
                throw new DataAccessException("Wrong password");
            }
        } catch (DataAccessException e) {
            throw new DataAccessException("");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken,userData.username());
        authDAO.createAuth(authData);

        return authData;
    }

    public void logoutUser(String authToken) throws DataAccessException, UnauthorizedException {
        //find the user using the authToken and remove it from database. Otherwise throw an error.
        try {
            AuthData authData = authDAO.getAuthData(authToken);
            if(authData == null) {
                throw new DataAccessException("Invalid auth");
            }
            boolean authorized = authDAO.authenicateToken(authData);
            if(!authorized) {
                throw new DataAccessException("Error: unauthorized");
            }
        } catch(DataAccessException e) {
            throw new UnauthorizedException("unauthorized");
        }
        authDAO.deleteAuthToken(authToken);
    }

    public Collection<UserData> listUsers() throws DataAccessException {
        return userDAO.listUsers();
    }

    public boolean validateAuthToken(UserData userData) throws DataAccessException{
//        boolean valid = userDAO.validateAuthToken(userData.username(),userData.password());
//        if(valid) {
//            return true;
//        } else {
//            throw new DataAccessException("Invalid authToken");
//        }
        if(userData.password() != null && userData.username() != null) {
            return userDAO.validateAuthToken(userData.username(), userData.password());
        }
        return false;
    }

    public boolean authenicateToken(AuthData authData) throws DataAccessException {
        return authDAO.authenicateToken(authData);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }
}
