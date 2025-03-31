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
        game = new TicTacToe(42L);
    }

    @Test
    public void testSearchIterationsReturnsNonNull() {
        State<TicTacToe> initialState = game.start();
        Node<TicTacToe> rootNode = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> bestNode = mcts.searchIterations(100);
        assertNotNull(bestNode);
    }

    @Test
    public void testBestNodeHasPlayouts() {
        State<TicTacToe> initialState = game.start();
        Node<TicTacToe> rootNode = new TicTacToeNode(initialState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> bestNode = mcts.searchIterations(500);
        assertTrue(bestNode.playouts() > 0);
    }

    @Test
    public void testChildrenStatisticsUpdated() {
        State<TicTacToe> initialState = game.start();
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
        assertTrue(foundChildWithPlayouts);
    }

    @Test
    public void testTerminalState() {
        String board =
                "X O X\n" +
                "X O O\n" +
                "O X X";
        Position pos = Position.parsePosition(board, TicTacToe.blank);
        TicTacToe.TicTacToeState terminalState = game.new TicTacToeState(pos);
        assertTrue(terminalState.isTerminal());

        Node<TicTacToe> rootNode = new TicTacToeNode(terminalState);
        MCTS mcts = new MCTS(rootNode);

        Node<TicTacToe> resultNode = mcts.searchIterations(50);
        assertEquals((Object) terminalState, (Object) resultNode.state());
    }
}
