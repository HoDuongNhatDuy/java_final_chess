package com.chess.board;

import com.chess.Coordinate;
import com.chess.board.Board.Builder;
import com.chess.pieces.Pawn;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;
import org.omg.SendingContext.RunTime;

import javax.swing.*;
import java.awt.font.FontRenderContext;

/**
 * Created by hoduo on 6/9/2017.
 */
public abstract class Move
{
    final Board board;
    final Piece piece;
    final Coordinate destinationCoordinate;
    final boolean isFirstMove;

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
        this.isFirstMove = piece.isFirstMove();
    }

    private Move(final Board board, final Coordinate destinationCoordinate)
    {
        this.board = board;
        this.piece = null;
        this.destinationCoordinate  = destinationCoordinate;
        this.isFirstMove = false;
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
                this.getCurrentCoordinate().equals(move.getCurrentCoordinate()) &&
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

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return getMovedPiece().getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class MajorAttackMove extends AttackMove
    {

        public MajorAttackMove(Board board, Piece piece, Coordinate destinationCoordinate, Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return getMovedPiece().getPieceType() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
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

        @Override
        public boolean equals(Object other)
        {
            return this == other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
        }
    }

    public static class PawnAttackMove extends AttackMove
    {
        public PawnAttackMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString()
        {
            return BoardUtils.getPositionAtCoordinate(this.getMovedPiece().getCoordinate()).substring(0, 1) + "x" + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);

        }
    }

    public static final class PawnEdPassantAttackMove extends PawnAttackMove
    {
        public PawnEdPassantAttackMove(final Board board, final Piece piece, final Coordinate destinationCoordinate, final Piece attackedPiece)
        {
            super(board, piece, destinationCoordinate, attackedPiece);
        }

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof PawnEdPassantAttackMove && super.equals(other);
        }

        @Override
        public Board execute()
        {
            final Builder builder = new Builder();

            for (final Piece piece : this.board.getCurrentPlayer().getActivePieces())
            {
                if (!(this.getMovedPiece().equals(piece)))
                {
                    builder.setPiece(piece);
                }
            }

            for (final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePieces())
            {
                if (!(piece.equals(this.getAttackedPiece())))
                    builder.setPiece(piece);
            }

            builder.setPiece(this.getMovedPiece().movePiece(this));
            builder.setMoveMarker(this.board.getCurrentPlayer().getOpponent().getAlliance());

            return builder.build();
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

        @Override
        public String toString()
        {
            return getMovedPiece().getPieceType().toString() + BoardUtils.getPositionAtCoordinate(this.destinationCoordinate);
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
            builder.setPiece(new Rook(this.castleRookDestination, this.getCastleRook().getAlliance()));
            builder.setMoveMarker(this.board.getCurrentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = super.hashCode();
            result = prime * result + this.castleRook.hashCode();
            result = prime * result + this.castleRookDestination.hashCode();

            return result;
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;
            if (!(other instanceof CastleMove))
                return false;
            final CastleMove move = (CastleMove) other;

            return super.equals(move) && this.castleRook.equals(move.getCastleRook());
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

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof KingSideCastleMove && super.equals(other);
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

        @Override
        public boolean equals(final Object other)
        {
            return this == other || other instanceof QueenSideCastleMove && super.equals(other);
        }
    }

    public static class PawnPromotion extends Move
    {
        final Move decoratedMove;
        final Pawn promotedPawn;

        public PawnPromotion(final Move decoratedMove)
        {
            super(decoratedMove.board, decoratedMove.getMovedPiece(), decoratedMove.getDestinationCoordinate());

            this.decoratedMove = decoratedMove;
            this.promotedPawn = (Pawn)decoratedMove.getMovedPiece();
        }

        @Override
        public Board execute()
        {
            final Board pawnMovedBoard = this.decoratedMove.execute();
            final Builder builder = new Builder();

            for (Piece piece : pawnMovedBoard.getCurrentPlayer().getActivePieces())
            {
                if (!this.promotedPawn.equals(piece))
                    builder.setPiece(piece);
            }

            for (Piece piece : pawnMovedBoard.getCurrentPlayer().getOpponent().getActivePieces())
                builder.setPiece(piece);

            builder.setPiece(promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMarker(pawnMovedBoard.getCurrentPlayer().getAlliance());

            return builder.build();
        }

        @Override
        public boolean isAttacked()
        {
            return this.decoratedMove.isAttacked();
        }

        @Override
        public Piece getAttackedPiece()
        {
            return this.decoratedMove.getAttackedPiece();
        }

        @Override
        public int hashCode()
        {
            return decoratedMove.hashCode() + (31 * promotedPawn.hashCode());
        }

        @Override
        public boolean equals(Object other)
        {
            return this == other || other instanceof PawnPromotion && super.equals(other);
        }

        @Override
        public String toString()
        {
            return "";
        }
    }

    public static final class NullMove extends Move
    {
        public NullMove()
        {
            super(null, new Coordinate(-1, -1));
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
