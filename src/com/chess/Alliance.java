package com.chess;

import com.chess.player.BlackPlayer;
import com.chess.player.Player;
import com.chess.player.WhitePlayer;

/**
 * Created by hoduo on 6/9/2017.
 */
public enum Alliance
{
    WHITE
            {
                @Override
                public int getDirection()
                {
                    return 1;
                }

                @Override
                public boolean isBlack()
                {
                    return false;
                }

                @Override
                public boolean isWhite()
                {
                    return true;
                }

                @Override
                public int kingX()
                {
                    return 4;
                }

                @Override
                public int queenX()
                {
                    return 3;
                }
                @Override
                public boolean isPawnPromotionSquare(Coordinate coordinate)
                {
                    return coordinate.getY() == 7;
                }

                @Override
                public Alliance getOpposite()
                {
                    return Alliance.BLACK;
                }

                @Override
                public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer)
                {
                    return whitePlayer;
                }
            },  //  this is my alliance
    BLACK
            {
                @Override
                public int getDirection()
                {
                    return -1;
                }

                @Override
                public boolean isBlack()
                {
                    return true;
                }

                @Override
                public boolean isWhite()
                {
                    return false;
                }

                @Override
                public int kingX()
                {
                    return 3;
                }

                @Override
                public int queenX()
                {
                    return 4;
                }
                @Override
                public boolean isPawnPromotionSquare(Coordinate coordinate)
                {
                    return coordinate.getY() == 0;
                }

                @Override
                public Alliance getOpposite()
                {
                    return Alliance.WHITE;
                }

                @Override
                public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer)
                {
                    return blackPlayer;
                }
            };

    public abstract int getDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();
    public abstract int kingX();
    public abstract int queenX();
    public abstract boolean isPawnPromotionSquare(Coordinate coordinate);
    public abstract Alliance getOpposite();
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
