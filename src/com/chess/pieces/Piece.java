package com.chess.pieces;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;

import java.util.List;

/**
 * Created by hoduo on 6/9/2017.
 */
public abstract class Piece
{

    protected final PieceType pieceType;
    protected final Coordinate coordinate;
    protected final Alliance alliance;
    protected boolean isFirstMove;
    private final int cachedHashCode;

    public Piece(final PieceType pieceType, final Coordinate coordinate, final Alliance alliance, final boolean isFirstMove)
    {
        this.coordinate = coordinate;
        this.alliance = alliance;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;

        cachedHashCode = computeHashCode();
    }

    private int computeHashCode()
    {
        int result = this.pieceType.hashCode();
        result = result * 31 + this.alliance.hashCode();
        result = result * 31 + this.coordinate.getX();
        result = result * 31 + this.coordinate.getY();
        result = result * 31 + (this.isFirstMove() ? 1 : 0);

        return result;
    }

    @Override
    public int hashCode()
    {
        return cachedHashCode;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (this == other)
            return true;
        if (!(other instanceof Piece))
            return false;
        final Piece otherPiece= (Piece)other;

        return this.coordinate.equals(otherPiece.getCoordinate()) &&
                this.alliance == otherPiece.getAlliance() &&
                this.pieceType == otherPiece.getPieceType() &&
                this.isFirstMove == otherPiece.isFirstMove();

    }

    public void setFirstMove(boolean firstMove)
    {
        isFirstMove = firstMove;
    }

    public abstract Piece movePiece(Move move);

    public PieceType getPieceType()
    {
        return pieceType;
    }

    public Alliance getAlliance()
    {
        return alliance;
    }

    public Coordinate getCoordinate()
    {
        return coordinate;
    }

    public boolean isFirstMove()
    {
        return isFirstMove;
    }

    public abstract List<Move> calculateLegalMoves(final Board board);

    public int getPieceValue()
    {
        return pieceType.getPieceValue();
    }

    public enum PieceType
    {
        PAWN("P", 1)
                {
                    @Override
                    public boolean isKing()
                    {
                        return false;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return false;
                    }
                },
        KNIGHT("N", 2)
                {
                    @Override
                    public boolean isKing()
                    {
                        return false;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return false;
                    }
                },
        BISHOP("B", 3)
                {
                    @Override
                    public boolean isKing()
                    {
                        return false;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return false;
                    }
                },
        ROOK("R", 5)
                {
                    @Override
                    public boolean isKing()
                    {
                        return false;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return true;
                    }
                },
        QUEEN("Q", 9)
                {
                    @Override
                    public boolean isKing()
                    {
                        return false;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return false;
                    }
                },
        KING("K", 100)
                {
                    @Override
                    public boolean isKing()
                    {
                        return true;
                    }

                    @Override
                    public boolean isRook()
                    {
                        return false;
                    }
                };

        private String pieceName;
        private int pieceValue;

        PieceType(final String pieceName, final int pieceValue)
        {
            this.pieceName = pieceName;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString()
        {
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();

        public int getPieceValue()
        {
            return pieceValue;
        }
    }
}
