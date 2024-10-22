package dataaccess;

import model.GameData;

import java.util.List;

public interface IGameDAO {
    int insertGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData gameData) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    List<GameData> getListGames() throws DataAccessException;
    void clear() throws DataAccessException;
}
