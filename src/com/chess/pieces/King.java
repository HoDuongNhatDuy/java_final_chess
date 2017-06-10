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
public class King extends Piece
{
    private final static Coordinate[] CANDIDATE_MOVE_COORDINATE = {
            new Coordinate(-1, 0),          // left
            new Coordinate(1, 0),           // right
            new Coordinate(0, 1),           // top
            new Coordinate(0, -1),          // bottom
            new Coordinate(-1, 1),          // top left
            new Coordinate(1, 1),           // top right
            new Coordinate(-1, -1),         // bottom left
            new Coordinate(1, -1)           // bottom right
    };

    public King(Coordinate coordinate, Alliance alliance)
    {
        super(PieceType.KING, coordinate, alliance, true);
    }

    public King(Coordinate coordinate, Alliance alliance, boolean isFirstMove)
    {
        super(PieceType.KING, coordinate, alliance, isFirstMove);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for (Coordinate currentCoordinate : CANDIDATE_MOVE_COORDINATE)
        {
            Coordinate candidateDestinationCoordinate = this.coordinate.add(currentCoordinate);

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate))
            {
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isOccupied())
                {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                }
                else
                {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance destinationPieceAlliance = pieceAtDestination.getAlliance();
                    if (this.alliance != destinationPieceAlliance)
                    {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public String toString()
    {
        return PieceType.KING.toString();
    }

    @Override
    public King movePiece(Move move)
    {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }
}
