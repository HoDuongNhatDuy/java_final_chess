package com.chess.player;

import com.chess.Coordinate;
import com.chess.board.Move;

import java.util.Collection;

/**
 * Created by Danopie on 6/13/2017.
 */
public class AIPlayer {
    public Coordinate[] getMoveCoordinate(Collection<Move> userLegalMoves, Collection<Move> opponentLegalMoves){
        /*System.out.println("User: ");
        for(Move move:userLegalMoves){
            System.out.println(move.toString());
        }
        System.out.println("AI: ");
        for(Move move:opponentLegalMoves){
            System.out.println(move.toString());
        }*/
        return new Coordinate[]{
                new Coordinate(2,1),
                new Coordinate(2,2)
        };
    }
}
