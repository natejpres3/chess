package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements IGameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = games.get(gameID);
        return game;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        GameData game = games.get(gameID);
        if(game == null) {
            throw new DataAccessException("Cannot update because cannot find game");
        }
        games.put(gameID,gameData);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public int generateGameID() {
        int gameID = 1;
        while(games.containsKey(gameID)) {
            gameID++;
        }
        return gameID;
    }

    @Override
    public void clear() {
        games.clear();
    }
}
