package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TicTacToeNode implements Node<TicTacToe> {

    private final State<TicTacToe> state;
    private final TicTacToeNode parent;
    private final Move<TicTacToe> moveFromParent;
    private final ArrayList<Node<TicTacToe>> children;
    private int wins;
    private int playouts;
    private double score;

    public TicTacToeNode(State<TicTacToe> state) {
        this(state, null, null);
    }

    public TicTacToeNode(State<TicTacToe> state, Move<TicTacToe> moveFromParent, TicTacToeNode parent) {
        this.state = state;
        this.moveFromParent = moveFromParent;
        this.parent = parent;
        this.children = new ArrayList<>();
        initializeNode();
    }
    
    /**
     * Returns the parent node
     */
    public TicTacToeNode getParent() {
        return parent;
    }
    
    /**
     * Returns the move that created this node
     */
    public Move<TicTacToe> getMoveFromParent() {
        return moveFromParent;
    }

    private void initializeNode() {
        if (isLeaf()) {
            playouts = 1;
            Optional<Integer> optWinner = state.winner();
            if (optWinner.isPresent()) {
                int winner = optWinner.get();
                if (winner == state.player()) {
                    wins = 0;  // Current player just lost
                } else {
                    wins = 2;  // Current player just won
                }
            } else {
                wins = 1;  // Draw
            }
            updateScore();
        } else {
            playouts = 0;
            wins = 0;
            score = 0.0;
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
    public void backPropagate() {
        if (isLeaf()) return;
        if (children.isEmpty()) return;
        int totalWins = 0;
        int totalPlay = 0;
        for (Node<TicTacToe> child : children) {
            totalWins += child.wins();
            totalPlay += child.playouts();
        }
        this.wins = totalWins;
        this.playouts = totalPlay;
        updateScore();
    }

    @Override
    public void addChild(State<TicTacToe> newState) {
        Move<TicTacToe> linkingMove = null;
        for (Move<TicTacToe> m : state.moves(state.player())) {
            if (state.next(m).equals(newState)) {
                linkingMove = m;
                break;
            }
        }
        TicTacToeNode child = new TicTacToeNode(newState, linkingMove, this);
        children.add(child);
    }
    
    /**
     * Adds a child with a known move
     */
    public void addChild(State<TicTacToe> newState, Move<TicTacToe> move) {
        TicTacToeNode child = new TicTacToeNode(newState, move, this);
        children.add(child);
    }

    @Override
    public int wins() {
        return wins;
    }

    @Override
    public int playouts() {
        return playouts;
    }
    
    /**
     * Updates the statistics of this node with a new reward.
     * @param reward The reward value (1.0 for win, 0.5 for draw, 0.0 for loss)
     */
    public void updateStats(double reward) {
        playouts += 1;
        wins += (reward == 1.0) ? 2 : (reward == 0.5) ? 1 : 0;
        updateScore();
    }

    private void updateScore() {
        score = playouts > 0 ? (double) wins / playouts : 0.0;
    }
    
    /**
     * Returns a string representation of this node for debugging
     */
    @Override
    public String toString() {
        return "TicTacToeNode[wins=" + wins + ", playouts=" + playouts + 
               ", score=" + score + ", isLeaf=" + isLeaf() + "]";
    }
}