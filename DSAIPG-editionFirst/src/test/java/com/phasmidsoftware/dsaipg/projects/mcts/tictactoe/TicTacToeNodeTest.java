package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        // no tests yet
    }

    @Test
    public void addChild() {
        // no tests yet
    }

    @Test
    public void backPropagate() {
        // no tests yet
    }
    
    // Add some additional tests for the new implementation
    
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