package chess;

import org.junit.platform.commons.util.AnnotationUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    // remove piece for making a move
    public void removePiece(ChessPosition position) {
        squares[position.getRow()-1][position.getColumn()-1] = null;
    }

    //method for deep copying the board to test moves on
    public ChessBoard copy() {
        ChessBoard copiedBoard = new ChessBoard();
        for(int i=0; i<8; i++) {
            for(int j=0; j<9; j++) {
                ChessPiece piece = squares[i][j];
                if(piece != null) {
                    copiedBoard.addPiece(new ChessPosition(i,j),piece);
                } else {
                    copiedBoard.addPiece(new ChessPosition(i,j), null);
                }
            }
        }
        return copiedBoard;
    }

    public void testMove(ChessPiece piece, ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        addPiece(endPosition,piece);
        removePiece(startPosition);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    public void wipeBoard() {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                squares[i][j] = null;
            }
        }
    }

    public void resetBoard() {
        wipeBoard();

        for(int i=1; i<9; i++) {
            if(i == 1) {
                addPiece(new ChessPosition(i,1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
                addPiece(new ChessPosition(i,2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(i,3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(i,4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
                addPiece(new ChessPosition(i,5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
                addPiece(new ChessPosition(i,6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(i,7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(i,8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            }
            if(i == 2) {
                int j = 1;
                while(j<9) {
                    addPiece(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                    j++;
                }
            }
            if(i == 7) {
                int j = 1;
                while(j<9) {
                    addPiece(new ChessPosition(i,j), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
                    j++;
                }
            }
            if(i == 8) {
                addPiece(new ChessPosition(i,1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
                addPiece(new ChessPosition(i,2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(i,3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(i,4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
                addPiece(new ChessPosition(i,5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
                addPiece(new ChessPosition(i,6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
                addPiece(new ChessPosition(i,7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
                addPiece(new ChessPosition(i,8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            }
        }
    }

    public boolean isWithinBoard(ChessPosition position) {
        if(position.getRow() <= 8 && position.getRow() >= 1 && position.getColumn() <= 8 && position.getColumn() >= 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnemy(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        return board.getPiece(myPosition).getTeamColor() != board.getPiece(newPosition).getTeamColor();
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
