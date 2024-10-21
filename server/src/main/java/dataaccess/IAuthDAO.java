package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface IAuthDAO {
    void insertAuthToken(AuthData authData) throws DataAccessException;
    AuthData getAuthData(String authToken) throws DataAccessException;
    void deleteAuthToken(String authToken) throws DataAccessException;
}
