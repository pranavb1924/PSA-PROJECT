package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.List;

public class MCTS {

    private final Node<TicTacToe> root;
    private final double explorationConstant = Math.sqrt(2);

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    /**
     * Runs a given number of iterations of the MCTS algorithm.
     * If the root state is terminal, it returns the root immediately.
     *
     * @param iterations the number of iterations.
     * @return the best child of the root node (or the root itself if terminal).
     */
    public Node<TicTacToe> searchIterations(int iterations) {
        // If the starting state is terminal, return the root immediately.
        if (root.state().isTerminal()) {
            return root;
        }
        for (int i = 0; i < iterations; i++) {
            // 1. Selection: traverse the tree using UCT
            List<Node<TicTacToe>> visited = new ArrayList<>();
            Node<TicTacToe> node = root;
            visited.add(node);
            while (!node.isLeaf() && !node.children().isEmpty()) {
                node = bestUCT(node);
                visited.add(node);
            }
            // 2. Expansion: if node is non-terminal and not yet expanded, expand it.
            if (!node.isLeaf() && node.children().isEmpty()) {
                node.explore();
                // Choose one child arbitrarily for simulation.
                if (!node.children().isEmpty()) {
                    node = node.children().iterator().next();
                    visited.add(node);
                }
            }
            // 3. Simulation: perform a random playout from the node.
            int simulationScore = simulate(node.state());
            // 4. Backpropagation: update stats for all nodes in the visited path.
            for (Node<TicTacToe> visitedNode : visited) {
                updateStats(visitedNode, simulationScore);
            }
        }
        return bestChild(root);
    }

    /**
     * Selects the best child using the UCT formula.
     */
    private Node<TicTacToe> bestUCT(Node<TicTacToe> node) {
        double bestValue = Double.NEGATIVE_INFINITY;
        Node<TicTacToe> bestChild = null;
        for (Node<TicTacToe> child : node.children()) {
            double winRate = ((double) child.wins()) / child.playouts();
            double explorationTerm = explorationConstant * Math.sqrt(Math.log(node.playouts()) / child.playouts());
            double uctValue = winRate + explorationTerm;
            if (uctValue > bestValue) {
                bestValue = uctValue;
                bestChild = child;
            }
        }
        return bestChild;
    }

    /**
     * Performs a simulation (rollout) from a given state until a terminal state is reached.
     * Returns a score: win is 2 points, draw is 1 point.
     */
    private int simulate(State<TicTacToe> state) {
        State<TicTacToe> simState = state;
        while (!simState.isTerminal()) {
            simState = simState.next(simState.chooseMove(simState.player()));
        }
        return simState.winner().isPresent() ? 2 : 1;
    }

    /**
     * Updates the statistics (wins and playouts) for a given node.
     */
    private void updateStats(Node<TicTacToe> node, int simulationScore) {
        if (node instanceof TicTacToeNode ttn) {
            ttn.incrementPlayouts();
            ttn.addWins(simulationScore);
        }
    }

    /**
     * Selects the best child from a node based on the highest number of playouts.
     */
    private Node<TicTacToe> bestChild(Node<TicTacToe> node) {
        Node<TicTacToe> best = null;
        int bestPlayouts = -1;
        for (Node<TicTacToe> child : node.children()) {
            if (child.playouts() > bestPlayouts) {
                bestPlayouts = child.playouts();
                best = child;
            }
        }
        return best;
    }
}
