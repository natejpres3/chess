package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
//    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
//    public final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Connection>> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> allConnections = new ConcurrentHashMap<>();

    public void add(String username, Session session, Integer gameID) {
        var connection = new Connection(username, session);
        allConnections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>());
        allConnections.get(gameID).put(username, connection);
//        connections.put(username, connection);
    }

    public void remove(String username, Integer gameID) {
        allConnections.get(gameID).remove(username);
//        connections.remove(username);
    }

    public void broadcast(String excludeUsername, ServerMessage serverMessage, boolean toYourself, Integer gameID) throws Exception {
        ConcurrentHashMap<String, Connection> connections = allConnections.get(gameID);
        var removeList = new ArrayList<Connection>();
        for(var c : connections.values()) {
            if(c.session.isOpen()) {
                if(!toYourself) {
                    if(!c.username.equals(excludeUsername)) {
                        c.send(new Gson().toJson(serverMessage));
                    }
                } else {
                    c.send(new Gson().toJson(serverMessage));
                }
            } else {
                removeList.add(c);
            }
        }
        for(var c : removeList) {
            connections.remove(c.username);
        }
    }

//    public void broadcast(String excludeUsername, ServerMessage serverMessage, boolean toYourself) throws Exception {
//        var removeList = new ArrayList<Connection>();
//        for(var c : connections.values()) {
//            if(c.session.isOpen()) {
//                if(!toYourself) {
//                    if(!c.username.equals(excludeUsername)) {
//                        c.send(new Gson().toJson(serverMessage));
//                    }
//                } else {
//                    c.send(new Gson().toJson(serverMessage));
//                }
//            } else {
//                removeList.add(c);
//            }
//        }
//        for(var c : removeList) {
//            connections.remove(c.username);
//        }
//    }
}
