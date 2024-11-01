package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;

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
                ps.setString(2, encryptPassword(user.password()));
                ps.setString(3, user.email());
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("User already exists");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1,username);
                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username,password,email);
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException("User is not found");
        }
        return null;
    }

    @Override
    public boolean validateAuthToken(String username, String password) throws DataAccessException {
        UserData userData = getUser(username);
        return BCrypt.checkpw(password,userData.password());
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE users";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to delete all users");
        }
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

    private String encryptPassword(String clearTextPassword) {
        String encryptedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return encryptedPassword;
    }

//    boolean verifyUser(String username, String providedClearTextPassword) {
//        // read the previously hashed password from the database
////        var hashedPassword = readHashedPasswordFromDatabase(username);
//
//        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }

}
