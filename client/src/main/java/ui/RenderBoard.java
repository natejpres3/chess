package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class RenderBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 5;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static void main() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawBoard(out, false);
        out.println();
        drawBoard(out, true);
    }

    private static void drawBoard(PrintStream out, boolean isWhite) {
        String[][] board = initBoard(isWhite);
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRowSquares(out, board[boardRow], boardRow);
        }
        out.println(RESET_BG_COLOR);
    }

    private static String[][] initBoard(boolean isWhite) {
        String[][] board = new String[BOARD_SIZE_IN_SQUARES][BOARD_SIZE_IN_SQUARES];
        //put in pieces
        if (isWhite) {
            board[7] = new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
            board[6] = new String[]{WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN};
            board[1] = new String[]{BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN};
            board[0] = new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        } else {
            board[0] = new String[]{WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
            board[1] = new String[]{WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN};
            board[6] = new String[]{BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN};
            board[7] = new String[]{BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};
        }
        //put in empties
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < BOARD_SIZE_IN_SQUARES; j++) {
                board[i][j] = "   ";
            }
        }
        return board;
    }

    private static void drawRowSquares(PrintStream out, String[] row, int rowInd) {
        for (int col = 0; col < BOARD_SIZE_IN_SQUARES; col++) {
            boolean isBlackSquare = (rowInd + col) % 2 == 0;
            String bgColor = isBlackSquare ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
            out.print(bgColor + " " + row[col] + " " + RESET_BG_COLOR);
        }
        out.println();
    }
}
