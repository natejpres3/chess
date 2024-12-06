package ui;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;
import websocket.WebsocketFacade;

import java.util.*;
import java.util.stream.Collectors;

public class UserClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String authToken;
    private Integer gameID;
    private boolean isLoggedIn = false;
    private boolean isInGame = false;
    private WebsocketFacade ws;
    private ArrayList<GameData> gameList = new ArrayList<>();
    private HashMap<Integer, Integer> gameIndex = new HashMap<>();
    static boolean isClientWhite;

    public UserClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean getIsInGame() {
        return isInGame;
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
                if(!isInGame) {
                    return switch(cmd) {
                        case "logout" -> logout();
                        case "create" -> createGame(params);
                        case "list" -> listGames();
                        case "join" -> playGame(params);
                        case "observe" -> observeGame(params);
                        default -> loggedInHelp();
                    };
                } else {
                    return switch(cmd) {
                        case "redraw" -> redrawBoard();
                        case "leave" -> leaveGame();
                        case "move" -> makeMove(params);
                        case "resign" -> resignGame();
                        case "highlight" -> highlightMoves(params);
                        default -> inGameHelp();
                    };
                }

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
            //game id
            gameID = gameIndex.get(Integer.parseInt(params[0]));
            server.joinGame(gameID, params[1], authToken);
            isInGame = true;
            isClientWhite = params[1].equalsIgnoreCase("WHITE");
            //websocket facade
            ws = new WebsocketFacade(serverUrl);
            ws.connectToGame(authToken, Integer.parseInt(params[0]));
            return String.format("You've joined the game. Play well %n");
        } catch (Exception e) {
            return String.format("You can't join the game as that color. List games to see games and open colors. %n");
        }
    }

    public static boolean isClientWhite() {
        return isClientWhite;
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
                gameID = gameIndex.get(Integer.parseInt(params[0]));
                isClientWhite = true;
                ws = new WebsocketFacade(serverUrl);
                ws.connectToGame(authToken, gameID);
                isInGame = true;
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

    public String redrawBoard() {
        ws.redrawGame();
        return "Board redrawn";
    }

    public String leaveGame() {
        isInGame = false;
        ws.leaveGame(authToken, gameID);
        return "You have left the game \n";
    }

    public String makeMove(String... params) {
        if(params.length < 2) {
            return """
                    Provide a starting position, ending position, and a promotion piece if applicable
                    move <from> <to> <promotionPiece> - make a move on the board
                    """;
        }
        try {
            if(params.length >= 2 && params[0].matches("[a-h][1-8]") && params[1].matches("[a-h][1-8]")) {
                ChessPosition starting = getPositionFromInput(params[0]);
                ChessPosition ending = getPositionFromInput(params[1]);
                ChessPiece.PieceType promotion;
                if(params.length == 3) {
                    promotion = getPromotionType(params[2]);
                } else {
                    promotion = null;
                }
                ws.makeMove(authToken, gameID, new ChessMove(starting, ending, promotion));
            }
        } catch (Exception e) {

        }
        return "";
    }

    private ChessPosition getPositionFromInput(String param) {
        int firstChar = param.charAt(0) - ('a'-1);
        int secondChar = param.charAt(1) - '0';
        return new ChessPosition(secondChar, firstChar);
    }

    private ChessPiece.PieceType getPromotionType(String param) {
        String promotionPiece = param.toUpperCase();
        switch (promotionPiece) {
            case "PAWN": return ChessPiece.PieceType.PAWN;
            case "ROOK": return ChessPiece.PieceType.ROOK;
            case "KNIGHT": return ChessPiece.PieceType.KNIGHT;
            case "BISHOP": return ChessPiece.PieceType.BISHOP;
            case "QUEEN": return ChessPiece.PieceType.QUEEN;
            case "KING": return ChessPiece.PieceType.KING;
            default: return null;
        }
    }

    public String resignGame() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Are you sure you want to resign? ");
            String answer = scanner.nextLine();
            if(answer.equalsIgnoreCase("yes")) {
                ws.resignGame(authToken, gameID);
            }
        } catch (Exception e) {

        }

        return "";
    }

    public String highlightMoves(String... params) {
        if(params.length != 1) {
            return """
                    Provide a chess position to highlight from
                    highlight <position> - all legal moves from that position
                    """;
        }
        ChessPosition positionToHighlight = getPositionFromInput(params[0]);
        ws.highlightMoves(positionToHighlight);
        return "";
    }

    public String inGameHelp() {
        return """
                - redraw - the game board
                - leave - the chess game
                - move <from> <to> <promotionPiece> - make a move on the board (only enter promotion piece when appropriate)
                - resign - forfeit this game
                - highlight <position> - all legal moves from that position
                - help - with possible commands
                """;
    }

    public void clear() throws Exception {
        server.clearAll();
    }

}
