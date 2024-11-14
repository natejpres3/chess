package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class RenderBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 5;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        drawBoard(out);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawBoard(PrintStream out) {
        for(int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            drawRow(out, boardRow);
            if(boardRow < BOARD_SIZE_IN_SQUARES-1) {
                drawHorizontal(out);
                setBlack(out);
            }
        }
    }

    private static void drawRow(PrintStream out, int boardRow) {
        for(int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for(int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                setWhite(out);
                if(squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int preLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffLength = SQUARE_SIZE_IN_PADDED_CHARS - preLength - 1;

                    out.print(EMPTY.repeat(preLength));

                    if(boardRow == 0 || boardRow == 7) {
                        out.print(getPieces(boardRow, boardCol));
                    } else if(boardRow == 1 || boardRow == 6) {
                        out.print(boardRow == 1 ? WHITE_PAWN : BLACK_PAWN);
                    } else {
                        out.print(EMPTY);
                    }
                    out.print(EMPTY.repeat(suffLength));
                } else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS));
                }
                //set vertical separators
                if(boardCol < BOARD_SIZE_IN_SQUARES - 1) {
                    setRed(out);
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_PADDED_CHARS));
                }
                setBlack(out);
            }
            out.println();
        }
    }

    private static void drawHorizontal(PrintStream out) {
        int spacesBoardSize = BOARD_SIZE_IN_SQUARES * SQUARE_SIZE_IN_PADDED_CHARS +
                (BOARD_SIZE_IN_SQUARES - 1) * LINE_WIDTH_IN_PADDED_CHARS;
        for(int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setRed(out);
            out.print(EMPTY.repeat(spacesBoardSize));
            setBlack(out);
            out.println();
        }
    }

    private static String getPieces(int row, int col) {
        if(row == 0) {
            switch (col) {
                case 0:
                case 7:
                    return BLACK_ROOK;
                case 1:
                case 6:
                    return BLACK_KNIGHT;
                case 2:
                case 5:
                    return BLACK_BISHOP;
                case 3:
                    return BLACK_QUEEN;
                case 4:
                    return BLACK_KING;
            }
        }
        if(row == 7) {
            switch (col) {
                case 0:
                case 7:
                    return WHITE_ROOK;
                case 1:
                case 6:
                    return WHITE_KNIGHT;
                case 2:
                case 5:
                    return WHITE_BISHOP;
                case 3:
                    return WHITE_QUEEN;
                case 4:
                    return WHITE_KING;
            }
        }
        return EMPTY;
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setRed(PrintStream out) {
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
