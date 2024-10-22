package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class GameServiceTest {
    static GameService service;
    static MemoryGameDAO gameDAO;
    static MemoryAuthDAO authDAO;
    static AuthData authData;

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new GameService(gameDAO,authDAO);
        authData = new AuthData("authToken", "ninefirenine");
        authDAO.createAuth(authData);
    }

    @BeforeEach
    void clear() {
        service.clear();
    }

    @Test
    void createGameSuccess() throws DataAccessException {

    }
}
