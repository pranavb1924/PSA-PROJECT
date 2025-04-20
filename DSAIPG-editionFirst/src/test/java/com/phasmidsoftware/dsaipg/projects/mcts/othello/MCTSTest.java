package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import org.junit.Test;

import static org.junit.Assert.*;

public class MCTSTest {

    @Test
    public void testSearchIterations() {
        // Create initial state
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode root = new OthelloNode(state);

        // Create MCTS
        MCTS mcts = new MCTS(root);

        // Run some iterations
        Node<Othello> bestNode = mcts.searchIterations(100);

        // Verify we got a result
        assertNotNull(bestNode);

        // Should not be the same as root (unless there are no valid moves)
        if (!root.state().moves(root.state().player()).isEmpty()) {
            assertNotEquals(root, bestNode);
        }

        // Best node should have a non-zero number of playouts
        assertTrue(bestNode.playouts() > 0);
    }

    @Test
    public void testSimulation() {
        // Create initial state
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode root = new OthelloNode(state);

        // Create MCTS
        MCTS mcts = new MCTS(root);

        // Run multiple simulations to ensure they complete
        for (int i = 0; i < 5; i++) {
            // Run some iterations
            Node<Othello> result = mcts.searchIterations(10);

            // Verify result
            assertNotNull(result);

            // If there are valid moves, result should not be root
            if (!root.state().moves(root.state().player()).isEmpty()) {
                assertNotEquals(root, result);
            }
        }
    }
}