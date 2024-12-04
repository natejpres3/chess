package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public String username;
    public Session session;

    public Connection(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void send(String msg) throws Exception {
        session.getRemote().sendString(msg);
    }
}
