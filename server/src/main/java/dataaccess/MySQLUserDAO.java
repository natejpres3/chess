package dataaccess;

import model.UserData;

import java.lang.module.ResolutionException;
import java.sql.SQLException;

public class MySQLUserDAO  implements IUserDAO{

    public MySQLUserDAO() {
        try {
            configureDatabase();
        } catch(DataAccessException e) {
            throw new ResolutionException();
        }
        try {

        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public boolean validateAuthToken(String username, String password) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() {

    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS users (
            'id' 
            
            """;

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try(var conn = DatabaseManager.getConnection()) {
            try(var preparedStatement = conn.prepareStatement(createStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("Unable to configure database");
        }
    }
}
