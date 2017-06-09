package com.chess.player;

import com.chess.board.Board;
import com.chess.board.Move;

/**
 * Created by hoduo on 6/9/2017.
 */
public class MoveTransition
{
    private final Board transitionBoard;
    private final Move move;
    private final MoveStatus moveStatus;

    public MoveTransition(Board transitionBoard, Move move, MoveStatus moveStatus)
    {
        this.transitionBoard = transitionBoard;
        this.move = move;
        this.moveStatus = moveStatus;
    }

    public MoveStatus getMoveStatus()
    {
        return moveStatus;
    }

    public Board getTransitionBoard()
    {
        return transitionBoard;
    }
}
