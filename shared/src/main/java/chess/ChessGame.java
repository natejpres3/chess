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
    boolean isGameDone;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        this.isWhitesTurn = true;
        this.isGameDone = false;
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
        if(team == TeamColor.WHITE) {
            isWhitesTurn = true;
        } else {
            isWhitesTurn = false;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> filterIllegals(ChessBoard board, Collection<ChessMove> possibleMoves, ChessPosition startPosition) {
        Collection<ChessMove> legalMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);
        // for each move, see if it results in a check on the king
        for(var move : possibleMoves) {
            ChessBoard tempBoard = board.copy();
            tempBoard.testMove(piece,move);
            ChessPosition theKing = findKing(tempBoard,piece.getTeamColor());
            if(!isUnderAttack(tempBoard, theKing,piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        //if there is no piece there
        if(piece == null) {
            return null;
        }
        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
        return filterIllegals(board,pieceMoves,startPosition);
    }

    //method for checking if the right team color is making the move
    public boolean isRightTurn(ChessPiece piece) {
        if(piece.getTeamColor() == TeamColor.WHITE && isWhitesTurn) {
            return true;
        } else if (piece.getTeamColor() == TeamColor.BLACK && !isWhitesTurn) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if(piece == null) {
            throw new InvalidMoveException();
        }
        TeamColor pieceColor = piece.getTeamColor();
        if(!isRightTurn(piece)) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if(!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        // account for pawn promotions
        if(move.getPromotionPiece() != null) {
            piece = new ChessPiece(pieceColor,move.getPromotionPiece());
        }
        board.addPiece(move.getEndPosition(),piece);
        board.removePiece(move.getStartPosition());
        isWhitesTurn = !isWhitesTurn;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);
        return isUnderAttack(board, kingPosition,teamColor);
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
                ChessPosition position = new ChessPosition(i+1,j+1);
                ChessPiece piece = board.getPiece(position);
                if(piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        //check if its in check right now
        if(!isInCheck(teamColor)) {
            return false;
        } else {
            //See if a king move can get it out of check
            ChessPosition kingPosition = findKing(board,teamColor);
            Collection<ChessMove> validKingMoves = validMoves(kingPosition);
            if(!validKingMoves.isEmpty()) {return false;}
            //see if another pieces moves can get it out of check
            ArrayList<ChessPosition> teamPieces = getTeamPieces(board,teamColor);
            for(ChessPosition position : teamPieces) {
                Collection<ChessMove> validTeamMoves = validMoves(position);
                if(!validTeamMoves.isEmpty()) {return false;}
            }
            isGameDone = true;
            return true;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(board, teamColor);
        if(!isUnderAttack(board,kingPosition,teamColor)) {
            Collection<ChessMove> validKingMoves = validMoves(kingPosition);
            if(!validKingMoves.isEmpty()) {return false;}
            //see if another pieces moves can get it out of check
            ArrayList<ChessPosition> teamPieces = getTeamPieces(board,teamColor);
            for(ChessPosition position : teamPieces) {
                Collection<ChessMove> validTeamMoves = validMoves(position);
                if(!validTeamMoves.isEmpty()) {return false;}
            }
            isGameDone = true;
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    public ArrayList<ChessPosition> getTeamPieces(ChessBoard board, TeamColor teamColor) {
        ArrayList<ChessPosition> teamPieces = new ArrayList<>();
        for(int i=1; i<9; i++) {
            for(int j=1; j<9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor) {
                    teamPieces.add(position);
                }
            }
        }
        return teamPieces;
    }

    public ArrayList<ChessPosition> getOpponentPosition(ChessBoard board, TeamColor opponentColor) {
        ArrayList<ChessPosition> opponentPositions = new ArrayList<>();
        for(int i=1; i<9; i++) {
            for(int j=1; j<9; j++) {
                ChessPosition position = new ChessPosition(i,j);
                if(board.getPiece(position) != null && board.getPiece(position).getTeamColor() == opponentColor) {
                    opponentPositions.add(position);
                }
            }
        }
        return opponentPositions;
    }

    ArrayList<ChessPosition> getEndAttackPosition(Collection<ChessMove> attackMoves) {
        ArrayList<ChessPosition> endPositions = new ArrayList<>();
        for(var move : attackMoves) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    public boolean isUnderAttack(ChessBoard tempBoard, ChessPosition myPosition, TeamColor color) {
        TeamColor opponentColor = color == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        ArrayList<ChessPosition> opponentPositions = getOpponentPosition(tempBoard, opponentColor);
        //iterate over each opponent piece producing its potential moves and seeing if those include myPosition
        for(var position : opponentPositions) {
            ChessPiece piece = tempBoard.getPiece(position);
            Collection<ChessMove> attackMoves = piece.pieceMoves(tempBoard, position);
            ArrayList<ChessPosition> attackEndPosition = getEndAttackPosition(attackMoves);
            //check if myPosition is in that opponent pieces attack range
            if(attackEndPosition.contains(myPosition)) {
                return true;
            }
        }
        return false;
    }

    public void setGameDone(Boolean isGameDone) {
        this.isGameDone = isGameDone;
    }

    public boolean getIsGameDone() {
        return isGameDone;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isWhitesTurn == chessGame.isWhitesTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, isWhitesTurn);
    }
}
