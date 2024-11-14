package ui;

public class PreLoginRepl {
    private final UserClient client;

    public PreLoginRepl(String serverUrl) {
        client = new UserClient(serverUrl);
    }
}
