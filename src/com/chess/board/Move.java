package com.chess.board;

import com.chess.Coordinate;
import com.chess.board.Board.Builder;
import com.chess.pieces.Pawn;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;
import org.omg.SendingContext.RunTime;

import java.awt.font.FontRenderContext;

/**
 * Created by hoduo on 6/9/2017.
 */
public abstract class Move
{
    final Board board;
    final Piece piece;
    final Coordinate destinationCoordinate;

    public static final Move NULL_MOVE = new NullMove();

    public Coordinate getDestinationCoordinate()
    {
        return destinationCoordinate;
    }

    public Move(final Board board, final Piece piece, final Coordinate destinationCoordinate)
    {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = result * prime + this.piece.hashCode();
        result = result * prime + this.getDestinationCoordinate().getX();
        result = result * prime + this.getDestinationCoordinate().getY();

        return result;
    }

    @Override
    public boolean equals(Object other)
    {
        if (this == other)
            return true;
        if (!(other instanceof Move))
            return false;
        final Move move = (Move)other;

        return this.getDestinationCoordinate().equals(move.getDestinationCoordinate()) &&
                this.getMovedPiece().equals(move.getMovedPiece());
    }

    public Piece getMovedPiece()
    {
        return this.piece;
    }

    public boolean isAttacked()
    {
        return false;
    }

    public boolean isCastlingMove()
    {
        return false;
    }

    public Piece getAttackedPiece()
    {
        return null;
    }

    public Board execute()
    {
        final Builder builder = new Builder();

        for (final Piece piece : this.board.getCurrentPlayer().getActivePieces())
        {
            if (!this.piece.equals(piece))
            {
                builder.setPiece(piece);
            }
        }

        for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
            builder.setPiece(piece);

        builder.setPiece(this.piece.movePiece(this));
        builder.setMoveMarker(this.board.getCurrentPlayer().getOpponent().getAlliance());

        return builder.build();
    }

    public Coordinate getCurrentCoordinate()
    {
        return this.getMovedPiece().getCoordinate();
    }

    public static final class MajorMove extends Move
    {
        public MajorMove(final Board board, final Piece piece, final Coordinate destinationCoordinate)
        {
            super(board, piece, destinationCoordinate);
        }

    }

    public static class AttackMove extends Move
    {
        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode()
        {
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other)
        {
            if (this == other)
                return true;
            if (!(other instanceof AttackMove))
                return false;
            final AttackMove attackMove = (AttackMove)other;
            return super.equals(attackMove) &&
                    getAttackedPiece().equals(attackMove.getAttackedPiece());
        }

        @Override
        public Board execute()
        {
            return null;
        }

        @Override
        public boolean isAttacked()
        {
            return true;
        }

        @Override
        public Piece getAttackedPiece()
        {
            return attackedPiece;
        }
    }

    public static final class PawnMove extends Move
    {
        public PawnMove(final Board board, final Piece piece, final Coordinate destinationCoordinate)
        {
            super(board, piece, destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove
    {
        public PawnAttackMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

    }

    public static final class PawnEdPassantAttackMove extends PawnAttackMove
    {
        public PawnEdPassantAttackMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

    }

    public static final class PawnJump extends Move
    {
        public PawnJump(final Board board, final Piece piece, final Coordinate destinationCoordinate)
        {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();

            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            {
                if (!this.getMovedPiece().equals(piece))
                    builder.setPiece(piece);
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            final Pawn movedPawn = (Pawn)this.getMovedPiece().movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMarker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
    }

    static abstract class CastleMove extends Move
    {
        protected final Rook castleRook;
        protected final Coordinate castleRookStart;
        protected final Coordinate castleRookDestination;

        public CastleMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Rook castleRook, final Coordinate castleRookStart, final Coordinate castleRookDestination)
        {
            super(board, piece, destinationCoordinate);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDestination = castleRookDestination;
        }

        public Rook getCastleRook()
        {
            return castleRook;
        }

        @Override
        public boolean isCastlingMove()
        {
            return true;
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();

            for(final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            {
                if (!this.getMovedPiece().equals(piece) && !this.getCastleRook().equals(piece))
                    builder.setPiece(piece);
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            builder.setPiece(this.getMovedPiece().movePiece(this));
            builder.setPiece(new Rook(this.getDestinationCoordinate(), this.getCastleRook().getAlliance()));
            builder.setMoveMarker(this.board.getCurrentPlayer().getAlliance());

            return builder.build();
        }
    }

    public static final class KingSideCastleMove extends CastleMove
    {
        public KingSideCastleMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Rook castleRook, final Coordinate castleRookStart, final Coordinate castleRookDestination)
        {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString()
        {
            return "0-0";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove
    {
        public QueenSideCastleMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Rook castleRook, final Coordinate castleRookStart, final Coordinate castleRookDestination)
        {
            super(board, piece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination);
        }

        @Override
        public String toString()
        {
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move
    {
        public NullMove()
        {
            super(null, null, new Coordinate(-1, -1));
        }

        @Override
        public Board execute()
        {
            throw new RuntimeException("Can not execute the null move");
        }

    }

    public static class MoveFactory
    {
        private MoveFactory()
        {
            throw new RuntimeException("Move Factory class can not instantiable");
        }

        public static Move createMove(final Board board, final Coordinate currentCoordinate, final Coordinate destinationCoorinate)
        {
            for (final Move move : board.getAllLegalMoves())
            {
                if (move.getCurrentCoordinate().equals(currentCoordinate) &&
                        move.getDestinationCoordinate().equals(destinationCoorinate))
                {
                    return move;
                }
            }

            return NULL_MOVE;
        }
    }
}
