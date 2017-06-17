package com.chess.player;

import com.chess.Coordinate;
import com.chess.board.Board;
import com.chess.board.Move;

import java.util.*;

/**
 * Created by Danopie on 6/13/2017.
 */
public class AIPlayer {
    private static final Integer DEFAULT_BEST_VALUE = -1;

    public Coordinate[] getMoveCoordinate(Board board) {
        System.out.println("-------------------");

        Collection<Move> userLegalMoves = board.getCurrentPlayer().getLegalMoves();

        for(Move move: userLegalMoves){
            System.out.print(move.toString() + " ");
        }

        ArrayList<Move> bestMoves = new ArrayList<>();
        Integer bestValue = DEFAULT_BEST_VALUE;
        for (Move move : userLegalMoves) {

            Move tryMove = Move.MoveFactory.createMove(board, move.getCurrentCoordinate(), move.getDestinationCoordinate());
            MoveTransition transition = board.getCurrentPlayer().makeMove(tryMove);

            if (transition.getMoveStatus().isDone()) {

                Integer value = calculateValue(move);
                if (bestValue < value) {
                    bestMoves.clear();
                    bestMoves.add(move);
                    bestValue = value;
                } else if (bestValue.equals(value)) {
                    bestMoves.add(move);
                    bestValue = value;
                }
            }
        }

        Move chosenMove  = getMove(bestMoves);

        return new Coordinate[]{chosenMove.getCurrentCoordinate(),chosenMove.getDestinationCoordinate()};
    }

    private Integer calculateValue(Move move) {
        if (move instanceof Move.KingSideCastleMove || move instanceof Move.QueenSideCastleMove) {
            return 50;
        } else if (move instanceof Move.MajorMove) {
            return 10;
        } else if(move instanceof Move.PawnMove || move instanceof Move.PawnJump){
            return 15;
        } else if (move instanceof Move.PawnPromotion) {
            return 70;
        } else if (move instanceof Move.AttackMove) {
            Move.AttackMove atkMove = (Move.AttackMove) move;
            return atkMove.getAttackedPiece().getPieceValue();
        } else {
            return 0;
        }
    }

    private Move getMove(List<Move> moves){
        Random random = new Random();
        Integer index = random.nextInt(moves.size());
        return moves.get(index);
    }
}
