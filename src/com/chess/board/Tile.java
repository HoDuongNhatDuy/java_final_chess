package com.chess.board;

import com.chess.Coordinate;
import com.chess.pieces.Piece;

/**
 * Created by hoduo on 6/9/2017.
 */
public abstract class Tile
{
    protected final Coordinate coordinate;
    private static final EmptyTile[][] EMPTY_TILES = createAllPossibleEmptyTile();

    Tile (Coordinate coordinate){
        this.coordinate = coordinate;
    }

    private static EmptyTile[][] createAllPossibleEmptyTile(){
        final EmptyTile[][] emptyTileMap = new EmptyTile[8][8];

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
            {
                emptyTileMap[x][y] = new EmptyTile(new Coordinate(x, y));
            }

        return emptyTileMap;
    }

    public static Tile createTile(final Coordinate coordinate, final Piece piece){
        return piece != null ? new OccupiedTile(coordinate, piece) : EMPTY_TILES[coordinate.getX()][coordinate.getY()];
    }

    public abstract boolean isOccupied();
    public abstract Piece getPiece();

    public Coordinate getCoordinate()
    {
        return this.coordinate;
    }

    public static final class EmptyTile extends Tile{

        EmptyTile(final Coordinate coordinate)
        {
            super(coordinate);
        }

        @Override
        public String toString()
        {
            return "-";
        }

        @Override
        public boolean isOccupied()
        {
            return false;
        }

        @Override
        public Piece getPiece()
        {
            return null;
        }
    }

    public static final class OccupiedTile extends Tile{

        private final Piece piece;

        OccupiedTile(final Coordinate coordinate, final Piece piece)
        {
            super(coordinate);
            this.piece = piece;
        }

        @Override
        public String toString()
        {
            return getPiece().getAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public boolean isOccupied()
        {
            return true;
        }

        @Override
        public Piece getPiece()
        {
            return piece;
        }
    }


}
