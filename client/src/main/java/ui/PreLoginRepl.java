package ui;

import server.ServerFacade;

import java.util.Scanner;

public class PreLoginRepl {
    private final UserClient client;
    private PostLoginRepl postLoginRepl;
    private ServerFacade serverFacade;

    public PreLoginRepl(String serverUrl) {
        client = new UserClient(serverUrl);
//        postLoginRepl = new PostLoginRepl();
    }

    public void run() {
        System.out.println("Welcome! Sign in to start playing some chess.");
        client.loggedOutHelp();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            String line = scanner.nextLine();

            try{
                result = client.eval(line);
                System.out.print(result);
            } catch(Exception e) {

            }
        }
        if(client.getIsLoggedIn()) {

        }
        System.out.println();
    }
}
