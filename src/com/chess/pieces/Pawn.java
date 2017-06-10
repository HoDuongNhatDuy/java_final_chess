package com.chess.pieces;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.Move;
import com.chess.board.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoduo on 6/9/2017.
 */
public class Pawn extends Piece
{
    public Pawn(Coordinate coordinate, Alliance alliance)
    {
        super(PieceType.PAWN, coordinate, alliance, true);
    }

    public Pawn(Coordinate coordinate, Alliance alliance, boolean isFirstMove)
    {
        super(PieceType.PAWN, coordinate, alliance, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        // check straight move
        Coordinate straightMoveCoordinate = new Coordinate(this.coordinate.getX(), this.coordinate.getY() + 1 * this.alliance.getDirection());
        final Tile straightMoveTile = board.getTile(straightMoveCoordinate);
        if (!straightMoveTile.isOccupied())
        {
            legalMoves.add(new Move.MajorMove(board, this, straightMoveCoordinate));
        }
        if (this.isFirstMove)
        {
            Coordinate firstStraightMoveCoordinate = new Coordinate(this.coordinate.getX(), this.coordinate.getY() + 2 * this.alliance.getDirection());

            final Tile firstStraightMoveTile = board.getTile(firstStraightMoveCoordinate);
            if (!firstStraightMoveTile.isOccupied())
            {
                legalMoves.add(new Move.MajorMove(board, this, firstStraightMoveCoordinate));
            }
        }

        // check attack coordinate
        Coordinate[] attackCoordinate = {
                new Coordinate(this.coordinate.getX() - 1, this.coordinate.getY() + 1 * this.alliance.getDirection()),
                new Coordinate(this.coordinate.getX() + 1, this.coordinate.getY() + 1 * this.alliance.getDirection())
        };

        for (Coordinate currentCoordinate : attackCoordinate)
        {
            if (BoardUtils.isValidTileCoordinate(currentCoordinate))
            {
                final Tile candidateDestinationTile = board.getTile(currentCoordinate);
                if (candidateDestinationTile.isOccupied())
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    if (pieceAtDestination.getAlliance() != this.alliance)
                    {
                        legalMoves.add(new Move.AttackMove(board, this, currentCoordinate, pieceAtDestination));
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public String toString()
    {
        return PieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(Move move)
    {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }
}
