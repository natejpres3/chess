package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    boolean isWhitesTurn;

    public ChessGame() {
        board = new ChessBoard();
        this.isWhitesTurn = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if(isWhitesTurn) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        isWhitesTurn = !isWhitesTurn;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        //generate the piece moves for the piece at that position
//        ChessPiece piece = board.getPiece(startPosition);
//        Collection<ChessMove> pieceMoves = piece.pieceMoves(board,startPosition);

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */

    //helper function to find the king position so we can check for checkmate and stalemate
    public ChessPosition findKing(ChessBoard board, TeamColor teamColor) {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);
        return isUnderAttack(kingPosition,isWhitesTurn);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }

    // opponent piece function to get all of the opponent pieces to use for under attack function
    public ArrayList<ChessPiece> getOpponentPieces(ChessBoard board, boolean isWhitesTurn) {
        ArrayList<ChessPiece> opponentPieces = new ArrayList<>();
        TeamColor opponentColor = isWhitesTurn ? TeamColor.BLACK : TeamColor.WHITE;
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() != opponentColor) {
                    opponentPieces.add(new ChessPiece(opponentColor,board.getPiece(position).getPieceType()));
                }
            }
        }
        return opponentPieces;
    }

    ArrayList<ChessPosition> getEndAttackPosition(Collection<ChessMove> attackMoves) {
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for(var move : attackMoves) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    //generate an isUnderAttack function for checking if the king is in checkmate
    public boolean isUnderAttack(ChessPosition myPosition, boolean isWhitesTurn) {
        ArrayList<ChessPiece> opponentPieces = getOpponentPieces(board, isWhitesTurn);
        //iterate over each opponent piece producing its potential moves and seeing if those include myPosition
        for(var piece : opponentPieces) {
            Collection<ChessMove> attackMoves = piece.pieceMoves(board, myPosition);
            ArrayList<ChessPosition> attackEndPosition = getEndAttackPosition(attackMoves);
            //check if myPosition is in that opponent pieces attack range
            if(attackEndPosition.contains(myPosition)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", isWhitesTurn=" + isWhitesTurn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return isWhitesTurn == chessGame.isWhitesTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, isWhitesTurn);
    }
}
