package websocket;

import com.google.gson.Gson;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
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
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
            case CONNECT -> handleConnect(authData.username(), session, command.getGameID());
            case LEAVE -> handleLeave(authData.username(), session);
        };
    }

    private void handleConnect(String username, Session session, Integer gameID) throws Exception {
        connections.add(username, session);
        GameData gameData = gameDAO.getGame(gameID);
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
            connections.broadcast(username, notificationMessage);

            //Load game message
            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));
        }
    }

    private void handleLeave(String username, Session session) throws Exception {
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        ServerMessage notificationMessage = new NotificationMessage(websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notificationMessage);
        session.close();
    }

}
