package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    static GameService service;
    static MemoryGameDAO gameDAO;
    static MemoryAuthDAO authDAO;
    static AuthData authData;
    static MemoryUserDAO userDAO;

    GameData defaultGameData;

    @BeforeAll
    static void setup() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
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
        GameData defaultGameData = new GameData(1234,"WHITE","BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        int gameID = service.createGame(authToken,defaultGameData);
        assertNotNull(gameDAO.getGame(gameID));
    }

    @Test
    void createGameFailure() throws DataAccessException {
        GameData defaultGameData = new GameData(1234,"WHITE","BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        assertThrows(UnauthorizedException.class, () -> service.createGame("randomAuth",defaultGameData));
    }

    @Test
    void joinGameSuccess() throws DataAccessException {
        GameData defaultGameData = new GameData(1234,null,"BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        int gameID = service.createGame(authToken,defaultGameData);
        service.joinGame(authToken,"WHITE", gameID);
        String newWhiteUsername = gameDAO.getGame(gameID).whiteUsername();
        assertEquals(newWhiteUsername,authData.username());
    }

    @Test
    void joinGameFailure() throws DataAccessException {
        GameData defaultGameData = new GameData(1234,"UsernameAlreadyHere","BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"UsernameAlreadyHere");
        authDAO.createAuth(authData);
        int gameID = service.createGame(authToken,defaultGameData);
        assertThrows(UnauthorizedException.class,() -> service.joinGame("RandomAuthToken","WHITE", gameID));
    }

    @Test
    void listGamesSuccess() throws DataAccessException{
        GameData gameOne = new GameData(1234,null,"BLACK","GameName", new ChessGame());
        GameData gameTwo = new GameData(1543,null,"BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        int gameIDOne = service.createGame(authToken,gameOne);
        int gameIDTwo = service.createGame(authToken,gameTwo);

        Collection<GameData> listOfGames = service.listGames(authToken);

        assertEquals(2,listOfGames.size());
    }

    @Test
    void listGamesFailure() throws DataAccessException {
        GameData gameOne = new GameData(1234,null,"BLACK","GameName", new ChessGame());
        GameData gameTwo = new GameData(1543,null,"BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        int gameIDOne = service.createGame(authToken,gameOne);
        int gameIDTwo = service.createGame(authToken,gameTwo);

        assertThrows(UnauthorizedException.class,() -> service.listGames("My own authToken"));
    }

    @Test
    void clearAllGameData() throws DataAccessException {
        GameData gameOne = new GameData(1234,null,"BLACK","GameName", new ChessGame());
        GameData gameTwo = new GameData(1543,null,"BLACK","GameName", new ChessGame());
        String authToken = authDAO.generateAuthToken();
        AuthData authData = new AuthData(authToken,"username");
        authDAO.createAuth(authData);
        int gameIDOne = service.createGame(authToken,gameOne);
        int gameIDTwo = service.createGame(authToken,gameTwo);

        service.clear();

        Collection<GameData> listOfGames = service.listGames(authToken);

        assertEquals(0, listOfGames.size());
    }

    }
