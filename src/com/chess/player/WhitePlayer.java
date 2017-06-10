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
public class WhitePlayer extends Player
{


    public WhitePlayer(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves)
    {
        super(board, legalMoves, opponentMoves);
    }

    @Override
    public Collection<Piece> getActivePieces()
    {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance()
    {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent()
    {
        return this.board.getBlackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves)
    {
        final List<Move> kingCastles = new ArrayList<>();

        if (this.getKing().isFirstMove() && !this.isInCheck())
        {
            // king side
            if (!this.board.getTile(new Coordinate(5,0)).isOccupied() &&
                    !this.board.getTile(new Coordinate(6, 0)).isOccupied())
            {
                final Tile rookTile = this.board.getTile(new Coordinate(7, 0));
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove())
                {
                    if (Player.calculateAttackOnTile(new Coordinate(5, 0), opponentLegalMoves).isEmpty() &&
                            Player.calculateAttackOnTile(new Coordinate(6, 0), opponentLegalMoves).isEmpty() &&
                            rookTile.getPiece().getPieceType().isRook())
                    {
                        kingCastles.add(new KingSideCastleMove(this.board,
                                this.getKing(),
                                new Coordinate(6, 0),
                                (Rook) rookTile.getPiece(),
                                rookTile.getCoordinate(),
                                new Coordinate(5, 0)));
                    }
                }
            }

            // queen side
            if (!this.board.getTile(new Coordinate(1,0)).isOccupied() &&
                    !this.board.getTile(new Coordinate(2, 0)).isOccupied() &&
                    !this.board.getTile(new Coordinate(3, 0)).isOccupied())
            {
                final Tile rookTile = this.board.getTile(new Coordinate(0, 0));
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove())
                {
                    kingCastles.add(new QueenSideCastleMove(this.board,
                            this.getKing(),
                            new Coordinate(2, 0),
                            (Rook) rookTile.getPiece(),
                            rookTile.getCoordinate(),
                            new Coordinate(3, 0)));
                }
            }
        }

        return kingCastles;
    }
}
