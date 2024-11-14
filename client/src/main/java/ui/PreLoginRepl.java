package ui;

import server.ServerFacade;

import java.util.Scanner;

public class PreLoginRepl {
    private final UserClient client;
//    private final PostLoginRepl postLoginRepl;
    private ServerFacade serverFacade;

    public PreLoginRepl(String serverUrl) {
        client = new UserClient(serverUrl);
//        postLoginRepl = new PostLoginRepl(serverUrl);
    }

    public void run() {
        System.out.println("Welcome! Sign in to start playing some chess.");
        System.out.println(client.loggedOutHelp());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(result == null || !result.equals("quit")) {
            String loginStatus = client.getIsLoggedIn() ? "[LOGGED_IN] >>> " : "[LOGGED_OUT] >>> ";
            System.out.print(loginStatus);
            String line = scanner.nextLine();

            try{
                result = client.eval(line);
                if(result != null) {
                    System.out.print(result);
                }
            } catch(Exception e) {
                System.out.println("User already registered");
            }
        }
        if(client.getIsLoggedIn()) {
//            postLoginRepl.run();
        }
        System.out.println();
    }
}
