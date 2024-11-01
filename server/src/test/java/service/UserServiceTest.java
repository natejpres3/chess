package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static UserService service = new UserService(new MySQLUserDAO(), new MySQLAuthDAO());

    @BeforeEach
    void clear() throws DataAccessException {
        service.clear();
    }

    @Test
    void registerUserSuccess() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);

        var users = service.listUsers();
        assertEquals(1,users.size());
//        assertTrue(users.contains(newUserData));
    }

    @Test
    void registerUserFailure() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);
        assertThrows(AlreadyTakenException.class, () -> {service.register(newUserData);});
    }

    @Test
    void loginUserSuccess() throws DataAccessException {
        //register a user and login in
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);
        AuthData authData = service.loginUser(newUserData);
        assertTrue(service.validateAuthToken(newUserData));
        assertEquals(authData.username(),newUserData.username());
    }

    @Test
    void loginUserFailure() throws DataAccessException {
        //just attempt to login and throw a exc
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);
        UserData wrongUserPassword = new UserData("ninefirenine","wrong", null);
        assertThrows(DataAccessException.class,() -> {service.loginUser(wrongUserPassword);});
    }

    @Test
    void logoutUserSuccess() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        AuthData authData = service.register(newUserData);
        authData = service.loginUser(newUserData);
        service.logoutUser(authData.authToken());
        assertFalse(service.authenicateToken(authData));
    }

    @Test
    void logoutUserFailure() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        AuthData authData = service.register(newUserData);
        String badAuthToken = UUID.randomUUID().toString();
        assertThrows(UnauthorizedException.class,()->{service.logoutUser(badAuthToken);});
    }

    @Test
    void clearAllUserData() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);
        service.clear();
        var users = service.listUsers();
        assertEquals(0,users.size());
    }
}
