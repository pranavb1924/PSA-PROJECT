package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class OthelloNodeTest {

    @Test
    public void testIsLeaf_NonTerminal() {
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode node = new OthelloNode(state);

        assertFalse(node.isLeaf());
    }

    @Test
    public void testIsLeaf_Terminal() {
        // Create a terminal state (full board)
        int[][] fullBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fullBoard[i][j] = (i + j) % 2 == 0 ? Othello.BLACK : Othello.WHITE;
            }
        }

        Position terminalPos = new Position(fullBoard, 64, Othello.WHITE);
        OthelloState terminalState = new OthelloState(terminalPos);
        OthelloNode terminalNode = new OthelloNode(terminalState);

        assertTrue(terminalNode.isLeaf());
    }

    @Test
    public void testAddChildWithMove() {
        // Create parent node
        Position parentPos = Position.createStartingPosition();
        OthelloState parentState = new OthelloState(parentPos);
        OthelloNode parentNode = new OthelloNode(parentState);

        // Create child state after a move
        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
        State<Othello> childState = parentState.next(move);

        // Add child using the OthelloNode-specific addChild with move
        parentNode.addChild(childState, move);

        // Verify child was added
        Collection<Node<Othello>> children = parentNode.children();
        assertEquals(1, children.size());

        OthelloNode childNode = (OthelloNode) children.iterator().next();
        assertEquals(childState, childNode.state());
        assertEquals(move, childNode.getMoveFromParent());
    }


    @Test
    public void testUpdateStatsWin() {
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode node = new OthelloNode(state);

        // Initial stats
        assertEquals(0, node.playouts());
        assertEquals(0, node.wins());

        // Update with a win
        node.updateStats(1.0);

        // Check updated stats
        assertEquals(1, node.playouts());
        assertEquals(2, node.wins()); // 2 points for a win
    }

    @Test
    public void testUpdateStatsDraw() {
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode node = new OthelloNode(state);

        // Update with a draw
        node.updateStats(0.5);

        // Check updated stats
        assertEquals(1, node.playouts());
        assertEquals(1, node.wins()); // 1 point for a draw
    }

    @Test
    public void testUpdateStatsLoss() {
        Position position = Position.createStartingPosition();
        OthelloState state = new OthelloState(position);
        OthelloNode node = new OthelloNode(state);

        // Update with a loss
        node.updateStats(0.0);

        // Check updated stats
        assertEquals(1, node.playouts());
        assertEquals(0, node.wins()); // 0 points for a loss
    }

}