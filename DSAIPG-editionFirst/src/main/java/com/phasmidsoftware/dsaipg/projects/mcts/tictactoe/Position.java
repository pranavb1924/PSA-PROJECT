package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Position {

    public static final int blank = -1;
    private static final int SIZE = 3;
    private final int[][] grid;
    private final int last;    // last player who moved
    private final int count;   // number of moves made

    public Position(int[][] grid, int count, int last) {
        this.grid = grid;
        this.count = count;
        this.last = last;
    }

    /**
     * Parses a string representation into a Position.
     * Example: ". . .\n. . .\n. . ." with last.
     */
    public static Position parsePosition(String gridStr, int last) {
        String[] rows = gridStr.split("\\n");
        int[][] grid = new int[SIZE][SIZE];
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            String[] tokens = rows[i].trim().split(" ");
            for (int j = 0; j < SIZE; j++) {
                int val = parseCell(tokens[j]);
                grid[i][j] = val;
                if (val != blank) {
                    count++;
                }
            }
        }
        return new Position(grid, count, last);
    }

    /**
     * Parses an individual cell.
     * "X" or "1" returns 1; "O" or "0" returns 0; anything else returns blank.
     */
    public static int parseCell(String cell) {
        cell = cell.trim();
        if (cell.equalsIgnoreCase("X") || cell.equals("1"))
            return 1;
        else if (cell.equalsIgnoreCase("O") || cell.equals("0"))
            return 0;
        else
            return blank;
    }

    /**
     * Returns a new Position resulting from applying the move.
     */
    public Position move(int player, int row, int col) {
        if (full())
            throw new RuntimeException("Board is full");
        if (grid[row][col] != blank)
            throw new RuntimeException("Cell already occupied");
        if (player == last)
            throw new RuntimeException("Consecutive moves by same player not allowed");
        int[][] newGrid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], SIZE);
        }
        newGrid[row][col] = player;
        return new Position(newGrid, count + 1, player);
    }

    /**
     * Returns a list of available moves for the given player.
     */
    public List<int[]> moves(int player) {
        if (player == last)
            throw new RuntimeException("Cannot move twice in a row");
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                if (grid[i][j] == blank)
                    list.add(new int[]{i, j});
        return list;
    }

    public boolean full() {
        return count == SIZE * SIZE;
    }

    /**
     * Returns an Optional with the winning player if three in a row exist.
     */
    public Optional<Integer> winner() {
        if (count < 5)
            return Optional.empty();
        return threeInARow() ? Optional.of(last) : Optional.empty();
    }

    /**
     * Returns true if there exists a row, column, or diagonal where all cells match.
     */
    public boolean threeInARow() {
        // Check rows.
        for (int i = 0; i < SIZE; i++) {
            int[] row = projectRow(i);
            if (Arrays.equals(row, createWinningLine(row[0])))
                return true;
        }
        // Check columns.
        for (int j = 0; j < SIZE; j++) {
            int[] col = projectCol(j);
            if (Arrays.equals(col, createWinningLine(col[0])))
                return true;
        }
        // Check main diagonal.
        int[] diag1 = projectDiag(true);
        if (Arrays.equals(diag1, createWinningLine(diag1[0])))
            return true;
        // Check anti-diagonal.
        int[] diag2 = projectDiag(false);
        if (Arrays.equals(diag2, createWinningLine(diag2[0])))
            return true;
        return false;
    }

    private int[] createWinningLine(int sample) {
        int winVal = (sample != blank) ? sample : last;
        int[] line = new int[SIZE];
        Arrays.fill(line, winVal);
        return line;
    }

    /**
     * Returns a copy of row i.
     */
    public int[] projectRow(int i) {
        return Arrays.copyOf(grid[i], SIZE);
    }

    /**
     * Returns an array representing column j.
     */
    public int[] projectCol(int j) {
        int[] col = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            col[i] = grid[i][j];
        }
        return col;
    }

    /**
     * Returns the specified diagonal.
     * If main is true, returns the main diagonal; otherwise the anti-diagonal.
     */
    public int[] projectDiag(boolean main) {
        int[] diag = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            diag[i] = main ? grid[i][i] : grid[i][SIZE - 1 - i];
        }
        return diag;
    }

    /**
     * Renders the board for display.
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                char ch;
                if (grid[i][j] == 1)
                    ch = 'X';
                else if (grid[i][j] == 0)
                    ch = 'O';
                else
                    ch = '.';
                sb.append(ch);
                if (j < SIZE - 1)
                    sb.append(" ");
            }
            if (i < SIZE - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * toString returns a comma-separated representation of rows.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(grid[i][j]);
                if (j < SIZE - 1)
                    sb.append(",");
            }
            if (i < SIZE - 1)
                sb.append("\n");
        }
        return sb.toString();
    }

    public int lastPlayer() {
        return last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position other = (Position) o;
        return count == other.count && last == other.last && Arrays.deepEquals(grid, other.grid);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(grid);
        result = 31 * result + count;
        result = 31 * result + last;
        return result;
    }
}
