package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class OthelloStateTest {

    @Test
    public void testInitialState() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        assertEquals(Othello.BLACK, state.player());
        assertFalse(state.isTerminal());
        assertTrue(state.winner().isEmpty());
    }

    @Test
    public void testPlayer_InitialBoard() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        assertEquals(Othello.BLACK, state.player());
    }

    @Test
    public void testPlayer_AfterBlackMove() {
        // Create a position where Black just moved
        Position position = Position.createStartingPosition().move(Othello.BLACK, 2, 3);
        OthelloState state = new OthelloState(position);
        assertEquals(Othello.WHITE, state.player());
    }

    @Test
    public void testPlayer_AfterWhiteMove() {
        // Create a position where Black moved first, then White moved
        Position position = Position.createStartingPosition()
                .move(Othello.BLACK, 2, 3)
                .move(Othello.WHITE, 2, 2);
        OthelloState state = new OthelloState(position);
        assertEquals(Othello.BLACK, state.player());
    }

    @Test
    public void testNext_ValidMove() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 2, 3);
        State<Othello> nextState = state.next(move);

        assertTrue(nextState instanceof OthelloState);
        OthelloState othelloState = (OthelloState) nextState;

        // Check that the next player is White
        assertEquals(Othello.WHITE, othelloState.player());

        // Check that the board has been updated properly
        Position nextPosition = othelloState.getPosition();
        assertEquals(Othello.BLACK, nextPosition.getBoard()[2][3]);
        assertEquals(Othello.BLACK, nextPosition.getBoard()[3][3]); // Flipped piece
    }

    @Test(expected = IllegalStateException.class)
    public void testNext_InvalidMove() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        // Try to place in the middle of the board where no flips happen
        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 0, 0);
        state.next(move);
    }

    @Test
    public void testIsTerminal_InitialBoard() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        assertFalse(state.isTerminal());
    }

    @Test
    public void testIsTerminal_FullBoard() {
        // Create a full board manually with a clear winner (not 50/50 split)
        int[][] fullBoard = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Give BLACK 5 rows instead of 4 to create a clear winner
                fullBoard[i][j] = (i < 5) ? Othello.BLACK : Othello.WHITE;
            }
        }

        Position position = new Position(fullBoard, 64, Othello.WHITE);
        OthelloState state = new OthelloState(position);
        assertTrue(state.isTerminal());

        // Check winner
        Optional<Integer> winner = state.winner();
        assertTrue(winner.isPresent());
        assertEquals(Integer.valueOf(Othello.BLACK), winner.get());
    }
}