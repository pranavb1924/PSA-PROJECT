package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MCTSTest {

    private TicTacToe game;

    @Before
    public void setUp() {
        // Create a new game with a fixed seed for reproducibility.
        game = new TicTacToe(42L);
    }

    /**
     * Test that running MCTS.searchIterations on an initial (non-terminal) state
     * returns a non-null best node.
     */
    @Test
    public void testSearchIterationsReturnsNonNull() {
        State<TicTacToe> initialState = game.start();
        Node<TicTacToe> rootNode = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> bestNode = mcts.searchIterations(100);
        assertNotNull("MCTS should return a non-null best node.", bestNode);
    }

    /**
     * Test that the best node returned by MCTS.searchIterations has nonzero playouts.
     */
    @Test
    public void testBestNodeHasPlayouts() {
        State<TicTacToe> initialState = game.start();
        Node<TicTacToe> rootNode = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> bestNode = mcts.searchIterations(500);
        assertTrue("The best node should have been visited (playouts > 0).", bestNode.playouts() > 0);
    }

    /**
     * Test that after MCTS iterations at least one child of the root node has updated statistics.
     */
    @Test
    public void testChildrenStatisticsUpdated() {
        State<TicTacToe> initialState = game.start();
        // Use TicTacToeNode to access children.
        TicTacToeNode rootNode = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(rootNode);

        mcts.searchIterations(200);

        boolean foundChildWithPlayouts = false;
        for (Node<TicTacToe> child : rootNode.children()) {
            if (child.playouts() > 0) {
                foundChildWithPlayouts = true;
                break;
            }
        }
        assertTrue("At least one child of the root node should have nonzero playouts.", foundChildWithPlayouts);
    }

    /**
     * Test that when the starting state is terminal,
     * MCTS.searchIterations returns the same terminal state.
     */
    @Test
    public void testTerminalState() {
        // Create a board that is terminal (e.g., a full board draw).
        String board =
                "X O X\n" +
                "X O O\n" +
                "O X X"; // Fully filled board (draw).
        Position pos = Position.parsePosition(board, TicTacToe.blank);
        TicTacToe.TicTacToeState terminalState = game.new TicTacToeState(pos);
        assertTrue("The board should be terminal.", terminalState.isTerminal());

        Node<TicTacToe> rootNode = new TicTacToeNode(terminalState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> resultNode = mcts.searchIterations(50);
        // Since the state is terminal, MCTS should return the same state.
        assertEquals("For a terminal state, MCTS should return the same state.", 
                     (Object) terminalState, (Object) resultNode.state());
    }
}
