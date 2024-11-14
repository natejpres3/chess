package ui;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class UserClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;
    private boolean isLoggedIn = false;
    private ArrayList<GameData> gameList;

    public UserClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(!isLoggedIn) {
                return switch(cmd) {
//                    case "login" -> login(params);
                    case "register" -> register(params);
                    case "quit" -> "quit";
                    default -> loggedOutHelp();
                };
            } else {
                return switch(cmd) {
//                    case "create" -> createGame(params);
                    default -> loggedInHelp();
                };
            }

        } catch(Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        String result = "";
        if(params.length != 3) {
            return """
                    Please provide your username, password, and email
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    """;
        }
        AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
        if(authData != null) {
            isLoggedIn = true;
            result = String.format("You are registered and logged in as %s", params[0]);
        } else {
            result = """
                    The username you chose is already in use, choose another
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    """;
        }
        return result;
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

    public String loggedOutHelp() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                - login <USERNAME> <PASSWORD> - to play chess
                - quit - playing chess
                - help - with possible commands
                """;
    }

    //logged in methods
    public String loggedInHelp() {
        return """
                - create <NAME> - a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit - playing chess
                - help - with possible commands
                """;
    }

    public void logout() throws Exception {
        if(isLoggedIn == true) {
            server.logout(authToken);
            System.out.println("You are signed out");
        } else {
            throw new Exception("You must sign in");
        }
    }

    public void createGame(String... params) throws Exception {
        if(params.length != 1) {
            System.out.println("Provide a name for the game");
            System.out.println("create <NAME> - a game");
        } else {
            server.createGame(authToken, new GameData(0, null, null, params[0], null));
            System.out.println("Created a game with the name: " + params[0]);
        }
    }

    public void listGames() throws Exception {
        if(isLoggedIn) {
            gameList = getListOfGames();
            printListOfGames(gameList);
        } else {
            throw new Exception("You must be signed in");
        }
    }

    public void playGame(String... params) throws Exception {
        gameList = getListOfGames();
        if(gameList.isEmpty()) {
            System.out.println("First create the game");
        }
        if(params.length != 2 || !params[0].matches("\\d+") || !params[1].toLowerCase().matches("WHITE|BLACK")) {
            System.out.println("Provide a game ID and the color you would like to play");
        }
    }

    private ArrayList<GameData> getListOfGames() throws Exception {
        Collection<GameData> listOfGames = server.listGames();
        return new ArrayList<>(listOfGames);
    }

    private void printListOfGames(ArrayList<GameData> gameList) {
        for(int i=1; i< gameList.size()+1; i++) {
            GameData game = gameList.get(i);
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "None";
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "None";
            System.out.printf("%d. Game name: %s, White user: %s, Black user: %s %n", i, game.gameName(), whiteUser, blackUser);
        }
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void clear() throws Exception {
        server.clearAll();
    }

}
