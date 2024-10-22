package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    static final UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    @BeforeEach
    void clear() {
        service.clear();
    }

    @Test
    void registerUserSuccess() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);

        var users = service.listUsers();
        assertEquals(1,users.size());
        assertTrue(users.contains(newUserData));
    }

    @Test
    void registerUserFailure() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);
        assertThrows(DataAccessException.class, () -> {service.register(newUserData);});
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
}
