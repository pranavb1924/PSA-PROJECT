package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class OthelloNodeTest {

//    @Test
//    public void testConstructor() {
//        Position position = Position.createStartingPosition();
//        OthelloState state = new OthelloState(position);
//        OthelloNode node = new OthelloNode(state);
//
//        assertEquals(state, node.state());
//        assertNull(node.getParent());
//        assertNull(node.getMoveFromParent());
//        assertTrue(node.children().isEmpty());
//        assertFalse(node.isLeaf());
//    }

//    @Test
//    public void testConstructorWithParent() {
//        // Create parent node
//        Position parentPos = Position.createStartingPosition();
//        OthelloState parentState = new OthelloState(parentPos);
//        OthelloNode parentNode = new OthelloNode(parentState);
//
//        // Create child state and move
//        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
//        State<Othello> childState = parentState.next(move);
//
//        // Create child node with parent reference
//        OthelloNode childNode = new OthelloNode(childState, move, parentNode);
//
//        assertEquals(childState, childNode.state());
//        assertEquals(parentNode, childNode.getParent());
//        assertEquals(move, childNode.getMoveFromParent());
//        assertTrue(childNode.children().isEmpty());
//    }

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

//    @Test
//    public void testWhite() {
//        Position position = Position.createStartingPosition();
//        OthelloState state = new OthelloState(position);
//        OthelloNode node = new OthelloNode(state);
//
//        // In Othello, BLACK is the opener, and white() should return true if
//        // the player is the opener
//        assertTrue(node.white());
//
//        // Create a state where it's WHITE's turn
//        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
//        State<Othello> nextState = state.next(move);
//        OthelloNode nextNode = new OthelloNode(nextState);
//
//        // For WHITE's turn, white() should return false
//        assertFalse(nextNode.white());
//    }

//    @Test
//    public void testAddChild() {
//        // Create parent node
//        Position parentPos = Position.createStartingPosition();
//        OthelloState parentState = new OthelloState(parentPos);
//        OthelloNode parentNode = new OthelloNode(parentState);
//
//        // Create child state after a move
//        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
//        State<Othello> childState = parentState.next(move);
//
//        // Add child using the generic addChild method
//        parentNode.addChild(childState);
//
//        // Verify child was added
//        Collection<Node<Othello>> children = parentNode.children();
//        assertEquals(1, children.size());
//
//        Node<Othello> childNode = children.iterator().next();
//        assertEquals(childState, childNode.state());
//    }

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

//    @Test
//    public void testBackPropagate() {
//        // Create parent node
//        Position parentPos = Position.createStartingPosition();
//        OthelloState parentState = new OthelloState(parentPos);
//        OthelloNode parentNode = new OthelloNode(parentState);
//
//        // Create and add two children
//        Move<Othello> move1 = new Othello.OthelloMove(Othello.BLACK, 2, 3);
//        State<Othello> childState1 = parentState.next(move1);
//        OthelloNode childNode1 = new OthelloNode(childState1, move1, parentNode);
//
//        Move<Othello> move2 = new Othello.OthelloMove(Othello.BLACK, 3, 2);
//        State<Othello> childState2 = parentState.next(move2);
//        OthelloNode childNode2 = new OthelloNode(childState2, move2, parentNode);
//
//        // Add children to parent
//        parentNode.children().add(childNode1);
//        parentNode.children().add(childNode2);
//
//        // Update stats for children
//        childNode1.updateStats(1.0); // Win
//        childNode1.updateStats(0.5); // Draw
//        childNode2.updateStats(0.0); // Loss
//        childNode2.updateStats(1.0); // Win
//
//        // Back propagate from parent
//        parentNode.backPropagate();
//
//        // Check that parent stats are the sum of children's stats
//        assertEquals(4, parentNode.playouts()); // 2 + 2
//        assertEquals(5, parentNode.wins()); // 3 + 2
//    }

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

//    @Test
//    public void testInitializeNode_NonTerminal() {
//        Position position = Position.createStartingPosition();
//        OthelloState state = new OthelloState(position);
//        OthelloNode node = new OthelloNode(state);
//
//        // For a non-terminal node, initial stats should be 0
//        assertEquals(0, node.playouts());
//        assertEquals(0, node.wins());
//    }
//
//    @Test
//    public void testInitializeNode_TerminalWin() {
//        // Create a terminal state where BLACK wins
//        int[][] blackWinsBoard = new int[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                blackWinsBoard[i][j] = (i < 5) ? Othello.BLACK : Othello.WHITE;
//            }
//        }
//
//        // Set last player to BLACK, so it's WHITE's turn
//        Position terminalPos = new Position(blackWinsBoard, 64, Othello.BLACK);
//        OthelloState terminalState = new OthelloState(terminalPos);
//        OthelloNode terminalNode = new OthelloNode(terminalState);
//
//        // For a terminal node, initializes with correct stats
//        assertEquals(1, terminalNode.playouts());
//        assertEquals(2, terminalNode.wins()); // 2 points for a win
//    }
//
//    @Test
//    public void testToString() {
//        Position position = Position.createStartingPosition();
//        OthelloState state = new OthelloState(position);
//        OthelloNode node = new OthelloNode(state);
//
//        String nodeString = node.toString();
//        assertTrue(nodeString.contains("wins=0"));
//        assertTrue(nodeString.contains("playouts=0"));
//        assertTrue(nodeString.contains("isLeaf=false"));
//    }
}