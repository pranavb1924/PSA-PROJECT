package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import org.junit.Test;
import java.util.Arrays;

import static org.junit.Assert.*;

public class MCTSTest {

    /**
     * Test 1: Create and initialize MCTS with empty board
     * This verifies basic construction and state representation
     */
    @Test
    public void testInitialization() {
        // Create empty board
        int[][] emptyGrid = {
                {Position.blank, Position.blank, Position.blank},
                {Position.blank, Position.blank, Position.blank},
                {Position.blank, Position.blank, Position.blank}
        };
        Position position = new Position(emptyGrid, 0, Position.blank);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        // Create MCTS instance
        MCTS mcts = new MCTS(node);

        // Verify that the root node has no children initially
        assertTrue("Initial node should have no children", node.children().isEmpty());

        // Verify player at root
        assertEquals("First player should be X (1)", TicTacToe.X, node.state().player());

        // Run a single iteration to verify basic functionality
        Node<TicTacToe> resultNode = mcts.searchIterations(1);

        // Verify that we got a result (doesn't matter which move)
        assertNotNull("Search should return a node", resultNode);
    }

    /**
     * Test 2: Test direct threat detection for winning moves
     * Tests a simple case where there's an immediate winning move
     */
    @Test
    public void testDirectWinningMove() {
        // Create a board where player X has two in a row
        int[][] grid = {
                {Position.blank, 1, 1},
                {Position.blank, Position.blank, Position.blank},
                {Position.blank, Position.blank, Position.blank}
        };
        Position position = new Position(grid, 2, 0); // 2 moves made, last was O
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        MCTS mcts = new MCTS(node);
        Node<TicTacToe> bestNode = mcts.searchIterations(1);

        // Get the resulting position
        TicTacToeState resultState = (TicTacToeState) bestNode.state();
        Position resultPosition = resultState.getPosition();

        // The move should have been to (0,0) to complete the row
        assertEquals("X should play at top-left to win",
                1, resultPosition.projectRow(0)[0]);
    }

    /**
     * Test 3: Test blocking of opponent's winning move
     * Tests defensive play detection
     */
    @Test
    public void testDirectBlockingMove() {
        // Create a board where player O has two in a row
        int[][] grid = {
                {Position.blank, Position.blank, Position.blank},
                {0, 0, Position.blank},
                {Position.blank, Position.blank, Position.blank}
        };
        Position position = new Position(grid, 2, 1); // 2 moves made, last was X
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        MCTS mcts = new MCTS(node);
        Node<TicTacToe> bestNode = mcts.searchIterations(1);

        // Get the resulting position
        TicTacToeState resultState = (TicTacToeState) bestNode.state();
        Position resultPosition = resultState.getPosition();

        // The move should have been to (1,2) to block O's win
        assertEquals("O should play at middle-right to block X's win",
                0, resultPosition.projectRow(1)[2]);
    }

    /**
     * Test 4: Test tree expansion with multiple iterations
     * This verifies that the MCTS algorithm properly explores the game tree
     */
    @Test
    public void testTreeExpansion() {
        // Create a simple board with some moves already made
        int[][] grid = {
                {1, Position.blank, Position.blank},
                {Position.blank, 0, Position.blank},
                {Position.blank, Position.blank, Position.blank}
        };
        Position position = new Position(grid, 2, 0); // 2 moves made, last was O
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        MCTS mcts = new MCTS(node);

        // Run MCTS with enough iterations to expand tree significantly
        mcts.searchIterations(100);

        // Verify that the root node has children now
        assertFalse("Root node should have children after search",
                node.children().isEmpty());

        // Verify that root has appropriate number of children (7 empty spaces)
        assertEquals("Root should have 7 children for 7 possible moves",
                7, node.children().size());

        // Verify all children have at least some playouts
        boolean anyPlayoutsFound = false;
        for (Node<TicTacToe> child : node.children()) {
            if (((TicTacToeNode)child).playouts() > 0) {
                anyPlayoutsFound = true;
                break;
            }
        }
        assertTrue("At least one child should have playouts", anyPlayoutsFound);
    }

    /**
     * Test 5: Test full game simulation
     * Verifies that MCTS can play a complete game
     */
    @Test
    public void testFullGameSimulation() {
        // Start with an empty board
        int[][] emptyGrid = {
                {Position.blank, Position.blank, Position.blank},
                {Position.blank, Position.blank, Position.blank},
                {Position.blank, Position.blank, Position.blank}
        };
        Position position = new Position(emptyGrid, 0, Position.blank);
        TicTacToeState state = new TicTacToeState(position);

        // Play moves until game is terminal
        int moveCount = 0;
        while (!state.isTerminal() && moveCount < 9) {
            TicTacToeNode node = new TicTacToeNode(state);
            MCTS mcts = new MCTS(node);
            Node<TicTacToe> bestNode = mcts.searchIterations(100);

            // Update state
            state = (TicTacToeState) bestNode.state();
            moveCount++;

            // Debug output
            System.out.println("Move " + moveCount + ":");
            System.out.println(state.getPosition().render());
        }

        // Game should have reached terminal state
        assertTrue("Game should reach terminal state", state.isTerminal());

        // Game should not take more than 9 moves (maximum for tic-tac-toe)
        assertTrue("Game should take 9 or fewer moves", moveCount <= 9);
    }
}