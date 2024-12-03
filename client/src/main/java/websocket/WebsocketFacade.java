package websocket;

import com.google.gson.Gson;
import com.sun.nio.sctp.Notification;
//import com.sun.nio.sctp.NotificationHandler;

import javax.websocket.*;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws Exception{
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");
        this.notificationHandler = notificationHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                Notification notification = new Gson().fromJson(s, Notification.class);
                notificationHandler.notify();
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }


}
