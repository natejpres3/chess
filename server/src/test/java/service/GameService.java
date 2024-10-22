package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {
    MemoryGameDAO gameDAO;
    MemoryAuthDAO authDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            authDAO.getAuthData(authToken);
        } catch(DataAccessException e) {
            throw new DataAccessException("");
        }
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        try {
            authDAO.getAuthData(authToken);
        } catch(DataAccessException e) {
            throw new DataAccessException("");
        }
        int gameID = 0;
        while(gameDAO.getGame(gameID) != null) {
            gameID++;
        }
        ChessGame game = new ChessGame();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        gameDAO.createGame(new GameData(gameID,null,null,gameName,game));

        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        //global authData and gameData for use down below
        GameData gameData;
        AuthData authData;
        // authenticate the authToken
        try {
            authData = authDAO.getAuthData(authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("");
        }
        //get the game data for the gameID
        try {
            gameData = gameDAO.getGame(gameID);
        } catch(DataAccessException e) {
            throw new DataAccessException("");
        }

        //create the necessary game data given the player color
        if(playerColor == "WHITE") {
            gameData.whiteUsername().equals(authData.username());
        } else if(playerColor == "BLACK") {
            gameData.blackUsername().equals(authData.username());
        } else {
            throw new DataAccessException("This is not a team color");
        }
        //update the game with the username as the appropriate color
        gameDAO.updateGame(gameID, gameData);
    }

    //clear games
    public void clear() {
        gameDAO.clear();
    }
}
