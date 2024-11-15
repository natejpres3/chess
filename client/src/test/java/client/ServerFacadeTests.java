package client;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import ui.PreLoginRepl;
import ui.UserClient;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static UserClient client;
    private static ServerFacade facade;
    private static String url;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        url = "http://localhost:" + port;
        client = new UserClient(url);
        facade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void getStarted() throws Exception {
        client.clear();
    }

    @Test
    void registerSuccess() throws Exception {
        AuthData authData = facade.register(new UserData("njp", "pass", "njp@g"));
        assertEquals(authData.username(), "njp");
        assertNotNull(authData.authToken());
    }

    @Test
    void registerFailure() throws Exception {
        facade.register(new UserData("njp", "pass", "njp@g"));
        assertThrows(Exception.class, ()->facade.register(new UserData("njp", "pass", "njp@g")));
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register(new UserData("njp", "pass", "njp@g"));
        AuthData authData = facade.login(new UserData("njp", "pass", "njp@g"));
        assertEquals(authData.username(),"njp");
        assertNotNull(authData.authToken());
    }

    @Test
    void loginFailure() throws Exception {
        facade.register(new UserData("njp", "pass", "njp@g"));
        assertThrows(Exception.class, ()->facade.login(new UserData("njp", "wrongpass", "njp@g")));
    }

    @Test
    void logoutSuccess() throws Exception {
        AuthData authData = facade.register(new UserData("njp", "pass", "njp@g"));
        assertDoesNotThrow(()->facade.logout(authData.authToken()));
    }

    @Test
    void logoutFailure() throws Exception {
        assertThrows(Exception.class, ()->facade.logout("randomAuth"));
    }

    @Test
    void createGameSuccess() throws Exception {
        AuthData authData = facade.register(new UserData("user", "pass", "gmail"));
        GameData gameData = new GameData(0,null,null,"newGame", null);
        assertDoesNotThrow(() -> facade.createGame(authData.authToken(),gameData));
    }

    @Test
    void createGameFailure() throws Exception {
        assertThrows(Exception.class, ()->facade.createGame("randomAuth", new GameData(0,null,null,"new", null)));
    }

    @Test
    void joinGameSuccess() throws Exception {
        AuthData authData = facade.register(new UserData("user", "pass", "gmail"));
        GameData gameData = new GameData(0,null,null,"newGame", null);
        //assertDoesNotThrow(facade.joinGame());
    }

    @Test
    void joinGameFailure() throws Exception {

    }

    @Test
    void clearTest() throws Exception {

    }
}
