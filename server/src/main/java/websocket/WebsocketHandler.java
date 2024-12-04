package websocket;

import chess.ChessGame;
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
            case LEAVE -> handleLeave(authData.username(), session);
            case RESIGN -> handleResign(session, command);
        };
    }

    private void handleConnect(Session session, UserGameCommand command) throws Exception {
        try {
            AuthData authData = authDAO.getAuthData(command.getAuthToken());
            boolean authenicated = authDAO.authenicateToken(authData);
            String username = authData.username();
            connections.add(username, session);
            GameData gameData = gameDAO.getGame(command.getGameID());
            String playerColor;
            if(gameData != null) {
                if(username.equals(gameData.blackUsername())) {
                    playerColor = "Black";
                } else if(username.equals(gameData.whiteUsername())) {
                    playerColor = "White";
                } else {
                    playerColor = null;
                }

                //output message depending on player or observer
                String message;
                if(playerColor != null) {
                    message = String.format("%s joined the game as %s", username, playerColor);
                } else {
                    message = String.format("%s joined the game as an observer", username);
                }
                ServerMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(username, notificationMessage, false);

                //Load game message
                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
                session.getRemote().sendString(new Gson().toJson(loadGameMessage));
            }
        } catch (UnauthorizedException e) {
            var msg = "Error: Not authorized";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        } catch (BadRequestException e) {
            var msg = "Error: Not a valid game to join or observe";
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
            sendMessage(session, new Gson().toJson(errorMessage));
        }

    }

    private void handleLeave(String username, Session session) throws Exception {
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        ServerMessage notificationMessage = new NotificationMessage(websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notificationMessage, false);
        session.close();
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
                ServerMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, msg);
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
            var msg = String.format("%s resigns the game to %s who wins!", authData.username(), opponentUsername);
            ServerMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
            connections.broadcast(authData.username(), notificationMessage, true);
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

    public void sendMessage(Session session, String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}
