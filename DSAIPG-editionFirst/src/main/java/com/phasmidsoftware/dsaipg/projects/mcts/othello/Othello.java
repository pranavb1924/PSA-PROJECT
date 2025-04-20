package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.MCTSStatistics;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Othello implements Game<Othello> {

    public static final int BLACK = Position.BLACK;  // 0
    public static final int WHITE = Position.WHITE;  // 1
    public static final int EMPTY = Position.EMPTY;  // -1
    
    // Number of MCTS iterations - can be adjusted based on performance needs
    private static final int DEFAULT_MCTS_ITERATIONS = 10;
    
    // Add options for configurable iterations for experimentation
    private int mctsIterations = DEFAULT_MCTS_ITERATIONS;
    
    private final Random random;
    
    // Add statistics tracking
    private final MCTSStatistics stats = new MCTSStatistics();

    public Othello(Random random) {
        this.random = random;
    }
    
    public Othello(long seed) {
        this(new Random(seed));
    }
    
    public Othello() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Set the number of MCTS iterations to use.
     */
    public void setMCTSIterations(int iterations) {
        if (iterations > 0) {
            this.mctsIterations = iterations;
        }
    }
    
    /**
     * Get the MCTSStatistics object.
     */
    public MCTSStatistics getStats() {
        return stats;
    }

    @Override
    public State<Othello> start() {
        // Start with the standard Othello starting position
        return new OthelloState(Position.createStartingPosition());
    }

    @Override
    public int opener() {
        return BLACK; // Black always goes first in Othello
    }

    public Random getRandom() {
        return random;
    }

    public State<Othello> runGame() {
        Scanner scanner = new Scanner(System.in);
        
        // Ask for game mode
        System.out.println("Select game mode:");
        System.out.println("1. Human vs. Computer");
        System.out.println("2. Computer vs. Computer");
        System.out.println("3. Benchmark MCTS Performance");
        
        int mode = 0;
        while (mode < 1 || mode > 3) {
            try {
                System.out.print("Enter 1, 2, or 3: ");
                mode = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1, 2, or 3).");
            }
        }
        
        // If benchmark mode is selected
        if (mode == 3) {
            return runBenchmark(scanner);
        }
        
        boolean humanPlays = (mode == 1);
        
        // Ask for MCTS iterations
        if (mode == 2) {
            try {
                System.out.print("Enter number of MCTS iterations (default " + DEFAULT_MCTS_ITERATIONS + "): ");
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    int iterations = Integer.parseInt(input);
                    if (iterations > 0) {
                        mctsIterations = iterations;
                        System.out.println("Using " + mctsIterations + " MCTS iterations.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Using default " + mctsIterations + " iterations.");
            }
        }
        
        State<Othello> state = start();
        boolean consecutivePasses = false; // Track consecutive passes

        if (humanPlays) {
            System.out.println("Starting Othello game");
            System.out.println("You are playing as Black (B), computer is White (W)");
            System.out.println("Enter moves as 'row,col' (e.g., '3,4')");
        } else {
            System.out.println("Starting Computer vs. Computer game");
            System.out.println("Black: MCTS Player (" + mctsIterations + " iterations), White: Random Player");
        }
        
        // Main game loop
        while (!state.isTerminal()) {
            Position pos = ((OthelloState) state).getPosition();
            System.out.println("\n" + pos.render());
            
            // Get current player from the state
            int currentPlayer = state.player();
            
            // Check if current player has valid moves
            List<int[]> availableMoves = pos.moves(currentPlayer);
            
            if (availableMoves.isEmpty()) {
                System.out.println("Player " + (currentPlayer == BLACK ? "Black" : "White") + 
                                  " has no valid moves. Passing...");
                
                // Increment pass counter
                if (consecutivePasses) {
                    // Both players passed consecutively - game over
                    break;
                }
                consecutivePasses = true;
                
                // Create a new state with the same position to change the player
                state = new OthelloState(pos);
                continue;
            }
            
            // Reset consecutive passes counter since we have moves
            consecutivePasses = false;
            
            System.out.println("Player " + (currentPlayer == BLACK ? "Black" : "White") + "'s turn.");

            // Check if this is a human turn in Human vs Computer mode
            if (humanPlays && currentPlayer == BLACK) {
                // Human plays as Black in Human vs Computer mode
                boolean validMove = false;
                while (!validMove) {
                    try {
                        System.out.print("Enter your move (row,col): ");
                        String input = scanner.nextLine();
                        
                        // Check for help command
                        if (input.equalsIgnoreCase("help") || input.equalsIgnoreCase("h")) {
                            showHelp();
                            continue;
                        }
                        
                        // Check for hint command
                        if (input.equalsIgnoreCase("hint")) {
                            showHint(state, currentPlayer);
                            continue;
                        }
                        
                        String[] parts = input.split(",");
                        int row = Integer.parseInt(parts[0].trim());
                        int col = Integer.parseInt(parts[1].trim());
                        
                        // Validate move
                        if (row < 0 || row >= Position.getSize() || col < 0 || col >= Position.getSize()) {
                            System.out.println("Invalid position! Coordinates must be between 0 and 7.");
                            continue;
                        }
                        
                        // Create and apply the move
                        Move<Othello> move = new OthelloMove(currentPlayer, row, col);
                        state = state.next(move);
                        validMove = true;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input format. Use 'row,col' (e.g., '3,4')");
                    } catch (IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage() + ". Try again.");
                    }
                }
            } else {
                // Computer's turn (either White in Human vs Computer mode or both players in Computer vs Computer mode)
                String playerLabel;
                if (humanPlays) {
                    playerLabel = "Computer";
                } else {
                    playerLabel = (currentPlayer == BLACK) ? "MCTS Player" : "Random Player";
                }
                
                System.out.println(playerLabel + " is thinking...");
                
                try {
                    Thread.sleep(1000); // Add a delay to make it easier to follow
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                if (currentPlayer == BLACK && !humanPlays) {
                    // MCTS player (Black) in Computer vs Computer mode
                    OthelloNode rootNode = new OthelloNode(state);
                    MCTS mcts = new MCTS(rootNode);
                    
                    // Start timing
                    stats.startTiming();
                    
                    // Run MCTS search
                    Node<Othello> bestNode = mcts.searchIterations(mctsIterations);
                    
                    // Stop timing and record the runtime
                    long elapsedTime = stats.stopTiming(mctsIterations);
                    System.out.println("MCTS completed in " + elapsedTime + " ms");
                    
                    if (bestNode == rootNode) {
                        System.out.println("MCTS couldn't find a good move. Using random move.");
                        stats.recordMCTSFallback();
                        
                        if (!availableMoves.isEmpty()) {
                            int[] randomCoords = availableMoves.get(random.nextInt(availableMoves.size()));
                            Move<Othello> randomMove = new OthelloMove(currentPlayer, randomCoords[0], randomCoords[1]);
                            state = state.next(randomMove);
                            System.out.println("MCTS Player played at: " + randomCoords[0] + "," + randomCoords[1]);
                        }
                    } else {
                        state = bestNode.state();
                        Move<Othello> moveUsed = ((OthelloNode)bestNode).getMoveFromParent();
                        if (moveUsed != null) {
                            int[] coords = ((OthelloMove) moveUsed).move();
                            System.out.println("MCTS Player played at: " + coords[0] + "," + coords[1]);
                        } else {
                            System.out.println("MCTS Player made a move");
                        }
                    }
                } else {
                    // Random player (either White in Human vs Computer, or White in Computer vs Computer)
                    if (!availableMoves.isEmpty()) {
                        int[] randomCoords = availableMoves.get(random.nextInt(availableMoves.size()));
                        Move<Othello> randomMove = new OthelloMove(currentPlayer, randomCoords[0], randomCoords[1]);
                        state = state.next(randomMove);
                        System.out.println(playerLabel + " played at: " + randomCoords[0] + "," + randomCoords[1]);
                    }
                }
            }
        }

        // Game is over, show final board and results
        Position finalPos = ((OthelloState) state).getPosition();
        System.out.println("\nFinal board:");
        System.out.println(finalPos.render());
        
        int blackCount = finalPos.countPieces(BLACK);
        int whiteCount = finalPos.countPieces(WHITE);
        
        System.out.println("\nGame Over. Final score:");
        System.out.println("Black: " + blackCount);
        System.out.println("White: " + whiteCount);
        
        boolean mctsWon = false;
        boolean isDraw = false;
        
        if (blackCount > whiteCount) {
            System.out.println(humanPlays ? "You win!" : "MCTS Player (Black) wins!");
            if (!humanPlays) mctsWon = true;
        } else if (whiteCount > blackCount) {
            System.out.println(humanPlays ? "Computer wins!" : "Random Player (White) wins!");
        } else {
            System.out.println("It's a draw!");
            isDraw = true;
        }
        
        // Record game stats for computer vs computer games
        if (!humanPlays) {
            stats.recordGameResult(mctsWon, isDraw, mctsIterations);
            
            // Show statistics if it's a computer vs computer game
            System.out.println(stats.generateReport());
        }
        
        return state;
    }
    
    /**
     * Run benchmark to test MCTS performance with different iteration counts.
     */
    private State<Othello> runBenchmark(Scanner scanner) {
        System.out.println("\n=== MCTS Performance Benchmark ===");
        
        // Get number of games to run
        int numGames = 5;
        try {
            System.out.print("Enter number of games to run per iteration setting (default 5): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                numGames = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default " + numGames + " games.");
        }
        
        // Define iteration counts to test
        int[] iterationsToTest = {10, 50, 100, 500, 1000};
        try {
            System.out.print("Enter iterations to test (comma-separated, e.g. 10,50,100): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                String[] parts = input.split(",");
                iterationsToTest = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    iterationsToTest[i] = Integer.parseInt(parts[i].trim());
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Using default iteration values.");
        }
        
        System.out.println("\nRunning " + numGames + " games for each iteration count...");
        
        // Run games for each iteration count
        for (int iterations : iterationsToTest) {
            System.out.println("\nTesting with " + iterations + " iterations:");
            
            for (int game = 1; game <= numGames; game++) {
                System.out.println("  Game " + game + "/" + numGames + "...");
                mctsIterations = iterations;
                
                // Run a full game with current iteration count
                State<Othello> state = start();
                boolean consecutivePasses = false;
                
                // Silent mode for benchmark
                while (!state.isTerminal()) {
                    Position pos = ((OthelloState) state).getPosition();
                    int currentPlayer = state.player();
                    List<int[]> availableMoves = pos.moves(currentPlayer);
                    
                    if (availableMoves.isEmpty()) {
                        if (consecutivePasses) {
                            break;
                        }
                        consecutivePasses = true;
                        state = new OthelloState(pos);
                        continue;
                    }
                    
                    consecutivePasses = false;
                    
                    if (currentPlayer == BLACK) {
                        // MCTS player
                        OthelloNode rootNode = new OthelloNode(state);
                        MCTS mcts = new MCTS(rootNode);
                        
                        stats.startTiming();
                        Node<Othello> bestNode = mcts.searchIterations(iterations);
                        stats.stopTiming(iterations);
                        
                        if (bestNode == rootNode) {
                            stats.recordMCTSFallback();
                            if (!availableMoves.isEmpty()) {
                                int[] randomCoords = availableMoves.get(random.nextInt(availableMoves.size()));
                                Move<Othello> randomMove = new OthelloMove(currentPlayer, randomCoords[0], randomCoords[1]);
                                state = state.next(randomMove);
                            }
                        } else {
                            state = bestNode.state();
                        }
                    } else {
                        // Random player
                        if (!availableMoves.isEmpty()) {
                            int[] randomCoords = availableMoves.get(random.nextInt(availableMoves.size()));
                            Move<Othello> randomMove = new OthelloMove(currentPlayer, randomCoords[0], randomCoords[1]);
                            state = state.next(randomMove);
                        }
                    }
                }
                
                // Record game result
                Position finalPos = ((OthelloState) state).getPosition();
                int blackCount = finalPos.countPieces(BLACK);
                int whiteCount = finalPos.countPieces(WHITE);
                
                boolean mctsWon = blackCount > whiteCount;
                boolean isDraw = blackCount == whiteCount;
                
                stats.recordGameResult(mctsWon, isDraw, iterations);
                
                // Print brief result
                System.out.println("    Result: " + 
                                (isDraw ? "Draw" : (mctsWon ? "MCTS win" : "Random player win")));
            }
        }
        
        // Display comprehensive statistics
        System.out.println("\n" + stats.generateReport());
        
        return start(); // Return a fresh state
    }
    
    /**
     * Display available commands and rules
     */
    private void showHelp() {
        System.out.println("\n=== OTHELLO HELP ===");
        System.out.println("- Enter moves as 'row,col' (e.g., '3,4')");
        System.out.println("- Type 'hint' to see possible moves");
        System.out.println("- Type 'help' or 'h' to show this help");
        System.out.println("- Pieces are placed on intersections, not squares");
        System.out.println("- You can only place pieces that flip at least one opponent piece");
        System.out.println("- Game ends when the board is full or neither player can move");
        System.out.println("- The player with the most pieces at the end wins");
        System.out.println("===================\n");
    }
    
    /**
     * Show possible moves to help the player
     */
    private void showHint(State<Othello> state, int player) {
        Position position = ((OthelloState) state).getPosition();
        List<int[]> moves = position.moves(player);
        
        if (moves.isEmpty()) {
            System.out.println("No valid moves available. You must pass your turn.");
            return;
        }
        
        System.out.println("\nValid moves:");
        for (int[] move : moves) {
            System.out.println("- Row: " + move[0] + ", Column: " + move[1]);
        }
        System.out.println();
    }

    /**
     * Concrete implementation of Move for Othello.
     */
    public static class OthelloMove implements Move<Othello> {
        private final int player;
        private final int row;
        private final int col;

        public OthelloMove(int player, int row, int col) {
            this.player = player;
            this.row = row;
            this.col = col;
        }

        @Override
        public int player() {
            return player;
        }

        /**
         * Returns the move coordinates.
         */
        public int[] move() {
            return new int[]{row, col};
        }
        
        @Override
        public String toString() {
            return "Move(" + (player == BLACK ? "Black" : "White") + " at " + row + "," + col + ")";
        }
    }

    public static void main(String[] args) {
        new Othello().runGame();
    }
}