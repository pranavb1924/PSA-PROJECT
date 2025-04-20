package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import static org.junit.Assert.*;

public class OthelloTest {

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