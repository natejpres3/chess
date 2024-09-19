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

    public boolean isPromotionRow(ChessPosition myPosition) {
        if(this.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 7) {
            return true;
        } else if(this.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 2) {
            return true;
        }
        return false;
    }

    public boolean isFirstMove(ChessPosition myPosition) {
        if(this.getTeamColor() == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
            return true;
        } else if(this.getTeamColor() == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
            return true;
        }
        return false;
    }

    public ArrayList<ChessMove> doPromotions(ArrayList<ChessMove> pMoves, ChessPosition myPosition, ChessPosition newPosition) {
        pMoves.add(new ChessMove(myPosition,newPosition,PieceType.QUEEN));
        pMoves.add(new ChessMove(myPosition,newPosition,PieceType.ROOK));
        pMoves.add(new ChessMove(myPosition,newPosition,PieceType.BISHOP));
        pMoves.add(new ChessMove(myPosition,newPosition,PieceType.KNIGHT));
        return pMoves;
    }

    public ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> pMoves = new ArrayList<>();
        int direction = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        // handle one and two moves forward
        ChessPosition oneForward = myPosition.movePos(1*direction,0);
        ChessPosition twoForward = myPosition.movePos(2*direction,0);
        if(board.isWithinBoard(oneForward) && board.getPiece(oneForward) == null) {
            //one if that check for promotion
            if(isPromotionRow(myPosition)) {
                pMoves = doPromotions(pMoves,myPosition,oneForward);
            } else {
                pMoves.add(new ChessMove(myPosition,oneForward,null));
            }
            //if first move add two forward
            if(isFirstMove(myPosition) && board.getPiece(twoForward) == null) {
                pMoves.add(new ChessMove(myPosition,twoForward,null));
            }
        }
        // account for attacking left and right with or without promotions
        ChessPosition attackLeft = myPosition.movePos(1*direction,-1*direction);
        ChessPosition attackRight = myPosition.movePos(1*direction,1*direction);
        if(board.getPiece(attackLeft) != null && board.isEnemy(board,myPosition,attackLeft)) {
            if(isPromotionRow(myPosition)) {
                pMoves = doPromotions(pMoves,myPosition,attackLeft);
            } else {
                pMoves.add(new ChessMove(myPosition,attackLeft,null));
            }
        }
        if(board.getPiece(attackRight) != null && board.isEnemy(board,myPosition,attackRight)) {
            if(isPromotionRow(myPosition)) {
                pMoves = doPromotions(pMoves,myPosition,attackRight);
            } else {
                pMoves.add(new ChessMove(myPosition,attackRight,null));
            }
        }
        return pMoves;
    }

    public ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> rMoves = new ArrayList<>();
        int pieceDirections[][] = {
                {1,0}, {0,1}, {-1,0}, {0,-1}
        };

        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition startPosition = myPosition;
            while(board.isWithinBoard(startPosition)) {
                ChessPosition newPosition = startPosition.movePos(xShift,yShift);
                if(!board.isWithinBoard(newPosition)) {break;}
                if(board.getPiece(newPosition) == null) {
                    rMoves.add(new ChessMove(myPosition,newPosition,null));
                } else if(board.isEnemy(board,myPosition,newPosition)) {
                    rMoves.add(new ChessMove(myPosition,newPosition,null));
                    break;
                } else {
                    break;
                }
                startPosition = newPosition;
            }
        }
        return rMoves;
    }

    public ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> knMoves = new ArrayList<>();
        int pieceDirections[][] = {
                {2,-1},{2,1},{1,2},{-1,2},
                {-2,1},{-2,-1},{-1,-2},{1,-2}
        };
        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition newPosition = myPosition.movePos(xShift,yShift);
            if(board.isWithinBoard(newPosition)) {
                if(board.getPiece(newPosition) == null || board.isEnemy(board,myPosition,newPosition)) {
                    knMoves.add(new ChessMove(myPosition,newPosition,null));
                }
            }
        }
        return knMoves;
    }

    public ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> bMoves = new ArrayList<>();

        int pieceDirections[][] = {
                {1,1}, {-1,1}, {-1,-1}, {1,-1}
        };

        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition startPosition = myPosition;
            while(board.isWithinBoard(startPosition)) {
                ChessPosition newPosition = startPosition.movePos(xShift,yShift);
                if(!board.isWithinBoard(newPosition)) {break;}
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

    public ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> qMoves = new ArrayList<>();
        int pieceDirections[][] = {
                {1,0}, {1,1}, {0,1}, {-1,1},
                {-1,0}, {-1,-1}, {0,-1}, {1,-1},
        };
        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition startPosition = myPosition;
            while(board.isWithinBoard(startPosition)) {
                ChessPosition newPosition = startPosition.movePos(xShift,yShift);
                if(!board.isWithinBoard(newPosition)) {break;}
                if(board.getPiece(newPosition) == null) {
                    qMoves.add(new ChessMove(myPosition,newPosition,null));
                } else if(board.getPiece(newPosition).pieceColor != this.pieceColor) {
                    qMoves.add(new ChessMove(myPosition,newPosition,null));
                    break;
                } else {
                    break;
                }
                startPosition = newPosition;
            }
        }
        return qMoves;
    }

    public ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> kMoves = new ArrayList<>();
        int pieceDirections[][] = {
                {1,0}, {1,1}, {0,1}, {-1,1},
                {-1,0}, {-1,-1}, {0,-1}, {1,-1},
        };
        for(int [] direction : pieceDirections) {
            int xShift = direction[0];
            int yShift = direction[1];
            ChessPosition newPosition = myPosition.movePos(xShift,yShift);
            if(board.isWithinBoard(newPosition)) {
                if(board.getPiece(newPosition) == null) {
                    kMoves.add(new ChessMove(myPosition,newPosition,null));
                } else if(board.isEnemy(board,myPosition,newPosition)) {
                    kMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return kMoves;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        PieceType type = board.getPiece(myPosition).getPieceType();

        switch (type) {
            case PAWN:
                moves = pawnMoves(board, myPosition);
                break;
            case ROOK:
                moves = rookMoves(board, myPosition);
                break;
            case KNIGHT:
                moves = knightMoves(board, myPosition);
                break;
            case BISHOP:
                moves = bishopMoves(board, myPosition);
                break;
            case QUEEN:
                moves = queenMoves(board, myPosition);
                break;
            case KING:
                moves = kingMoves(board, myPosition);
                break;
        }
        return moves;
    }
}
