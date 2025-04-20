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

//    @Test
//    public void testGetPosition() {
//        Position position = Position.createStartingPosition();
//        OthelloState state = new OthelloState(position);
//        assertEquals(position, state.getPosition());
//    }
//
//    @Test
//    public void testGame() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//        assertNotNull(state.game());
//        assertTrue(state.game() instanceof Othello);
//    }

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

//    @Test
//    public void testRandom() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//        assertNotNull(state.random());
//    }
//
//    @Test
//    public void testMoves_InitialPosition() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//        Collection<Move<Othello>> moves = state.moves(Othello.BLACK);
//        assertEquals(4, moves.size());
//
//        // Verify that the moves are valid
//        for (Move<Othello> move : moves) {
//            assertEquals(Othello.BLACK, move.player());
//            assertTrue(move instanceof Othello.OthelloMove);
//        }
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testMoves_WrongPlayer() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//        // It's Black's turn, but we're asking for White's moves
//        state.moves(Othello.WHITE);
//    }

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

//    @Test(expected = IllegalStateException.class)
//    public void testNext_WrongPlayer() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//        // It's Black's turn, but we're trying to make a move as White
//        Move<Othello> move = new Othello.OthelloMove(Othello.WHITE, 2, 3);
//        state.next(move);
//    }

    @Test(expected = IllegalStateException.class)
    public void testNext_InvalidMove() {
        OthelloState state = new OthelloState(Position.createStartingPosition());
        // Try to place in the middle of the board where no flips happen
        Move<Othello> move = new Othello.OthelloMove(Othello.BLACK, 0, 0);
        state.next(move);
    }




//    @Test(expected = IllegalArgumentException.class)
//    public void testNext_InvalidMoveType() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//
//        // Create a Move that's not an OthelloMove
//        Move<Othello> invalidMove = new Move<Othello>() {
//            @Override
//            public int player() {
//                return Othello.BLACK;
//            }
//        };
//
//        state.next(invalidMove);
//    }

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

//    @Test
//    public void testChooseMove() {
//        OthelloState state = new OthelloState(Position.createStartingPosition());
//
//        Move<Othello> move = state.chooseMove(Othello.BLACK);
//        assertNotNull(move);
//        assertEquals(Othello.BLACK, move.player());
//        assertTrue(move instanceof Othello.OthelloMove);
//    }
//
//    @Test
//    public void testEquals() {
//        Position position1 = Position.createStartingPosition();
//        Position position2 = Position.createStartingPosition();
//
//        OthelloState state1 = new OthelloState(position1);
//        OthelloState state2 = new OthelloState(position2);
//
//        assertEquals(state1, state2);
//        assertEquals(state1.hashCode(), state2.hashCode());
//    }
//
//    @Test
//    public void testNotEquals() {
//        Position position1 = Position.createStartingPosition();
//        Position position2 = Position.createStartingPosition().move(Othello.BLACK, 2, 3);
//
//        OthelloState state1 = new OthelloState(position1);
//        OthelloState state2 = new OthelloState(position2);
//
//        assertNotEquals(state1, state2);
//        assertNotEquals(state1.hashCode(), state2.hashCode());
//    }
}