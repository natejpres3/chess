package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class SQLUserTest {
    MySQLUserDAO userDAO;
    UserData userData;

    @BeforeEach
    void getStarted() throws DataAccessException, SQLException {
        userDAO = new MySQLUserDAO();
        userData = new UserData("username", "password", "email@byu.edu");
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE users";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    @Test
    void createUserPositive() throws DataAccessException, SQLException {
        userDAO.createUser(userData);
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    void createUserNegative() throws DataAccessException {
        userDAO.createUser(userData);
        assertThrows(DataAccessException.class, ()->userDAO.createUser(userData));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        userDAO.createUser(userData);
        UserData retrievedUserData = userDAO.getUser(userData.username());
        assertEquals(userData.username(), retrievedUserData.username());
        assertTrue(BCrypt.checkpw(userData.password(), retrievedUserData.password()));
        assertEquals(userData.email(), retrievedUserData.email());
    }

    @Test
    void getUserNegative() throws DataAccessException {
        userDAO.createUser(userData);
        UserData invalidUserData = new UserData("differentUsername", "wrongPassword", "andWrongEmail@byu.edu");
        assertNull(userDAO.getUser(invalidUserData.username()));
    }

    @Test
    void listUserPositive() throws DataAccessException, SQLException {
        userDAO.createUser(userData);
        UserData userData1 = new UserData("anotherUsername", "anotherPassword", "anotherEmail");
        userDAO.createUser(userData1);
        UserData userData2 = new UserData("andAnotherUsername", "andAnotherPassword", "andAnotheEmail");
        userDAO.createUser(userData2);
        int sizeOfList = 0;
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        sizeOfList++;
                    }
                }
            }
        }
        assertEquals(3,sizeOfList);
    }

//    @Test
//    void listUserNegative() throws DataAccessException, SQLException {
//
//    }

    @Test
    void validateAuthTokenPositive() throws DataAccessException {
        userDAO.createUser(userData);
        assertTrue(userDAO.validateAuthToken(userData.username(),userData.password()));
    }

    @Test
    void validateAuthTokenNegative() throws DataAccessException {
        userDAO.createUser(userData);
        assertThrows(DataAccessException.class, ()->userDAO.validateAuthToken("wrongusername", "wrongpassword"));
    }

    @Test
    void clearTest() throws DataAccessException, SQLException {
        userDAO.createUser(userData);
        UserData userData1 = new UserData("anotherUsername", "anotherPassword", "anotherEmail");
        userDAO.createUser(userData1);
        UserData userData2 = new UserData("andAnotherUsername", "andAnotherPassword", "andAnotheEmail");
        userDAO.createUser(userData2);
        userDAO.clear();
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        }
    }
}
