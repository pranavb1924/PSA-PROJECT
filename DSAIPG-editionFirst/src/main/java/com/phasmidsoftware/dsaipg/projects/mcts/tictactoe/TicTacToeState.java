package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.tictactoe.TicTacToe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class TicTacToeState implements State<TicTacToe> {

    private final TicTacToe game;
    private final Position position;

    public TicTacToeState(Position position) {
        this.game = new TicTacToe();
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public TicTacToe game() {
        return game;
    }

    @Override
    public boolean isTerminal() {
        return position.full() || position.winner().isPresent();
    }

    @Override
    public int player() {
        int lastPlayer = position.lastPlayer();
        return (lastPlayer == TicTacToe.O || lastPlayer == TicTacToe.blank) ? TicTacToe.X : TicTacToe.O;
    }

    @Override
    public Optional<Integer> winner() {
        return position.winner();
    }

    @Override
    public Random random() {
        return game.getRandom();
    }

    @Override
    public Collection<Move<TicTacToe>> moves(int p) {
        Collection<int[]> coords = position.moves(p);
        Collection<Move<TicTacToe>> moves = new ArrayList<>();
        for (int[] rc : coords) {
            moves.add(new TicTacToe.TicTacToeMove(p, rc[0], rc[1])); 
        }
        return moves;
    }

    @Override
    public State<TicTacToe> next(Move<TicTacToe> move) {
        if (!(move instanceof TicTacToe.TicTacToeMove tMove))
            throw new RuntimeException("Expected TicTacToeMove");
        int[] rc = tMove.move();
        Position newPos = position.move(move.player(), rc[0], rc[1]);
        return new TicTacToeState(newPos);
    }
}
