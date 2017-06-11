package com.chess.player;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;
import com.chess.board.Tile;
import com.chess.pieces.Piece;
import com.chess.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chess.board.Move.*;

/**
 * Created by hoduo on 6/9/2017.
 */
public class BlackPlayer extends Player
{

    public BlackPlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves)
    {
        super(board, legalMoves, opponentMoves);
    }

    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance()
    {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent()
    {
        return this.board.getWhitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves)
    {
        final List<Move> kingCastles = new ArrayList<>();

        if (this.getKing().isFirstMove() && !this.isInCheck())
        {
            // king side
            if (!this.board.getTile(new Coordinate(5,7)).isOccupied() &&
                    !this.board.getTile(new Coordinate(6, 7)).isOccupied())
            {
                final Tile rookTile = this.board.getTile(new Coordinate(7, 7));
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove())
                {
                    if (Player.calculateAttackOnTile(new Coordinate(5, 7), opponentLegalMoves).isEmpty() &&
                            Player.calculateAttackOnTile(new Coordinate(6, 7), opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook())
                    {
                        kingCastles.add(new KingSideCastleMove(this.board,
                                this.getKing(),
                                new Coordinate(6, 7),
                                (Rook) rookTile.getPiece(),
                                rookTile.getCoordinate(),
                                new Coordinate(5, 7)));
                    }
                }
            }

            // queen side
            if (!this.board.getTile(new Coordinate(1,7)).isOccupied() &&
                    !this.board.getTile(new Coordinate(2, 7)).isOccupied() &&
                    !this.board.getTile(new Coordinate(3, 7)).isOccupied())
            {
                final Tile rookTile = this.board.getTile(new Coordinate(0, 7));
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove())
                {
                    kingCastles.add(new QueenSideCastleMove(this.board,
                            this.getKing(),
                            new Coordinate(2, 7),
                            (Rook) rookTile.getPiece(),
                            rookTile.getCoordinate(),
                            new Coordinate(3, 7)));
                }
            }
        }

        return kingCastles;
    }

    @Override
    public String toString()
    {
        return "Black";
    }
}
