package websocket;

import com.sun.nio.sctp.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}
