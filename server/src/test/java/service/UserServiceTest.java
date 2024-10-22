package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
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
    void registerUser() throws DataAccessException {
        UserData newUserData = new UserData("ninefirenine", "Ilikecars", "joe@gmail.com");
        service.register(newUserData);

        var users = service.listUsers();
        assertEquals(1,users.size());
        assertTrue(users.contains(newUserData));
    }

    
}
