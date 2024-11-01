package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.sql.SQLException;

public class SQLAuthTest {
    MySQLAuthDAO authDAO;
    AuthData authData;

    @BeforeEach
    void getStarted() throws DataAccessException, SQLException {
        authDAO = new MySQLAuthDAO();
        authData = new AuthData("myAuthToken", "username");
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auths";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    @AfterEach
    void clearDB() throws DataAccessException, SQLException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auths";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    @Test
    void createAuthPositive() throws DataAccessException, SQLException {
        AuthData retrievedAuthData;
        authDAO.createAuth(authData);
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    rs.next();
                    var authToken = rs.getString("authToken");
                    var username = rs.getString("username");
                    retrievedAuthData = new AuthData(authToken, username);
                }
            }
        }
        assertEquals(authData, retrievedAuthData);
    }

    @Test
    void createAuthNegative() throws DataAccessException {
        authDAO.createAuth(authData);
        assertThrows(DataAccessException.class, ()->authDAO.createAuth(authData));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        authDAO.createAuth(authData);
        var resultGetAuth = authDAO.getAuthData(authData.authToken());
        assertEquals(resultGetAuth,authData);
    }

    @Test
    void getAuthNegative() throws DataAccessException {
        authDAO.createAuth(authData);
        AuthData invalidAuthData = new AuthData("invalidAuthToken", "username");
        assertThrows(DataAccessException.class, ()->authDAO.getAuthData(invalidAuthData.authToken()));
    }

    @Test
    void deleteAuthTokenPositive() throws DataAccessException, SQLException {
        authDAO.createAuth(authData);
        authDAO.deleteAuthToken(authData.authToken());
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auths";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }

    @Test
    void deleteAuthTokenNegative() throws DataAccessException {
        authDAO.createAuth(authData);
        assertThrows(DataAccessException.class, ()->authDAO.deleteAuthToken("thisisabadauthToken"));
    }
}