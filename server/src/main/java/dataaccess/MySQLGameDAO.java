package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements IGameDAO{

    public MySQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setInt(1,gameData.gameID());
                ps.setString(2,gameData.whiteUsername());
                ps.setString(3, gameData.blackUsername());
                ps.setString(4, gameData.gameName());
                var json = new Gson().toJson(gameData.game());
                ps.setString(5, json);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try(var rs = ps.executeQuery()) {
                    if(rs.next()) {
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        return new GameData(gameID,whiteUsername,blackUsername,gameName,json);
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
            try(var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.whiteUsername());
                ps.setString(2, gameData.blackUsername());
                ps.setString(3, gameData.gameName());
                var json = new Gson().toJson(gameData.game());
                ps.setString(4, json);
                ps.setInt(5, gameID);
                ps.executeUpdate();
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> listOfGames = new ArrayList<>();
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try(var ps = conn.prepareStatement(statement)) {
                try(var rs = ps.executeQuery()) {
                    while(rs.next()) {
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        listOfGames.add(new GameData(gameID,whiteUsername,blackUsername,gameName,json));
                    }
                }
            }
        } catch(SQLException e) {
            throw new DataAccessException("");
        }
        return listOfGames;
    }

    public int generateGameID() throws DataAccessException {
        int gameID = 1;
        while(getGame(gameID) != null) {
            gameID++;
        }
        return gameID;
    }

    @Override
    public void clear() throws DataAccessException{
        try(var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE TABLE games";
            try(var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to delete all game data");
        }
    }

    private final String createStatement =
            """
            CREATE TABLE IF NOT EXISTS games (
            gameID int NOT NULL,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255),
            game TEXT,
            PRIMARY KEY (gameID))
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
