package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class TicTacToeNodeTest {

    @Test
    public void winsAndPlayouts() {
        // Create a terminal state with X winning (X has 3 in a column)
        Position position = Position.parsePosition("X . 0\nX O .\nX . 0", TicTacToe.X);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.isLeaf());
        assertEquals(2, node.wins());
        assertEquals(1, node.playouts());
    }

    @Test
    public void state() {
        // Create a blank board state
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);
        assertEquals(state, node.state());
    }

    @Test
    public void white() {
        // Create a blank board state, X should be first to move
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);
        assertTrue(node.white());  // X is the "white" player (first to move)
    }

    @Test
    public void children() {
        // Create an empty board with no last player
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        // Initially, node should have no children
        assertTrue("New node should have no children", node.children().isEmpty());

        // Add a child and verify children() returns the correct collection
        Move<TicTacToe> move = new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 0);
        State<TicTacToe> childState = state.next(move);
        node.addChild(childState, move);

        Collection<Node<TicTacToe>> children = node.children();
        assertEquals("Node should have exactly one child", 1, children.size());

        // Add another child and verify children() still works
        Move<TicTacToe> move2 = new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 1);
        State<TicTacToe> childState2 = state.next(move2);
        node.addChild(childState2, move2);

        children = node.children();
        assertEquals("Node should have exactly two children", 2, children.size());
    }

    @Test
    public void addChild() {
        // Create an empty board with no last player
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);

        // Test addChild with explicit move
        Move<TicTacToe> move = new TicTacToe.TicTacToeMove(TicTacToe.X, 1, 1);
        State<TicTacToe> childState = state.next(move);
        node.addChild(childState, move);

        // Verify the child was added
        Collection<Node<TicTacToe>> children = node.children();
        assertEquals(1, children.size());
        Node<TicTacToe> child = children.iterator().next();

        // Verify child's state has X at position 1,1
        Position childPosition = ((TicTacToeState) child.state()).getPosition();
        String expected = ". . .\n. X .\n. . .";
        assertEquals(expected, childPosition.render().trim());

        // Test addChild without explicit move
        // Create a move at 0,0
        // Since Position implements checking for same player moves,
        // we need to create a new position with O as last player to make a valid X move
        Position positionWithO = Position.parsePosition(". . .\n. . .\n. . .", TicTacToe.O);
        TicTacToeState stateWithO = new TicTacToeState(positionWithO);
        TicTacToeNode nodeWithO = new TicTacToeNode(stateWithO);

        Move<TicTacToe> move2 = new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 0);
        State<TicTacToe> childState2 = stateWithO.next(move2);
        nodeWithO.addChild(childState2);

        // Verify the child was added
        assertEquals(1, nodeWithO.children().size());
        Node<TicTacToe> addedChild = nodeWithO.children().iterator().next();

        // Verify child has the correct board position
        Position childPos = ((TicTacToeState) addedChild.state()).getPosition();
        String boardStr = childPos.render().trim();
        assertEquals("X . .\n. . .\n. . .", boardStr);
    }

    @Test
    public void testBackpropagate_SingleLevel() {
        // Create a simple TicTacToe state
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);

        // Create a root node
        TicTacToeNode root = new TicTacToeNode(state);

        // Create MCTS instance
        MCTS mcts = new MCTS(root);

        // Directly call backpropagate with a win result (1.0)
        // Using reflection to access private method
        java.lang.reflect.Method backpropMethod;
        try {
            backpropMethod = MCTS.class.getDeclaredMethod("backpropagate", TicTacToeNode.class, double.class);
            backpropMethod.setAccessible(true);
            backpropMethod.invoke(mcts, root, 1.0);

            // Root should now have 1 playout and 2 wins (representing a win)
            assertEquals(1, root.playouts());
            assertEquals(2, root.wins());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    public void testBackpropagate_MultiLevel() {
        // Create a simple TicTacToe state
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);

        // Create root node
        TicTacToeNode root = new TicTacToeNode(state);

        // Create a child node manually (player X places at 0,0)
        Move<TicTacToe> move = new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 0);
        State<TicTacToe> childState = state.next(move);
        TicTacToeNode child = new TicTacToeNode(childState, move, root);

        // Create a grandchild node (player O places at 1,1)
        Move<TicTacToe> move2 = new TicTacToe.TicTacToeMove(TicTacToe.O, 1, 1);
        State<TicTacToe> grandchildState = childState.next(move2);
        TicTacToeNode grandchild = new TicTacToeNode(grandchildState, move2, child);

        // Add children to their parents
        root.addChild(childState, move);
        child.addChild(grandchildState, move2);

        // Create MCTS instance
        MCTS mcts = new MCTS(root);

        // Backpropagate a win (1.0) from the grandchild
        java.lang.reflect.Method backpropMethod;
        try {
            backpropMethod = MCTS.class.getDeclaredMethod("backpropagate", TicTacToeNode.class, double.class);
            backpropMethod.setAccessible(true);
            backpropMethod.invoke(mcts, grandchild, 1.0);

            // Verify state of nodes after backpropagation
            // Grandchild: 1 playout, 2 wins (win)
            assertEquals(1, grandchild.playouts());
            assertEquals(2, grandchild.wins());

            // Child: 1 playout, 0 wins (loss, because reward was flipped)
            assertEquals(1, child.playouts());
            assertEquals(0, child.wins());

            // Root: 1 playout, 2 wins (win, because reward was flipped again)
            assertEquals(1, root.playouts());
            assertEquals(2, root.wins());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    public void testBackpropagate_Draw() {
        // Create a simple TicTacToe state
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);

        // Create a root node
        TicTacToeNode root = new TicTacToeNode(state);

        // Create MCTS instance
        MCTS mcts = new MCTS(root);

        // Call backpropagate with draw result (0.5)
        java.lang.reflect.Method backpropMethod;
        try {
            backpropMethod = MCTS.class.getDeclaredMethod("backpropagate", TicTacToeNode.class, double.class);
            backpropMethod.setAccessible(true);
            backpropMethod.invoke(mcts, root, 0.5);

            // Root should now have 1 playout and 1 win (representing a draw)
            assertEquals(1, root.playouts());
            assertEquals(1, root.wins());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    public void testBackpropagate_UpdateStatsConsistency() {
        // This test verifies the consistency between backpropagate and updateStats

        // Create a simple TicTacToe state
        Position position = Position.parsePosition(". . .\n. . .\n. . .", Position.blank);
        TicTacToeState state = new TicTacToeState(position);

        // Create two identical nodes for comparison
        TicTacToeNode node1 = new TicTacToeNode(state);
        TicTacToeNode node2 = new TicTacToeNode(state);

        // Update node1 directly
        node1.updateStats(1.0); // Win

        // Update node2 via backpropagate
        MCTS mcts = new MCTS(node2);
        java.lang.reflect.Method backpropMethod;
        try {
            backpropMethod = MCTS.class.getDeclaredMethod("backpropagate", TicTacToeNode.class, double.class);
            backpropMethod.setAccessible(true);
            backpropMethod.invoke(mcts, node2, 1.0);

            // Both nodes should have identical stats
            assertEquals(node1.playouts(), node2.playouts());
            assertEquals(node1.wins(), node2.wins());
        } catch (Exception e) {
            fail("Exception during reflection: " + e.getMessage());
        }
    }

    @Test
    public void testLeafNodeEvaluation() {
        // Test X wins
        Position posXWins = Position.parsePosition("X X X\n. O .\nO . .", TicTacToe.X);
        TicTacToeState stateXWins = new TicTacToeState(posXWins);
        TicTacToeNode nodeXWins = new TicTacToeNode(stateXWins);
        assertTrue(nodeXWins.isLeaf());
        assertEquals(2, nodeXWins.wins());  // X won, so the node gets 2 wins
        
        // Test O wins
        Position posOWins = Position.parsePosition("X . X\nO O O\nX . .", TicTacToe.O);
        TicTacToeState stateOWins = new TicTacToeState(posOWins);
        TicTacToeNode nodeOWins = new TicTacToeNode(stateOWins);
        assertTrue(nodeOWins.isLeaf());
        assertEquals(2, nodeOWins.wins());  // O won, so the node gets 2 wins
        
        // Test draw
        Position posDraw = Position.parsePosition("X O X\nO X O\nO X O", TicTacToe.O);
        TicTacToeState stateDraw = new TicTacToeState(posDraw);
        TicTacToeNode nodeDraw = new TicTacToeNode(stateDraw);
        assertTrue(nodeDraw.isLeaf());
        assertEquals(1, nodeDraw.wins());  // Draw, so the node gets 1 win
    }
    
    @Test
    public void testUpdateStats() {
        // Create a non-terminal state
        Position position = Position.parsePosition(". . .\n. X .\n. . .", TicTacToe.X);
        TicTacToeState state = new TicTacToeState(position);
        TicTacToeNode node = new TicTacToeNode(state);
        
        // Initial values
        assertEquals(0, node.wins());
        assertEquals(0, node.playouts());
        
        // Update with a win
        node.updateStats(1.0);
        assertEquals(2, node.wins());
        assertEquals(1, node.playouts());
        
        // Update with a draw
        node.updateStats(0.5);
        assertEquals(3, node.wins());  // 2 + 1 for the draw
        assertEquals(2, node.playouts());
        
        // Update with a loss
        node.updateStats(0.0);
        assertEquals(3, node.wins());  // No change in wins for a loss
        assertEquals(3, node.playouts());
    }
}