package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.Server;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

    MySQLAuthDAO authDAO = new MySQLAuthDAO();
    MySQLGameDAO gameDAO = new MySQLGameDAO();
    private final ConnectionManager connections = new ConnectionManager();

//    @OnWebSocketConnect
//    public void whenConnect(Session session) throws Exception {
//        Server.connections.put(session, 0);
//    }
//
//    @OnWebSocketClose
//    public void whenClose(Session session) {
//        Server.connections.remove(session);
//    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(session, command);
            case LEAVE -> handleLeave(session, command);
            case RESIGN -> handleResign(session, command);
            case MAKE_MOVE -> handleMakeMove(session, new Gson().fromJson(message, MakeMoveCommand.class));
        };
    }

    private void handleConnect(Session session, UserGameCommand command) throws Exception {
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            if(authData == null) {
                throw new UnauthorizedException("Unauthorized");
            }
            boolean authenicated = authDAO.authenicateToken(authData);
            String username = authData.username();
            connections.add(username, session, command.getGameID());
            GameData gameData = gameDAO.getGame(command.getGameID());
            if(gameData == null) {
                throw new BadRequestException("No game here");
            }
            ChessGame.TeamColor playerColor = getUsernameColor(username, gameData);

            //output message depending on player or observer
            String message;
            if(playerColor != null) {
                message = String.format("%s joined the game as %s", username, playerColor);
            } else {
                message = String.format("%s joined the game as an observer", username);
            }
            ServerMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username, notificationMessage, false, gameData.gameID());

            //Load game message
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
        } catch (UnauthorizedException e) {
            var msg = "Error: Not authorized";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
//            sendError(session, errorMessage);
        } catch (BadRequestException e) {
            var msg = "Error: Not a valid game to join or observe";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        }
    }

//    private void handleLeave(String username, Session session) throws Exception {
//        connections.remove(username);
//        var message = String.format("%s has left the game", username);
//        ServerMessage notificationMessage = new NotificationMessage(websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION, message);
//        connections.broadcast(username, notificationMessage, false);
//        session.close();
//    }

    private void handleLeave(Session session, UserGameCommand command) throws Exception {
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            if(authData == null) {
                throw new UnauthorizedException("Unauthorized");
            }
            boolean authenicated = authDAO.authenicateToken(authData);
            String username = authData.username();
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame.TeamColor playerColor = getUsernameColor(username, gameData);
            GameData newGameData = leaveUpdateHelper(gameData, playerColor);
            gameDAO.updateGame(command.getGameID(), newGameData);

            connections.remove(username, gameData.gameID());
            var message = String.format("%s has left the game", username);
            ServerMessage notificationMessage = new NotificationMessage(websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(username, notificationMessage, false, gameData.gameID());
            session.close();
        } catch(Exception e) {

        }
    }

    private void handleResign(Session session, UserGameCommand command) {
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            authDAO.authenicateToken(authData);
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame.TeamColor playerColor = getUsernameColor(authData.username(), gameData);
            String opponentUsername = playerColor == ChessGame.TeamColor.WHITE ? gameData.blackUsername() : gameData.whiteUsername();
            if(playerColor == null) {
                var msg = "Error: You are observing, you cannot resign";
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
            }

            if(gameData.game().getIsGameDone()) {
                var msg = "Error: The game is already over, you cannot resign";
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
            }

            gameData.game().setGameDone(true);
            gameDAO.updateGame(gameData.gameID(), gameData);
            var msg = String.format("%s resigns the game to %s who wins!", authData.username(), opponentUsername);
            ServerMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
            connections.broadcast(authData.username(), notificationMessage, true, gameData.gameID());
        } catch (Exception e) {

        }
    }

    private void handleMakeMove(Session session, MakeMoveCommand command) throws Exception {
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            if(authData == null) {
                throw new UnauthorizedException("Unauthorized");
            }
            authDAO.authenicateToken(authData);
            GameData gameData = gameDAO.getGame(command.getGameID());
            ChessGame.TeamColor playerColor = getUsernameColor(authData.username(), gameData);

            if(playerColor == null) {
                var msg = "Error: You are observing, you cannot make a move";
                ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
            }

            if(gameData.game().getIsGameDone()) {
                var msg = "Error: The game is already over, you cannot make a move anymore";
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
            }

            //make move if correct color
            if(playerColor.equals(gameData.game().getTeamTurn())) {
                gameData.game().makeMove(command.getMove());

                String msg;
                if(gameData.game().isInCheck(playerColor)) {
                    msg = String.format("%s just put his opponent in check", authData.username());
                } else if (gameData.game().isInStalemate(playerColor)) {
                    msg = String.format("%s just caused a stalemate", authData.username());
                } else if (gameData.game().isInCheckmate(playerColor)) {
                    msg = String.format("%s just put his opponent in checkmate", authData.username());
                } else {
                    msg = String.format("%s just made a move", authData.username());
                }
                NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
                connections.broadcast(authData.username(), notificationMessage, false, gameData.gameID());

                gameDAO.updateGame(gameData.gameID(), gameData);
                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
                connections.broadcast(authData.username(), loadGameMessage, true, gameData.gameID());
            } else {
                var msg = "Error: It is not your turn to make a move";
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
                sendMessage(session, new Gson().toJson(errorMessage));
                return;
            }
        } catch (UnauthorizedException e) {
            var msg = "Error: Not authorized";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        } catch (BadRequestException e) {
            var msg = "Error: Not a valid game to join or observe";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        } catch (InvalidMoveException e) {
            var msg = "Error: Not a valid move to make";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        } catch (Exception e) {

        }
    }

    public ChessGame.TeamColor getUsernameColor(String username, GameData gameData) {
        if(gameData.whiteUsername() != null && username.equals(gameData.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if(gameData.blackUsername() != null && username.equals(gameData.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

//    public void sendMessage(Session session, ServerMessage message) throws IOException {
//        session.getRemote().sendString(new Gson().toJson(message));
//    }

    public void sendError(Session session, ErrorMessage errorMessage) throws IOException {
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }

    public void sendMessage(Session session, String msg) throws Exception {
        session.getRemote().sendString(msg);
    }

    private GameData leaveUpdateHelper(GameData gameData, ChessGame.TeamColor playerColor) {
        if(playerColor == ChessGame.TeamColor.WHITE) {
            return new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
        } else {
            return new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
        }
    }
}
