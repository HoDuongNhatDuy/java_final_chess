package com.chess.board;

import com.chess.Alliance;
import com.chess.Coordinate;
import com.chess.pieces.*;
import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by hoduo on 6/9/2017.
 */
public class Board
{

    private Tile[][] gameBoard = new Tile[8][8];
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    public Board(Builder builder)
    {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);

        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardLegalMoves, blackStandardLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardLegalMoves, whiteStandardLegalMoves);
        this.currentPlayer = builder.nextMoveMarker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();

        for (int x = 7; x >=0; x--)
        {
            for (int y = 7; y >= 0; y--)
            {
                final String tileText = this.gameBoard[y][x].toString();
                builder.append(String.format("%3s", tileText));
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }


    public Collection<Piece> getWhitePieces()
    {
        return this.whitePieces;
    }

    public Collection<Piece> getBlackPieces()
    {
        return this.blackPieces;
    }

    public WhitePlayer getWhitePlayer()
    {
        return whitePlayer;
    }

    public BlackPlayer getBlackPlayer()
    {
        return blackPlayer;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces)
    {
        final List<Move> legalMoves = new ArrayList<>();

        for (final Piece piece : pieces)
        {
            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return legalMoves;
    }

    private static Collection<Piece> calculateActivePieces(final Tile[][] gameBoard, final Alliance alliance)
    {
        final List<Piece> activePieces = new ArrayList<>();
        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                if (gameBoard[x][y].isOccupied())
                {
                    final Piece piece = gameBoard[x][y].getPiece();
                    if (piece.getAlliance() == alliance)
                    {
                        activePieces.add(piece);
                    }
                }
            }
        }
        return activePieces;
    }

    public Tile getTile(Coordinate candidateDestinationCoordinate)
    {
        return gameBoard[candidateDestinationCoordinate.getX()][candidateDestinationCoordinate.getY()];
    }

    public Pawn getEnPassantPawn()
    {
        return enPassantPawn;
    }


    private static Tile[][] createGameBoard(final Builder builder)
    {
        Tile[][] tiles = new Tile[8][8];

        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
            {
                tiles[x][y] = Tile.createTile(new Coordinate(x, y), builder.boardConfig[x][y]);
            }

        return tiles;
    }

    public static Board createStandardBoard(Alliance myAlliance)
    {
        final Builder builder = new Builder();
        builder.nextMoveMarker = myAlliance;

        // WHITE
        builder.setPiece(new Rook(new Coordinate(0, 0), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(1, 0), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(2, 0), Alliance.WHITE));
        builder.setPiece(new Queen(new Coordinate(myAlliance.queenX(), 0), Alliance.WHITE));
        builder.setPiece(new King(new Coordinate(myAlliance.kingX(), 0), Alliance.WHITE));
        builder.setPiece(new Bishop(new Coordinate(5, 0), Alliance.WHITE));
        builder.setPiece(new Knight(new Coordinate(6, 0), Alliance.WHITE));
        builder.setPiece(new Rook(new Coordinate(7, 0), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(0, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(1, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(2, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(3, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(4, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(5, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(6, 1), Alliance.WHITE));
        builder.setPiece(new Pawn(new Coordinate(7, 1), Alliance.WHITE));

        // BLACK
        builder.setPiece(new Rook(new Coordinate(0, 7), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(1, 7), Alliance.BLACK));
        builder.setPiece(new Bishop(new Coordinate(2, 7), Alliance.BLACK));
        builder.setPiece(new Queen(new Coordinate(myAlliance.queenX(), 7), Alliance.BLACK));
        builder.setPiece(new King(new Coordinate(myAlliance.kingX(), 7), Alliance.BLACK));
        builder.setPiece(new Bishop(new Coordinate(5, 7), Alliance.BLACK));
        builder.setPiece(new Knight(new Coordinate(6, 7), Alliance.BLACK));
        builder.setPiece(new Rook(new Coordinate(7, 7), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(0, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(1, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(2, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(3, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(4, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(5, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(6, 6), Alliance.BLACK));
        builder.setPiece(new Pawn(new Coordinate(7, 6), Alliance.BLACK));

        return builder.build();
    }

    public Collection<Move> getAllLegalMoves()
    {
        List<Move> result = new ArrayList<>();

        result.addAll(this.whitePlayer.getLegalMoves());
        result.addAll(this.blackPlayer.getLegalMoves());

        return result;
    }

    public static class Builder
    {
        Piece[][] boardConfig = new Piece[8][8];
        Alliance nextMoveMarker = Alliance.WHITE;
        Pawn enPassantPawn = null;

        public Builder()
        {
        }

        public Builder setPiece(final Piece piece)
        {
            this.boardConfig[piece.getCoordinate().getX()][piece.getCoordinate().getY()] = piece;
            return this;
        }

        public Builder setMoveMarker(final Alliance nextMoveMarker)
        {
            this.nextMoveMarker = nextMoveMarker;
            return this;
        }

        public Board build()
        {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn)
        {
            this.enPassantPawn = enPassantPawn;
        }
    }

}
