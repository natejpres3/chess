package websocket;

import com.google.gson.Gson;
import dataaccess.MySQLAuthDAO;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import server.Server;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketHandler {

    MySQLAuthDAO authDAO = new MySQLAuthDAO();
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

    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect();
            case LEAVE -> handleLeave(authData.username());
        };
    }

    private void handleConnect() {

    }

    private void handleLeave(String username) throws Exception {
        connections.remove(username);
        var message = String.format("%s has left the game", username);
        ServerMessage notificationMessage = new NotificationMessage(websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, notificationMessage);
    }

}
