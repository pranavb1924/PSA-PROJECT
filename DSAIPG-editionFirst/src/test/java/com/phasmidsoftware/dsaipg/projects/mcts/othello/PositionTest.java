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
}