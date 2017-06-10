package com.chess.board;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.pieces.Piece;

/**
 * Created by hoduo on 6/9/2017.
 */
public class BoardUtils
{
    private BoardUtils()
    {
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
        return "Not implement yet";
    }
}
