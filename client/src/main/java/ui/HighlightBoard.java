package ui;

import chess.*;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class HighlightBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 5;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static void printBoard(ChessGame game, boolean isWhitePerspective, ChessPosition chessPosition) {
        ChessBoard board = game.getBoard();
        System.out.print(ERASE_SCREEN);

        Collection<ChessMove> highlightMoves = game.validMoves(chessPosition);

        int rowStart = isWhitePerspective ? 7 : 0;
        int rowEnd = isWhitePerspective ? -1 : 8;
        int rowStep = isWhitePerspective ? -1 : 1;

        int colStart = isWhitePerspective ? 0 : 7;
        int colEnd = isWhitePerspective ? 8 : -1;
        int colStep = isWhitePerspective ? 1 : -1;

        System.out.print("   ");
        for(char col = (char) ('a' + colStart); col != 'a' + colEnd; col += colStep) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        for(int row = rowStart; row != rowEnd; row += rowStep) {
            System.out.print((row+1) + " ");

            for(int col = colStart; col != colEnd; col += colStep) {
                ChessPosition currentPos = new ChessPosition(row+1, col+1);
                boolean isBlackSquare = (row + col) % 2 != 0;
                String bgColor = isBlackSquare ? SET_BG_COLOR_DARK_GREY : SET_BG_COLOR_WHITE;

                if(highlightMoves.stream().anyMatch(move -> move.getEndPosition().equals(currentPos))) {
                    bgColor = SET_BG_COLOR_YELLOW;
                }
                System.out.print(bgColor);

                ChessPiece piece = board.getPiece(new ChessPosition(row+1, col+1));
                if(piece == null) {
                    System.out.print(EMPTY);
                } else {
                    System.out.print(getSymbol(piece));
                }
                System.out.print(RESET_BG_COLOR);
            }
            System.out.println(" " + (row + 1));
        }

        System.out.print("   ");
        for(char col = (char) ('a' + colStart); col != 'a' + colEnd; col += colStep) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }


    private static String getSymbol(ChessPiece piece) {
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        switch(piece.getPieceType()) {
            case PAWN: return white ? WHITE_PAWN : BLACK_PAWN;
            case ROOK: return white ? WHITE_ROOK : BLACK_ROOK;
            case KNIGHT: return white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case BISHOP: return white ? WHITE_BISHOP : BLACK_BISHOP;
            case QUEEN: return white ? WHITE_QUEEN : BLACK_QUEEN;
            case KING: return white ? WHITE_KING : BLACK_KING;
            default: return EMPTY;
        }
    }
}
