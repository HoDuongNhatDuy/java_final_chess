package com.chess.player;

import com.chess.board.Move;

/**
 * Created by hoduo on 6/9/2017.
 */
public enum MoveStatus
{
    DONE
            {
                @Override
                public boolean isDone()
                {
                    return true;
                }
            },
   ILLEGAL_MOVE
           {
               @Override
               public boolean isDone()
               {
                   return false;
               }
           },
    LEAVES_PLAYER_IN_CHECK
            {
                @Override
                public boolean isDone()
                {
                    return false;
                }
            };

    public abstract boolean isDone();
}
