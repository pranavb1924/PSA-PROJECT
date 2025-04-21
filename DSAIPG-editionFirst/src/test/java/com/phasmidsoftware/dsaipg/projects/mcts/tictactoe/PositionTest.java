package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class PositionTest {

    @Test(expected = RuntimeException.class)
    public void testMove_2() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        target.move(1, 0, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testMove_1() {
        String grid = "X X 0\nX O 0\nX X 0";
        Position target = Position.parsePosition(grid, 1);
        target.move(1, 0, 0);
    }

    @Test(expected = RuntimeException.class)
    public void testMove0() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        target.move(1, 0, 1);
    }

    @Test
    public void testMove1() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        Position moved = target.move(0, 0, 1);
        Position expected = Position.parsePosition(grid.replaceFirst("\\.", "O"), 0);
        assertEquals(expected, moved);
    }

    @Test
    public void testMoves() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        List<int[]> moves = target.moves(0);
        assertEquals(6, moves.size());
        assertArrayEquals(new int[]{0, 1}, moves.get(0));
        assertArrayEquals(new int[]{0, 2}, moves.get(1));
        assertArrayEquals(new int[]{1, 0}, moves.get(2));
        assertArrayEquals(new int[]{1, 2}, moves.get(3));
        assertArrayEquals(new int[]{2, 0}, moves.get(4));
        assertArrayEquals(new int[]{2, 1}, moves.get(5));
    }

    @Test
    public void testReflect() {
        // Create a position with X in top left, O in center, and X in bottom right
        Position position = Position.parsePosition(
                "X . .\n" +
                        ". O .\n" +
                        ". . X", 1);  // Last player was X (1)

        // Test reflection about horizontal axis (axis 0)
        Position reflectedH = position.reflect(0);

        // The expected position after horizontal reflection:
        // ". . X\n" +
        // ". O .\n" +
        // "X . ."

        // Check specific cells to verify the reflection worked properly
        int[][] expectedGridH = {
                {-1, -1, 1},  // top row: . . X
                {-1, 0, -1},  // middle row: . O .
                {1, -1, -1}   // bottom row: X . .
        };

        // Verify by checking specific cells
        assertEquals("Top left should move to bottom left", 1, reflectedH.projectRow(2)[0]);
        assertEquals("Bottom right should move to top right", 1, reflectedH.projectRow(0)[2]);
        assertEquals("Center should stay the same", 0, reflectedH.projectRow(1)[1]);

        // Test reflection about vertical axis (axis 1)
        Position reflectedV = position.reflect(1);

        // The expected position after vertical reflection:
        // ". . X\n" +
        // ". O .\n" +
        // "X . ."

        // Check specific cells for vertical reflection
        assertEquals("Top left should move to top right", 1, reflectedV.projectRow(0)[2]);
        assertEquals("Bottom right should move to bottom left", 1, reflectedV.projectRow(2)[0]);
        assertEquals("Center should stay the same", 0, reflectedV.projectRow(1)[1]);

        // Verify other properties remain unchanged
        assertEquals("Move count should be preserved", position.toString().chars().filter(ch -> ch != ',' && ch != '\n' && ch != '-').count(),
                reflectedH.toString().chars().filter(ch -> ch != ',' && ch != '\n' && ch != '-').count());
        assertEquals("Last player should be preserved", 1, reflectedH.lastPlayer());
    }

    @Test
    public void testRotate() {
        // Create a position with a distinct pattern to test rotation
        Position position = Position.parsePosition(
                "X O .\n" +
                        ". X .\n" +
                        ". . O", 0);  // Last player was O (0)

        System.out.println("Original:");
        System.out.println(position.render());

        // Rotate 90 degrees clockwise
        Position rotated = position.rotate();

        System.out.println("After rotation:");
        System.out.println(rotated.render());

        // Verify all positions after rotation
        // Original: X O .  ->  Rotated: . . X
        //           . X .  ->           . X O
        //           . . O  ->           O . .

        // Top row
        assertEquals("Top left after rotation", Position.blank, rotated.projectRow(0)[0]);
        assertEquals("Top middle after rotation", Position.blank, rotated.projectRow(0)[1]);
        assertEquals("Top right after rotation", 1, rotated.projectRow(0)[2]);

        // Middle row
        assertEquals("Middle left after rotation", Position.blank, rotated.projectRow(1)[0]);
        assertEquals("Middle center after rotation", 1, rotated.projectRow(1)[1]);
        assertEquals("Middle right after rotation", 0, rotated.projectRow(1)[2]);

        // Bottom row
        assertEquals("Bottom left after rotation", 0, rotated.projectRow(2)[0]);
        assertEquals("Bottom middle after rotation", Position.blank, rotated.projectRow(2)[1]);
        assertEquals("Bottom right after rotation", Position.blank, rotated.projectRow(2)[2]);

        // Verify that rotating four times gets us back to the original
        Position rotated4 = rotated.rotate().rotate().rotate();

        System.out.println("After four rotations:");
        System.out.println(rotated4.render());

        for (int i = 0; i < 3; i++) {
            assertArrayEquals("Row " + i + " should match original after 4 rotations",
                    position.projectRow(i), rotated4.projectRow(i));
        }

        assertEquals("Last player should be preserved", 0, rotated.lastPlayer());
    }

    @Test
    public void testWinner0() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        assertTrue(target.winner().isEmpty());
    }

    @Test
    public void testWinner1() {
        String grid = "X . 0\nX O .\nX . 0";
        Position target = Position.parsePosition(grid, 1);
        Optional<Integer> winner = target.winner();
        assertTrue(winner.isPresent());
        assertEquals(Integer.valueOf(1), winner.get());
    }

    @Test
    public void testWinner2() {
        String grid = "0 . X\n0 X .\nO . X";
        Position target = Position.parsePosition(grid, 0);
        Optional<Integer> winner = target.winner();
        assertTrue(winner.isPresent());
        assertEquals(Integer.valueOf(0), winner.get());
    }

    @Test
    public void testProjectRow() {
        String grid = "X . 0\nX O .\nX . 0";
        Position target = Position.parsePosition(grid, 1);
        assertArrayEquals(new int[]{1, -1, 0}, target.projectRow(0));
        assertArrayEquals(new int[]{1, 0, -1}, target.projectRow(1));
        assertArrayEquals(new int[]{1, -1, 0}, target.projectRow(2));
    }

    @Test
    public void testProjectCol() {
        String grid = "X . 0\nX O .\nX . 0";
        Position target = Position.parsePosition(grid, 1);
        assertArrayEquals(new int[]{1, 1, 1}, target.projectCol(0));
        assertArrayEquals(new int[]{-1, 0, -1}, target.projectCol(1));
        assertArrayEquals(new int[]{0, -1, 0}, target.projectCol(2));
    }

    @Test
    public void testProjectDiag() {
        String grid = "X . 0\nX O .\nX . 0";
        Position target = Position.parsePosition(grid, 1);
        assertArrayEquals(new int[]{1, 0, 0}, target.projectDiag(true));
        assertArrayEquals(new int[]{1, 0, 0}, target.projectDiag(false));
    }

    @Test
    public void testParseCell() {
        assertEquals(0, Position.parseCell("0"));
        assertEquals(0, Position.parseCell("O"));
        assertEquals(0, Position.parseCell("o"));
        assertEquals(1, Position.parseCell("X"));
        assertEquals(1, Position.parseCell("x"));
        assertEquals(1, Position.parseCell("1"));
        assertEquals(-1, Position.parseCell("."));
        assertEquals(-1, Position.parseCell("a"));
    }

    @Test
    public void testThreeInARow() {
        String grid = "X . 0\nX O .\nX . 0";
        Position target = Position.parsePosition(grid, 1);
        assertTrue(target.threeInARow());
    }

    @Test
    public void testFull() {
        assertFalse(Position.parsePosition("X . 0\nX O .\nX . 0", 1).full());
        assertTrue(Position.parsePosition("X X 0\nX O 0\nX X 0", 1).full());
    }

    @Test
    public void testRender() {
        String grid = "X . .\n. O .\n. . X";
        Position target = Position.parsePosition(grid, 1);
        assertEquals(grid, target.render());
    }

    @Test
    public void testToString() {
        Position target = Position.parsePosition("X . .\n. O .\n. . X", 1);
        assertEquals("1,-1,-1\n-1,0,-1\n-1,-1,1", target.toString());
    }
}