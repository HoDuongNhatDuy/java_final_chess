package com.chess.board;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.pieces.Piece;

/**
 * Created by hoduo on 6/9/2017.
 */
public class BoardUtils
{
    final static String[] BOARD_COLUMN_NAME = {"a", "b", "c", "d", "e", "f", "g", "h"};

    private BoardUtils()
    {
        throw new RuntimeException("Can not instant me");
    }

    public static boolean isValidTileCoordinate(Coordinate coordinate)
    {
        return coordinate.getX() >= 0 &&
                coordinate.getX() <= 7 &&
                coordinate.getY() >= 0 &&
                coordinate.getY() <= 7;
    }

    public static String getPositionAtCoordinate(Coordinate coordinate)
    {
        return BOARD_COLUMN_NAME[coordinate.getX()] + (coordinate.getY() + 1);
    }
}
