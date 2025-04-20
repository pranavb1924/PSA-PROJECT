package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import static org.junit.Assert.*;

public class OthelloTest {

//    @Test
//    public void testConstructor() {
//        Othello othello = new Othello();
//        assertNotNull(othello.getRandom());
//    }
//
//    @Test
//    public void testConstructorWithSeed() {
//        long seed = 12345L;
//        Othello othello = new Othello(seed);
//
//        // Create a random with the same seed
//        Random expectedRandom = new Random(seed);
//
//        // Both randoms should generate the same sequence
//        for (int i = 0; i < 10; i++) {
//            assertEquals(expectedRandom.nextInt(), othello.getRandom().nextInt());
//        }
//    }
//
//    @Test
//    public void testOpener() {
//        Othello othello = new Othello();
//        assertEquals(Othello.BLACK, othello.opener());
//    }
//
//    @Test
//    public void testStart() {
//        Othello othello = new Othello();
//        State<Othello> state = othello.start();
//
//        assertNotNull(state);
//        assertTrue(state instanceof OthelloState);
//
//        OthelloState othelloState = (OthelloState) state;
//        Position position = othelloState.getPosition();
//
//        // Check starting position has correct pieces
//        int[][] board = position.getBoard();
//        assertEquals(Othello.WHITE, board[3][3]);
//        assertEquals(Othello.BLACK, board[3][4]);
//        assertEquals(Othello.BLACK, board[4][3]);
//        assertEquals(Othello.WHITE, board[4][4]);
//    }
//
//    @Test
//    public void testOthelloMove() {
//        // Create an OthelloMove
//        Othello.OthelloMove move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
//
//        // Check player
//        assertEquals(Othello.BLACK, move.player());
//
//        // Check move coordinates
//        int[] coords = move.move();
//        assertEquals(2, coords[0]);
//        assertEquals(3, coords[1]);
//
//        // Check toString
//        String moveString = move.toString();
//        assertTrue(moveString.contains("Black"));
//        assertTrue(moveString.contains("2,3"));
//    }

    @Test
    public void testCompleteGame() {
        // Create a game with a fixed seed for deterministic testing
        Othello othello = new Othello(42L);

        // Initial state
        State<Othello> state = othello.start();
        assertFalse(state.isTerminal());

        // Play a few moves
        int currentPlayer = othello.opener();

        // Make 10 moves or until game is over
        for (int i = 0; i < 10 && !state.isTerminal(); i++) {
            // Choose a move
            Move<Othello> move = state.chooseMove(currentPlayer);

            // Apply the move
            State<Othello> nextState = state.next(move);

            // Verify the move was applied
            OthelloState otState = (OthelloState) state;
            OthelloState nextOtState = (OthelloState) nextState;

            // Verify the player changed
            assertNotEquals(otState.player(), nextOtState.player());

            // Verify the board changed
            assertNotEquals(otState.getPosition(), nextOtState.getPosition());

            // Update for next iteration
            state = nextState;
            currentPlayer = 1 - currentPlayer;
        }
    }
}