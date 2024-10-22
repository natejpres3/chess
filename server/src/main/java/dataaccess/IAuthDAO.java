package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface IAuthDAO {
    void createAuth(AuthData authData) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
    void clear();
}
