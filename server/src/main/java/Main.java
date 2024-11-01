import chess.*;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import server.Server;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        new Server().run(8080);
    }

}