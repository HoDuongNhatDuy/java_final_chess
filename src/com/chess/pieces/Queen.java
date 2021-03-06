package com.chess.pieces;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.board.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.chess.board.BoardUtils.isValidTileCoordinate;
import static com.chess.board.Move.AttackMove;
import static com.chess.board.Move.MajorMove;

/**
 * Created by hoduo on 6/9/2017.
 */
public class Queen extends Piece
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

    public Queen(Coordinate coordinate, Alliance alliance)
    {
        super(PieceType.QUEEN, coordinate, alliance);
    }

    @Override
    public List<Move> calculateLegalMoves(Board board)
    {
        List<Move> legalMoves = new ArrayList<>();

        for (Coordinate currentCoordinateOffset: CANDIDATE_MOVE_COORDINATE)
        {
            Coordinate candidateDestinationCoordinate = this.coordinate;

            while(isValidTileCoordinate(candidateDestinationCoordinate))
            {
                candidateDestinationCoordinate = candidateDestinationCoordinate.add(currentCoordinateOffset);

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
                        final Alliance pieceAllianceDestination = pieceAtDestination.getAlliance();
                        if (this.getAlliance() != pieceAllianceDestination)
                        {
                            legalMoves.add(new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }

        return legalMoves;
    }

    @Override
    public String toString()
    {
        return PieceType.QUEEN.toString();
    }

    @Override
    public Queen movePiece(Move move)
    {
        return new Queen(move.getDestinationCoordinate(), move.getMovedPiece().getAlliance());
    }
}
