package com.chess.pieces;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.BoardUtils;
import com.chess.board.Move;
import com.chess.board.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.chess.board.BoardUtils.*;
import static com.chess.board.Move.*;

/**
 * Created by hoduo on 6/9/2017.
 */
public class Knight extends Piece
{
    private final static Coordinate[] CANDIDATE_MOVE_COORDINATE = {
            new Coordinate(-1, -2),
            new Coordinate(-1, 2),
            new Coordinate(1, -2),
            new Coordinate(1, 2),
            new Coordinate(-2, -1),
            new Coordinate(-2, 1),
            new Coordinate(2, -1),
            new Coordinate(2, 1)
    };

    public Knight(final Coordinate coordinate, final Alliance alliance)
    {
        super(PieceType.KNIGHT, coordinate, alliance, true);
    }

    public Knight(final Coordinate coordinate, final Alliance alliance, boolean isFirstMove)
    {
        super(PieceType.KNIGHT, coordinate, alliance, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Coordinate currentCoordinate : CANDIDATE_MOVE_COORDINATE)
        {
            Coordinate candidateDestinationCoordinate = this.coordinate.add(currentCoordinate);

            if (isValidTileCoordinate(candidateDestinationCoordinate))
            {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isOccupied())
                {
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
                else
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance destinationPieceAlliance = pieceAtDestination.getAlliance();
                    if (this.alliance != destinationPieceAlliance)
                    {
                        legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public String toString()
    {
        return PieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(Move move)
    {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }
}
