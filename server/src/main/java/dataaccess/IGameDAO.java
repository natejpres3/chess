package dataaccess;

import model.GameData;

import java.util.List;

public interface IGameDAO {
    int createGame(String gameName) throws DataAccessException;
    void joinGame(String playerColor, int gameID) throws DataAccessException;
    void setUserColor(String playerColor) throws DataAccessException;
    GameData getGameByID(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    List<GameData> getListGames() throws DataAccessException;
}
