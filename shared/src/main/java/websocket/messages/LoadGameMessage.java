package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {

    ChessGame game;
    ChessGame.TeamColor playerColor;

    public LoadGameMessage(ServerMessageType type, ChessGame game, ChessGame.TeamColor playerColor) {
        super(type);
        this.game = game;
        this.playerColor = playerColor;
    }

    public ChessGame getGame() {
        return game;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
