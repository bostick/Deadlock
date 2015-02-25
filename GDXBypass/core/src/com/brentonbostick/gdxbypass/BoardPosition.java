package com.brentonbostick.gdxbypass;

/**
 * Created by brenton on 2/22/2015.
 */
public class BoardPosition {

    Board board;
    int col;
    int row;

    public BoardPosition(Board board, int col, int row) {
        this.board = board;
        this.col = col;
        this.row = row;
    }

}
