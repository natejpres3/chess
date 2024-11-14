package ui;

public class PreLoginRepl {
    private final UserClient client;

    public PreLoginRepl(String serverUrl) {
        client = new UserClient(serverUrl);
    }

    public void run() {
        System.out.println("Welcome! Sign in to start playing some chess.");
        client.loggedOutHelp();
        
    }
}
