package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Position {

    public static final int EMPTY = -1;
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    private static final int SIZE = 8;
    
    // Directions for checking valid moves
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };
    
    private final int[][] board;
    private final int lastPlayer;
    private final int pieceCount;

    public Position(int[][] board, int pieceCount, int lastPlayer) {
        this.board = board;
        this.pieceCount = pieceCount;
        this.lastPlayer = lastPlayer;
    }

    /**
     * Creates a new Othello board with the standard starting position.
     */
    public static Position createStartingPosition() {
        int[][] board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            Arrays.fill(board[i], EMPTY);
        }
        
        // Initial 4 pieces in the center
        board[3][3] = WHITE;
        board[3][4] = BLACK;
        board[4][3] = BLACK;
        board[4][4] = WHITE;
        
        return new Position(board, 4, EMPTY); // No one has moved yet
    }

    /**
     * Returns a new Position resulting from applying the move.
     */
    public Position move(int player, int row, int col) {
        if (isFull()) {
            throw new IllegalStateException("Board is full");
        }
        if (board[row][col] != EMPTY) {
            throw new IllegalStateException("Cell already occupied at " + row + "," + col);
        }
        
        // We won't check if the same player is moving twice in this method
        // That should be handled at the State level
        
        // Check if this is a valid move (flips at least one piece)
        List<int[]> flips = getFlips(player, row, col);
        if (flips.isEmpty()) {
            throw new IllegalStateException("Invalid move at " + row + "," + col + " - must flip at least one piece");
        }
        
        // Create a new board with the move applied
        int[][] newBoard = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            newBoard[i] = Arrays.copyOf(board[i], SIZE);
        }
        
        // Place the new piece
        newBoard[row][col] = player;
        
        // Flip captured pieces
        for (int[] pos : flips) {
            newBoard[pos[0]][pos[1]] = player;
        }
        
        // The total piece count increases by 1
        return new Position(newBoard, pieceCount + 1, player);
    }

    /**
     * Returns a list of positions that would be flipped if player places at (row, col).
     */
    public List<int[]> getFlips(int player, int row, int col) {
        if (board[row][col] != EMPTY) {
            return new ArrayList<>(); // Can't place on an occupied cell
        }
        
        List<int[]> flips = new ArrayList<>();
        
        for (int[] dir : DIRECTIONS) {
            List<int[]> dirFlips = getFlipsInDirection(player, row, col, dir[0], dir[1]);
            flips.addAll(dirFlips);
        }
        
        return flips;
    }

    /**
     * Returns a list of positions that would be flipped in a particular direction.
     */
    private List<int[]> getFlipsInDirection(int player, int row, int col, int dRow, int dCol) {
        List<int[]> flips = new ArrayList<>();
        int opponent = 1 - player; // Opponent is the other player
        
        int r = row + dRow;
        int c = col + dCol;
        
        // Temporary list to track potential flips
        List<int[]> temp = new ArrayList<>();
        
        // Keep going in this direction as long as we're finding opponent pieces
        while (isValidPosition(r, c) && board[r][c] == opponent) {
            temp.add(new int[]{r, c});
            r += dRow;
            c += dCol;
        }
        
        // If we hit one of our own pieces, all the opponent pieces in between get flipped
        if (isValidPosition(r, c) && board[r][c] == player && !temp.isEmpty()) {
            flips.addAll(temp);
        }
        
        return flips;
    }

    /**
     * Returns true if the position is within the board boundaries.
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Returns a list of available moves for the given player.
     */
    public List<int[]> moves(int player) {
        // We won't check if the same player is moving twice in this method
        // That should be handled at the State level
        
        List<int[]> validMoves = new ArrayList<>();
        
        // Check each empty cell for valid moves
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY && !getFlips(player, i, j).isEmpty()) {
                    validMoves.add(new int[]{i, j});
                }
            }
        }
        
        return validMoves;
    }

    /**
     * Returns true if the board is full.
     */
    public boolean isFull() {
        return pieceCount == SIZE * SIZE;
    }

    /**
     * Returns true if neither player can make a valid move.
     */
    public boolean isGameOver() {
        if (isFull()) {
            return true;
        }
        
        // Don't check for consecutive passes, just if any player has moves
        List<int[]> blackMoves = new ArrayList<>();
        List<int[]> whiteMoves = new ArrayList<>();
        
        // Check each empty cell for valid moves for each player
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    if (!getFlips(BLACK, i, j).isEmpty()) {
                        blackMoves.add(new int[]{i, j});
                    }
                    if (!getFlips(WHITE, i, j).isEmpty()) {
                        whiteMoves.add(new int[]{i, j});
                    }
                }
            }
        }
        
        // Game is over if neither player has valid moves
        return blackMoves.isEmpty() && whiteMoves.isEmpty();
    }

    /**
     * Returns the winning player if the game is over.
     */
    public Optional<Integer> winner() {
        if (!isGameOver()) {
            return Optional.empty();
        }
        
        int blackCount = countPieces(BLACK);
        int whiteCount = countPieces(WHITE);
        
        if (blackCount > whiteCount) {
            return Optional.of(BLACK);
        } else if (whiteCount > blackCount) {
            return Optional.of(WHITE);
        } else {
            // It's a draw
            return Optional.empty();
        }
    }
    
    /**
     * Counts the number of pieces for a player.
     */
    public int countPieces(int player) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == player) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the last player who moved.
     */
    public int lastPlayer() {
        return lastPlayer;
    }
    
    /**
     * Get the current state of the board.
     */
    public int[][] getBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            copy[i] = Arrays.copyOf(board[i], SIZE);
        }
        return copy;
    }
    
    /**
     * Get the board size.
     */
    public static int getSize() {
        return SIZE;
    }

    /**
     * Renders the board for display.
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("  0 1 2 3 4 5 6 7\n");
        
        for (int i = 0; i < SIZE; i++) {
            sb.append(i).append(" ");
            for (int j = 0; j < SIZE; j++) {
                char ch;
                if (board[i][j] == BLACK) {
                    ch = 'B';
                } else if (board[i][j] == WHITE) {
                    ch = 'W';
                } else {
                    ch = '.';
                }
                sb.append(ch).append(" ");
            }
            sb.append("\n");
        }
        
        sb.append("\nBlack: ").append(countPieces(BLACK))
          .append(", White: ").append(countPieces(WHITE));
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position other = (Position) o;
        return pieceCount == other.pieceCount && 
               lastPlayer == other.lastPlayer && 
               Arrays.deepEquals(board, other.board);
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(board);
        result = 31 * result + pieceCount;
        result = 31 * result + lastPlayer;
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(board[i][j]);
                if (j < SIZE - 1) {
                    sb.append(",");
                }
            }
            if (i < SIZE - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}