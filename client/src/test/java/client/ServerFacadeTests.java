package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.UserClient;


public class ServerFacadeTests {

    private static Server server;
    static UserClient client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        client = new UserClient(url);
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
    void registerSuccess() {
        String params =
        client.register()
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
