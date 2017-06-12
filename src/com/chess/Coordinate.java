package com.chess;

import com.chess.pieces.Piece;

import java.util.Collection;

/**
 * Created by hoduo on 6/9/2017.
 */
public class Coordinate
{
    private int x, y;

    public Coordinate(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /*
    *  "x-y"
    * */
    public Coordinate(String coordinateString)
    {
        String[] map = coordinateString.split("-");

        if (map.length != 2)
        {
            this.x = -1;
            this.y = -1;
        }

        this.x = Integer.valueOf(map[0]);
        this.y = Integer.valueOf(map[1]);
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public Coordinate add(Coordinate coordinate)
    {
        return new Coordinate(this.x + coordinate.x, this.y + coordinate.y);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = result * prime + x;
        result = result * prime + y;

        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        if (!(other instanceof Coordinate))
            return false;
        final Coordinate otherCoordinate = (Coordinate)other;

        return this.x == otherCoordinate.x && this.y == otherCoordinate.y;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + y + ")";
    }
}
