package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TicTacToeNode implements Node<TicTacToe> {

    private final State<TicTacToe> state;
    private final ArrayList<Node<TicTacToe>> children;
    private int wins;
    private int playouts;

    public TicTacToeNode(State<TicTacToe> state) {
        this.state = state;
        this.children = new ArrayList<>();
        initializeNodeData();
    }

    private void initializeNodeData() {
        if (isLeaf()) {
            playouts = 1;
            Optional<Integer> winner = state.winner();
            if (winner.isPresent())
                wins = 2; // win is 2 points
            else
                wins = 1; // draw is 1 point
        }
    }

    @Override
    public boolean isLeaf() {
        return state.isTerminal();
    }

    @Override
    public State<TicTacToe> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<TicTacToe>> children() {
        return children;
    }

    @Override
    public void addChild(State<TicTacToe> state) {
        children.add(new TicTacToeNode(state));
    }

    @Override
    public void backPropagate() {
        playouts = 0;
        wins = 0;
        for (Node<TicTacToe> child : children) {
            wins += child.wins();
            playouts += child.playouts();
        }
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }

    // --- Helper methods for MCTS backpropagation ---
    public void incrementPlayouts() {
        playouts++;
    }

    public void addWins(int score) {
        wins += score;
    }
}
