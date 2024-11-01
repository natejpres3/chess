package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySQLAuthDAO implements IAuthDAO{

    public MySQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auths (authToken, username) VALUES(?, ?)";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1,authData.authToken());
                ps.setString(2,authData.username());
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("Authorization already taken");
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT (authToken, username) FROM auths WHERE authToken=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        var username = rs.getString("username");
                        return new AuthData(authToken,username);
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
        return null;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE authToken=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1,authToken);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE auths";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to delete all authorization data");
        }
    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS auths (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
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
