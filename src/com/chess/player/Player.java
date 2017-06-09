package com.chess.player;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.pieces.King;
import com.chess.pieces.Piece;
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.awt.peer.CanvasPeer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hoduo on 6/9/2017.
 */
public abstract class Player
{
    protected final Board board;
    private final King king;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentMoves)
    {
        this.board = board;
        this.king = establishKing();
        this.isInCheck = !Player.calculateAttackOnTile(this.king.getCoordinate(), opponentMoves).isEmpty();

        List<Move> legalMovesTmp = new ArrayList<>();
        legalMovesTmp.addAll(legalMoves);
        legalMovesTmp.addAll(calculateKingCastles(legalMoves, opponentMoves));
        this.legalMoves = legalMovesTmp;
    }

    protected static Collection<Move> calculateAttackOnTile(Coordinate coordinate, Collection<Move> moves)
    {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves)
        {
            if (coordinate.equals(move.getDestinationCoordinate()))
            {
                attackMoves.add(move);
            }
        }

        return attackMoves;
    }

    private King establishKing()
    {
        for (final Piece piece : getActivePieces())
        {
            if (piece.getPieceType().isKing())
                return (King)piece;
        }

        throw new RuntimeException("Not found king");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck()
    {
        return this.isInCheck;
    }

    public boolean isInCheckMate()
    {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate()
    {
        return !isInCheck && !hasEscapeMoves();
    }

    private boolean hasEscapeMoves()
    {
        for (Move move : this.legalMoves)
        {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone())
            {
                return true;
            }
        }
        return false;
    }



    public boolean isCastled()
    {
        return false;
    }

    public MoveTransition makeMove(final Move move)
    {
        if (!isMoveLegal(move))
        {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();

        final Collection<Move> kingAttacks = Player.calculateAttackOnTile(
                transitionBoard.getCurrentPlayer().getOpponent().getKing().getCoordinate(),
                transitionBoard.getCurrentPlayer().getLegalMoves()
        );

        if (!kingAttacks.isEmpty())
        {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public King getKing()
    {
        return king;
    }

    public Collection<Move> getLegalMoves()
    {
        return legalMoves;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves);
}
