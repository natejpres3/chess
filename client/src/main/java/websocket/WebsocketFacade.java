package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.sun.nio.sctp.Notification;
import ui.EscapeSequences;
import ui.PrintBoard;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
//import com.sun.nio.sctp.NotificationHandler;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    Session session;
    LoadGameMessage loadGameMessage;

    public WebsocketFacade(String url) throws Exception {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String s) {
                //handle incoming message
                handleIncomingMessage(s);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void handleIncomingMessage(String message) {
        if(message.contains("\"serverMessageType\":\"LOAD_GAME\"")) {
            LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
            boolean isWhite = true;
            if(loadGameMessage.getPlayerColor() != null) {
                isWhite = loadGameMessage.getPlayerColor() == ChessGame.TeamColor.WHITE;
            }
            PrintBoard.printBoard(loadGameMessage.getGame(), isWhite);
//            boolean isWhite;
//            if(loadGameMessage.getPlayerColor() == null) {
//                this.loadGameMessage = loadGameMessage;
//            } else {
//                isWhite = loadGameMessage.getPlayerColor() == ChessGame.TeamColor.WHITE;
//                PrintBoard.printBoard(loadGameMessage.getGame(), isWhite);
//            }
        } else if(message.contains("\"serverMessageType\":\"NOTIFICATION\"")) {
            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
            System.out.print(EscapeSequences.ERASE_LINE + '\n');
            System.out.println(notificationMessage.getMessage());
        } else if(message.contains("\"serverMessageType\":\"ERROR\"")) {
            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);

        }
    }





    public void connectToGame(String authToken, Integer gameID) {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {

        }

    }

    public void leaveGame(String authToken, Integer gameID) {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
            this.session.close();
        } catch (IOException e) {

        }
    }

    public void resignGame(String authToken, Integer gameID) {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {

        }
    }

    public void redrawGame() {
        PrintBoard.printBoard(loadGameMessage.getGame(), true);
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) {
        try {
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException e) {

        }
    }


}
