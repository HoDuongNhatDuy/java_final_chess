package com.chess.gui;

import com.chess.board.Board;
import com.chess.board.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoduo on 6/18/2017.
 */
public class GameHistory {
    private List<GameState> stateHistory;

    public GameHistory() {
        this.stateHistory = new ArrayList<>();
        reset();
    }

    public void reset(){
        this.stateHistory.clear();
    }

    public void add(Board board, Move move){
        GameState newState = new GameState(board, move);
        this.stateHistory.add(newState);
    }

    public GameState getLastState(){
        if (this.stateHistory.size() <= 1)
            return null;
        return this.stateHistory.get(this.stateHistory.size() - 2);
    }

    public GameState getCurrentState(){
        if (this.stateHistory.size() < 1)
            return null;
        return this.stateHistory.get(this.stateHistory.size() - 1);
    }

    public void undo(){
        this.stateHistory.remove(this.stateHistory.size() - 1);
    }

    class GameState{
        private Board board;
        private Move move;

        public GameState(Board board, Move move) {
            this.board = board;
            this.move = move;
        }

        public Board getBoard() {
            return board;
        }

        public void setBoard(Board board) {
            this.board = board;
        }

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }
    }
}
