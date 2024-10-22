package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public interface IGameDAO {
    void createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, GameData gameData) throws DataAccessException;
//    void deleteGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void clear();
}
