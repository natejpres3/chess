package ui;

import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class UserClient {
    private final ServerFacade server;
    private final String serverUrl;
    private boolean isLoggedIn = false;

    public UserClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public void eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch(cmd) {
                case "login" -> login(params);
                default -> help();
            };
        } catch(Exception e) {

        }
    }

    public void register(String... params) throws Exception {
        if(params.length != 3) {
            System.out.println("Please provide your username, password, and email");
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        } else if(server.register(new UserData(params[0], params[1], params[2])) != null) {
            System.out.println("You are registerd and logged in as " + params[0]);
            isLoggedIn = true;
        } else {
            System.out.println("The username you chose is already in use, choose another");
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        }
    }

    public void login(String... params) throws Exception {
        if(params.length != 2) {
            System.out.println("Please provide a username and password");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        } else if(server.login(new UserData(params[0],params[1], null)) != null) {
            System.out.println("You are logged in as " + params[0]);
            isLoggedIn = true;
        } else {
            System.out.println("Username or password are incorrect, try again");
        }
    }

    public void help() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }

}
