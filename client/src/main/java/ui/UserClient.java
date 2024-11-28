package ui;

import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.*;
import java.util.stream.Collectors;

public class UserClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;
    private boolean isLoggedIn = false;
    private ArrayList<GameData> gameList = new ArrayList<>();
    private HashMap<Integer, Integer> gameIndex = new HashMap<>();

    public UserClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(!isLoggedIn) {
                return switch(cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> loggedOutHelp();
                };
            } else {
                return switch(cmd) {
                    case "logout" -> logout();
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> playGame(params);
                    case "observe" -> observeGame(params);
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
        try {
            AuthData authData = server.register(new UserData(params[0], params[1], params[2]));
            if(authData != null) {
                isLoggedIn = true;
                authToken = authData.authToken();
                result = String.format("You are registered and logged in as %s %n", params[0]);
            }
        } catch(Exception e) {
            result = """
                    The username you chose is already in use, choose another
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    """;
        }
        return result;
    }

    public String login(String... params) throws Exception {
        if(params.length != 2) {
            return """
                    Please provide a username and password
                    login <USERNAME> <PASSWORD> - to play chess
                    """;
        }
        try {
            AuthData authData = server.login(new UserData(params[0],params[1], null));
            if(authData != null) {
                isLoggedIn = true;
                authToken = authData.authToken();
                return String.format("You are logged in as %s %n", params[0]);
            }
        } catch (Exception e) {
            return String.format("Username or password are incorrect, try again %n");
        }
        return "";
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

    public String logout() throws Exception {
        if(isLoggedIn == true) {
            server.logout(authToken);
            isLoggedIn = false;
            return String.format("You are signed out %n");
        } else {
            return String.format("You must sign in %n");
        }
    }

    public String createGame(String... params) throws Exception {
        if(params.length != 1) {
            return """
                    Provide a name for the game
                    create <NAME> - a game
                    """;
        } else {
            try {
                server.createGame(authToken, new GameData(0, null, null, params[0], null));
                return String.format("Created a game with the name: %s %n", params[0]);
            } catch (Exception e) {
                return String.format("Error creating game. Game was not created. %n");
            }

        }
    }

    public String listGames() throws Exception {
        if(isLoggedIn) {
            try {
                gameList = server.listGames(authToken);
                String result = printableLists();
                return result;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            return String.format("You must be signed in. %n");
        }
        return "";
    }

    private String printableLists() throws Exception {
        String answer = "";
        for(int i=0; i<gameList.size(); i++) {
            gameIndex.put(i+1, gameList.get(i).gameID());
            GameData game = gameList.get(i);
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "None";
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "None";
            String temp = String.format("-- %d. GameName: %s, BlackUser: %s, WhiteUser: %s %n", i+1,game.gameName(), blackUser,whiteUser);
            answer += temp;
        }
        return answer;
    }

    public String playGame(String... params) throws Exception {
        if(gameList.isEmpty()) {
            return String.format("First create the game %n");
        }
        if(params.length != 2 || !params[0].matches("\\d+") || !params[1].toUpperCase().matches("WHITE|BLACK")) {
            return String.format("Provide a game ID and the color you would like to play %n");
        }
        try {
            server.joinGame(gameIndex.get(Integer.parseInt(params[0])), params[1], authToken);
            RenderBoard.main();
            return String.format("You've joined the game. Play well %n");
        } catch (Exception e) {
            return String.format("You can't join the game as that color. List games to see games and open colors. %n");
        }
    }

    public String observeGame(String... params) throws Exception {
        if(params.length != 1) {
            return """
                    Provide a game number
                    observe <ID> - a game
                    """;
        }
        try {
            if(gameIndex.containsKey(Integer.parseInt(params[0]))) {
                RenderBoard.main();
                return String.format("You are observing the game %n");
            } else {
                return String.format("Not a valid game id to observe %n");
            }
        } catch (Exception e) {
            return String.format("Not a valid game to observe %n");
        }
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void clear() throws Exception {
        server.clearAll();
    }

}
