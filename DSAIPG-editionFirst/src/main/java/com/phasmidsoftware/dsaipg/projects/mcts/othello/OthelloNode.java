package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class OthelloNode implements Node<Othello> {

    private final State<Othello> state;
    private final OthelloNode parent;
    private final Move<Othello> moveFromParent;
    private final ArrayList<Node<Othello>> children;
    private int wins;
    private int playouts;
    private double score;

    public OthelloNode(State<Othello> state) {
        this(state, null, null);
    }

    public OthelloNode(State<Othello> state, Move<Othello> moveFromParent, OthelloNode parent) {
        this.state = state;
        this.moveFromParent = moveFromParent;
        this.parent = parent;
        this.children = new ArrayList<>();
        initializeNode();
    }
    
    /**
     * Returns the parent node
     */
    public OthelloNode getParent() {
        return parent;
    }
    
    /**
     * Returns the move that created this node
     */
    public Move<Othello> getMoveFromParent() {
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
    public State<Othello> state() {
        return state;
    }

    @Override
    public boolean white() {
        return state.player() == state.game().opener();
    }

    @Override
    public Collection<Node<Othello>> children() {
        return children;
    }

    @Override
    public void backPropagate() {
        if (isLeaf()) return;
        if (children.isEmpty()) return;
        int totalWins = 0;
        int totalPlay = 0;
        for (Node<Othello> child : children) {
            totalWins += child.wins();
            totalPlay += child.playouts();
        }
        this.wins = totalWins;
        this.playouts = totalPlay;
        updateScore();
    }

    @Override
    public void addChild(State<Othello> newState) {
        Move<Othello> linkingMove = null;
        for (Move<Othello> m : state.moves(state.player())) {
            if (state.next(m).equals(newState)) {
                linkingMove = m;
                break;
            }
        }
        OthelloNode child = new OthelloNode(newState, linkingMove, this);
        children.add(child);
    }
    
    /**
     * Add a child with a known move
     */
    public void addChild(State<Othello> newState, Move<Othello> move) {
        OthelloNode child = new OthelloNode(newState, move, this);
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
     * Update node statistics with a new reward
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
     * Returns a string representation for debugging
     */
    @Override
    public String toString() {
        return "OthelloNode[wins=" + wins + 
               ", playouts=" + playouts + 
               ", score=" + score + 
               ", isLeaf=" + isLeaf() + "]";
    }
}