package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PositionTest {

    @Test
    public void testCreateStartingPosition() {
        Position position = Position.createStartingPosition();

        // Check the board layout
        int[][] board = position.getBoard();

        // Center pieces should be in correct starting configuration
        assertEquals(Position.WHITE, board[3][3]);
        assertEquals(Position.BLACK, board[3][4]);
        assertEquals(Position.BLACK, board[4][3]);
        assertEquals(Position.WHITE, board[4][4]);

        // All other cells should be empty
        for (int i = 0; i < Position.getSize(); i++) {
            for (int j = 0; j < Position.getSize(); j++) {
                if ((i == 3 && j == 3) || (i == 4 && j == 4) ||
                        (i == 3 && j == 4) || (i == 4 && j == 3)) {
                    continue; // Skip the center pieces
                }
                assertEquals(Position.EMPTY, board[i][j]);
            }
        }

        // Piece count should be 4
        assertEquals(2, position.countPieces(Position.BLACK));
        assertEquals(2, position.countPieces(Position.WHITE));

        // No winner yet
        assertTrue(position.winner().isEmpty());

        // Game not over
        assertFalse(position.isGameOver());

        // Nobody moved yet
        assertEquals(Position.EMPTY, position.lastPlayer());
    }

    @Test
    public void testValidMoves_InitialPosition() {
        Position position = Position.createStartingPosition();

        // Black has 4 valid starting moves
        List<int[]> blackMoves = position.moves(Position.BLACK);
        assertEquals(4, blackMoves.size());

        // Check each move is in the correct position (2,3), (3,2), (4,5), (5,4)
        boolean found2_3 = false, found3_2 = false, found4_5 = false, found5_4 = false;

        for (int[] move : blackMoves) {
            if (move[0] == 2 && move[1] == 3) found2_3 = true;
            if (move[0] == 3 && move[1] == 2) found3_2 = true;
            if (move[0] == 4 && move[1] == 5) found4_5 = true;
            if (move[0] == 5 && move[1] == 4) found5_4 = true;
        }

        assertTrue(found2_3);
        assertTrue(found3_2);
        assertTrue(found4_5);
        assertTrue(found5_4);
    }

    @Test
    public void testValidMove() {
        Position position = Position.createStartingPosition();

        // Make a valid move
        Position newPosition = position.move(Position.BLACK, 2, 3);

        // Check the new piece was placed
        assertEquals(Position.BLACK, newPosition.getBoard()[2][3]);

        // Check that pieces were flipped
        assertEquals(Position.BLACK, newPosition.getBoard()[3][3]); // Was WHITE, should now be BLACK

        // Check other pieces were unchanged
        assertEquals(Position.WHITE, newPosition.getBoard()[4][4]);
        assertEquals(Position.BLACK, newPosition.getBoard()[3][4]);
        assertEquals(Position.BLACK, newPosition.getBoard()[4][3]);

        // Check piece count
        assertEquals(4, newPosition.countPieces(Position.BLACK)); // 2 + 2 (1 new, 1 flipped)
        assertEquals(1, newPosition.countPieces(Position.WHITE)); // 2 - 1 (1 flipped)

        // Check last player
        assertEquals(Position.BLACK, newPosition.lastPlayer());
    }

//    @Test(expected = IllegalStateException.class)
//    public void testInvalidMove_OccupiedCell() {
//        Position position = Position.createStartingPosition();
//
//        // Try to place on an already occupied cell
//        position.move(Position.BLACK, 3, 3);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void testInvalidMove_NoFlips() {
//        Position position = Position.createStartingPosition();
//
//        // Try to place where no flips would occur
//        position.move(Position.BLACK, 0, 0);
//    }
//
//    @Test
//    public void testGetFlips() {
//        Position position = Position.createStartingPosition();
//
//        // Check flips for a valid move
//        List<int[]> flips = position.getFlips(Position.BLACK, 2, 3);
//        assertEquals(1, flips.size());
//
//        // The flip should be at position (3,3)
//        assertEquals(3, flips.get(0)[0]);
//        assertEquals(3, flips.get(0)[1]);
//
//        // Check flips for an invalid move
//        List<int[]> noFlips = position.getFlips(Position.BLACK, 0, 0);
//        assertTrue(noFlips.isEmpty());
//    }
//
//    @Test
//    public void testIsFull() {
//        Position position = Position.createStartingPosition();
//        assertFalse(position.isFull());
//
//        // Create a full board
//        int[][] fullBoard = new int[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                fullBoard[i][j] = Position.BLACK; // Doesn't matter which color for this test
//            }
//        }
//
//        Position fullPosition = new Position(fullBoard, 64, Position.BLACK);
//        assertTrue(fullPosition.isFull());
//    }
//
//    @Test
//    public void testIsGameOver() {
//        Position position = Position.createStartingPosition();
//        assertFalse(position.isGameOver());
//
//        // Create a board where neither player can move
//        int[][] deadlockBoard = new int[8][8];
//        // Fill with alternating colors in a pattern that leaves no valid moves
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                if ((i + j) % 2 == 0) {
//                    deadlockBoard[i][j] = Position.BLACK;
//                } else {
//                    deadlockBoard[i][j] = Position.WHITE;
//                }
//            }
//        }
//
//        Position deadlockPosition = new Position(deadlockBoard, 64, Position.BLACK);
//        assertTrue(deadlockPosition.isGameOver());
//    }
//
//    @Test
//    public void testWinner() {
//        // Create a position with more black pieces
//        int[][] blackWinsBoard = new int[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                blackWinsBoard[i][j] = (i < 5) ? Position.BLACK : Position.WHITE;
//            }
//        }
//
//        Position blackWinsPosition = new Position(blackWinsBoard, 64, Position.WHITE);
//        Optional<Integer> winner = blackWinsPosition.winner();
//        assertTrue(winner.isPresent());
//        assertEquals(Integer.valueOf(Position.BLACK), winner.get());
//
//        // Create a position with more white pieces
//        int[][] whiteWinsBoard = new int[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                whiteWinsBoard[i][j] = (i < 3) ? Position.BLACK : Position.WHITE;
//            }
//        }
//
//        Position whiteWinsPosition = new Position(whiteWinsBoard, 64, Position.BLACK);
//        winner = whiteWinsPosition.winner();
//        assertTrue(winner.isPresent());
//        assertEquals(Integer.valueOf(Position.WHITE), winner.get());
//
//        // Create a tied position
//        int[][] tieBoard = new int[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                tieBoard[i][j] = (i < 4) ? Position.BLACK : Position.WHITE;
//            }
//        }
//
//        Position tiePosition = new Position(tieBoard, 64, Position.BLACK);
//        winner = tiePosition.winner();
//        assertFalse(winner.isPresent()); // No winner in a tie
//    }
//
//    @Test
//    public void testRender() {
//        Position position = Position.createStartingPosition();
//        String rendered = position.render();
//
//        // Check that render output contains the correct piece counts
//        assertTrue(rendered.contains("Black: 2"));
//        assertTrue(rendered.contains("White: 2"));
//
//        // Check that render includes the board display
//        assertTrue(rendered.contains("3 . . . W B . . ."));
//        assertTrue(rendered.contains("4 . . . B W . . ."));
//    }
//
//    @Test
//    public void testEquals() {
//        Position position1 = Position.createStartingPosition();
//        Position position2 = Position.createStartingPosition();
//
//        assertEquals(position1, position2);
//        assertEquals(position1.hashCode(), position2.hashCode());
//
//        // Make a move and check inequality
//        Position position3 = position1.move(Position.BLACK, 2, 3);
//        assertNotEquals(position1, position3);
//        assertNotEquals(position1.hashCode(), position3.hashCode());
//    }
}