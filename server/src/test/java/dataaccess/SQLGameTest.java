package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTest {
    MySQLGameDAO gameDAO;
    GameData gameData;

    @BeforeEach
    void getStarted() throws DataAccessException, SQLException {
        gameDAO = new MySQLGameDAO();
        gameData = new GameData(100, "whiteUser", "blackUser", "theGame", new ChessGame());
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE games";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        }
    }

    @Test
    void createGamePositive() throws DataAccessException, SQLException {
        gameDAO.createGame(gameData);
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    assertTrue(rs.next());
                }
            }
        }
    }

    @Test
    void createGameNegative() throws DataAccessException {
        gameDAO.createGame(gameData);
        assertThrows(DataAccessException.class, ()->gameDAO.createGame(gameData));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        gameDAO.createGame(gameData);
        GameData retrievedGameData = gameDAO.getGame(gameData.gameID());
        assertEquals(gameData, retrievedGameData);
    }

    @Test
    void getGameNegative() throws DataAccessException {
        gameDAO.createGame(gameData);
        GameData invalidGameData = new GameData(99, "sameWhite", "sameBlack", "sameGameName", new ChessGame());
        assertNull(gameDAO.getGame(invalidGameData.gameID()));
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        gameDAO.createGame(gameData);
        GameData updatedGameData = new GameData(gameData.gameID(),"newWhiteUser", "newBlackUser", "newGameName", gameData.game());
        gameDAO.updateGame(gameData.gameID(), updatedGameData);
        GameData retrievedGameData = gameDAO.getGame(gameData.gameID());
        assertEquals(retrievedGameData, updatedGameData);
    }

    @Test
    void updateGameNegative() {
        assertThrows(DataAccessException.class, ()->gameDAO.updateGame(gameData.gameID(),gameData));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        gameDAO.createGame(gameData);
        GameData gameData1 = new GameData(101, "anotherWhiteUser", "anotherBlackUser", "newGame", new ChessGame());
        gameDAO.createGame(gameData1);
        Collection<GameData> listOfGames = gameDAO.listGames();
        assertEquals(2, listOfGames.size());
    }

    @Test
    void listGamesNegative() throws DataAccessException {
        Collection<GameData> listOfGames = gameDAO.listGames();
        assertEquals(0, listOfGames.size());
    }

    @Test
    void clearTest() throws DataAccessException {
        gameDAO.createGame(gameData);
        GameData gameData1 = new GameData(101, "anotherWhiteUser", "anotherBlackUser", "newGame", new ChessGame());
        gameDAO.createGame(gameData1);
        gameDAO.clear();
        Collection<GameData> listOfGames = gameDAO.listGames();
        assertEquals(0, listOfGames.size());
    }
}
