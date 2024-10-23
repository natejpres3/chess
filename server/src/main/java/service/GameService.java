package service;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameService {
    MemoryGameDAO gameDAO;
    MemoryAuthDAO authDAO;

    public GameService(MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public int createGame(String authToken, GameData gameData) throws DataAccessException {
        if(gameData.gameName() == null || authToken == null) {
            throw new BadRequestException("");
        }
        AuthData authData;
        try{
            authData = authDAO.getAuthData(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("");
        }
        if(!authDAO.authenicateToken(authData)) {
            throw new UnauthorizedException("");
        }
        if(gameDAO.getGame(gameData.gameID()) != null) {
            throw new DataAccessException("");
        }
        int gameID = gameDAO.generateGameID();
        ChessGame game = new ChessGame();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        gameDAO.createGame(new GameData(gameID,null,null,gameData.gameName(),game));

        return gameID;
//        try {
//            authDAO.getAuthData(authToken);
//        } catch(DataAccessException e) {
//            throw new DataAccessException("");
//        }
//        int gameID = 0;
//        while(gameDAO.getGame(gameID) != null) {
//            gameID++;
//        }
//        ChessGame game = new ChessGame();
//        ChessBoard board = new ChessBoard();
//        board.resetBoard();
//        gameDAO.createGame(new GameData(gameID,null,null,gameName,game));
//
//        return gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        //global authData and gameData for use down below
        GameData gameData;
        AuthData authData;
        try{
            authData = authDAO.getAuthData(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("");
        }
        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException("");
        }
        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        if(playerColor.equals("WHITE")) {
            if(gameData.whiteUsername() != null && !whiteUser.equals(authData.username())) {
                throw new AlreadyTakenException("");
            } else {
                whiteUser = authData.username();
            }
        } else if(playerColor.equals("BLACK")) {
            if(gameData.blackUsername() != null && !blackUser.equals(authData.username())) {
                throw new AlreadyTakenException("");
            } else {
                blackUser = authData.username();
            }
        } else {
            throw new BadRequestException("");
        }
        try{
            gameDAO.updateGame(gameID, new GameData(gameID,whiteUser,blackUser,gameData.gameName(),gameData.game()));
        } catch (DataAccessException e) {
            throw new BadRequestException("");
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        try {
            authDAO.getAuthData(authToken);
        } catch(DataAccessException e) {
            throw new UnauthorizedException("");
        }
        return gameDAO.listGames();
    }

    //clear games
    public void clear() {
        gameDAO.clear();
    }
}
