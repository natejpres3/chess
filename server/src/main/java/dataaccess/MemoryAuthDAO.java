package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements IAuthDAO{
    private final HashMap<String, AuthData> auths = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if(auths.containsKey(authData.authToken())) {
            throw new DataAccessException("This authToken already exists");
        }
        auths.put(authData.authToken(),authData);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        AuthData auth = auths.get(authToken);
        if(auth == null) {
            throw new DataAccessException("This authToken doesn't exist");
        }
        return auth;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        if(!auths.containsKey(authToken)) {
            throw new DataAccessException("This authToken already doesn't exist");
        }
        auths.remove(authToken);
    }

    @Override
    public void clear() {
        auths.clear();
    }
}