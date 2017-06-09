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
                boolean isWhite()
                {
                    return true;
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
                boolean isWhite()
                {
                    return false;
                }

                @Override
                public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer)
                {
                    return blackPlayer;
                }
            };

    public abstract int getDirection();
    public abstract boolean isBlack();
    abstract boolean isWhite();

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
