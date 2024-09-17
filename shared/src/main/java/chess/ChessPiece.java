package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

//    public ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
//        ArrayList<ChessMove> pMoves = new ArrayList<>();
//        //handle pawns moving one forward
//        if()
//    }
//
//    public ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
//
//    }
//
//    public ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
//
//    }

    public ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> bMoves = new ArrayList<>();

        int pieceDirections[][] = {
                {1,1},
                {-1,1},
                {-1,-1},
                {1,-1}
        };

        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition startPosition = myPosition;
            while(board.isWithinBoard(startPosition)) {
                ChessPosition newPosition = startPosition.movePos(xShift,yShift);
                if(board.getPiece(newPosition) == null) {
                    bMoves.add(new ChessMove(myPosition,newPosition,null));
                } else if(board.getPiece(newPosition).pieceColor != this.pieceColor) {
                    bMoves.add(new ChessMove(myPosition,newPosition,null));
                    break;
                } else {
                    break;
                }
                startPosition = newPosition;
            }
        }
        return bMoves;
    }

//    public ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
//
//    }
//
//    public ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
//
//    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        PieceType type = board.getPiece(myPosition).getPieceType();

        switch (type) {
            case PAWN:
//                moves = pawnMoves(board, myPosition);
                break;
            case ROOK:
//                moves = rookMoves(board, myPosition);
                break;
            case KNIGHT:
//                moves = knightMoves(board, myPosition);
                break;
            case BISHOP:
                moves = bishopMoves(board, myPosition);
                break;
            case QUEEN:
//                moves = queenMoves(board, myPosition);
                break;
            case KING:
//                moves = kingMoves(board, myPosition);
                break;
        }
        return moves;
    }
}
