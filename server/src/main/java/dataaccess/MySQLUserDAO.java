package dataaccess;

import model.UserData;

import java.lang.module.ResolutionException;
import java.sql.SQLException;

public class MySQLUserDAO  implements IUserDAO{

    public MySQLUserDAO() throws DataAccessException{
        configureDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("User already exists");
        }
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
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255),
            PRIMARY KEY (username)
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
