package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class RenderBoard {
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 5;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;
    private static Random rand = new Random();

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
                //drawhorizontalline
                //setblack
            }
        }
    }

    private static void drawRow(PrintStream out, int boardRow) {
        for(int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            for(int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                //set white(out)
                if(squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int preLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffLength = SQUARE_SIZE_IN_PADDED_CHARS - preLength - 1;

                    out.print(EMPTY.repeat(preLength));

                    if(boardRow == 0 || boardRow == 7) {
                        out.print(//get chess piece);
                    } else if(boardRow == 1 || boardRow == 6) {
                        
                    }
                }
            }
        }
    }
}
