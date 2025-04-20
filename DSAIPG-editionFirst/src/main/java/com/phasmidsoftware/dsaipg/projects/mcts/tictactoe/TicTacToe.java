package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.MCTSStatistics;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe implements Game<TicTacToe> {

    public static final int X = 1;
    public static final int O = 0;
    public static final int blank = -1;
    
    // Number of MCTS iterations
    private static final int DEFAULT_MCTS_ITERATIONS = 5000;
    
    // Add options for configurable iterations
    private int mctsIterations = DEFAULT_MCTS_ITERATIONS;

    private final Random random;
    
    // Add statistics tracking
    private final MCTSStatistics stats = new MCTSStatistics();

    public TicTacToe(Random random) {
        this.random = random;
    }
    
    public TicTacToe(long seed) {
        this(new Random(seed));
    }
    
    public TicTacToe() {
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
    public State<TicTacToe> start() {
        // Start with a blank board.
        return new TicTacToeState(Position.parsePosition(". . .\n. . .\n. . .", blank));
    }

    @Override
    public int opener() {
        return X; // X always goes first.
    }

    public Random getRandom() {
        return random;
    }

    public State<TicTacToe> runGame() {
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
        
        State<TicTacToe> state = start();
        int currentPlayer = opener();
        
        if (humanPlays) {
            System.out.println("Starting Tic Tac Toe game");
            System.out.println("You are playing as X (first player)");
        } else {
            System.out.println("Starting Computer vs. Computer game");
            System.out.println("X: MCTS Player (" + mctsIterations + " iterations), O: Random Player");
        }
        
        while (!state.isTerminal()) {
            Position pos = ((TicTacToeState) state).getPosition();
            displayBoard(pos);
            System.out.println("Player " + (currentPlayer == X ? "X" : "O") + "'s turn.");

            if (humanPlays && currentPlayer == X) {
                // Human plays as X
                boolean validMove = false;
                while (!validMove) {
                    try {
                        System.out.print("Enter your move (row,col): ");
                        String input = scanner.nextLine();
                        String[] parts = input.split(",");
                        int row = Integer.parseInt(parts[0].trim());
                        int col = Integer.parseInt(parts[1].trim());
                        Move<TicTacToe> move = new TicTacToeMove(currentPlayer, row, col);
                        state = state.next(move);
                        validMove = true;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage() + ". Try again.");
                    }
                }
            } else if (!humanPlays && currentPlayer == O) {
                // Computer plays as O with random moves in computer vs. computer mode
                System.out.println("Random Player is thinking...");
                try {
                    Thread.sleep(500); // Add a small delay to make it easier to follow
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                Move<TicTacToe> randomMove = state.chooseMove(currentPlayer);
                state = state.next(randomMove);
                
                int[] coords = ((TicTacToeMove)randomMove).move();
                System.out.println("Random Player played at: " + coords[0] + "," + coords[1]);
            } else {
                // Computer plays with MCTS (either as O in Human vs Computer or as X in Computer vs Computer)
                System.out.println("MCTS Player is thinking...");
                
                try {
                    Thread.sleep(500); // Add a small delay to make it easier to follow
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Use enhanced MCTS to find the best move
                TicTacToeNode rootNode = new TicTacToeNode(state);
                MCTS mcts = new MCTS(rootNode);
                
                // Start timing
                stats.startTiming();
                
                // Run MCTS search
                Node<TicTacToe> bestNode = mcts.searchIterations(mctsIterations);
                
                // Stop timing and record the runtime
                long elapsedTime = stats.stopTiming(mctsIterations);
                System.out.println("MCTS completed in " + elapsedTime + " ms");
                
                if (bestNode == rootNode) {
                    System.out.println("MCTS couldn't find a good move. Using random move.");
                    stats.recordMCTSFallback();
                    
                    state = state.next(state.chooseMove(currentPlayer));
                } else {
                    Move<TicTacToe> mctsMove = ((TicTacToeNode)bestNode).getMoveFromParent();
                    state = bestNode.state();
                    
                    if (mctsMove != null) {
                        int[] coords = ((TicTacToeMove)mctsMove).move();
                        System.out.println("MCTS Player played at: " + coords[0] + "," + coords[1]);
                    } else {
                        System.out.println("MCTS Player made a move.");
                    }
                }
            }
            currentPlayer = 1 - currentPlayer;
        }

        displayBoard(((TicTacToeState) state).getPosition());
        Optional<Integer> winner = state.winner();
        
        boolean mctsWon = false;
        boolean isDraw = false;
        
        if (winner.isPresent()) {
            String winnerName;
            if (humanPlays) {
                winnerName = winner.get() == X ? "You (X)" : "Computer (O)";
                mctsWon = winner.get() == O; // MCTS wins as O in Human vs Computer
            } else {
                winnerName = winner.get() == X ? "MCTS Player (X)" : "Random Player (O)";
                mctsWon = winner.get() == X; // MCTS wins as X in Computer vs Computer
            }
            System.out.println("Game Over. Winner: " + winnerName);
        } else {
            System.out.println("Game ended in a draw!");
            isDraw = true;
        }
        
        // Record game stats for games involving MCTS (both modes)
        if (!humanPlays || (humanPlays && !isDraw)) {
            stats.recordGameResult(mctsWon, isDraw, mctsIterations);
            
            // Show statistics if it's a computer vs computer game
            if (!humanPlays) {
                System.out.println(stats.generateReport());
            }
        }
        
        return state;
    }
    
    /**
     * Run benchmark to test MCTS performance with different iteration counts.
     */
    private State<TicTacToe> runBenchmark(Scanner scanner) {
        System.out.println("\n=== MCTS Performance Benchmark ===");
        
        // Get number of games to run
        int numGames = 10;
        try {
            System.out.print("Enter number of games to run per iteration setting (default 10): ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                numGames = Integer.parseInt(input);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Using default " + numGames + " games.");
        }
        
        // Define iteration counts to test
        int[] iterationsToTest = {100, 500, 1000, 5000, 10000};
        try {
            System.out.print("Enter iterations to test (comma-separated, e.g. 100,500,1000): ");
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
                State<TicTacToe> state = start();
                int currentPlayer = opener();
                while (!state.isTerminal()) {
                    if (currentPlayer == X) {
                        // MCTS player as X
                        TicTacToeNode rootNode = new TicTacToeNode(state);
                        MCTS mcts = new MCTS(rootNode);
                        
                        stats.startTiming();
                        Node<TicTacToe> bestNode = mcts.searchIterations(iterations);
                        stats.stopTiming(iterations);
                        
                        if (bestNode == rootNode) {
                            stats.recordMCTSFallback();
                            state = state.next(state.chooseMove(currentPlayer));
                        } else {
                            state = bestNode.state();
                        }
                    } else {
                        // Random player as O
                        Move<TicTacToe> randomMove = state.chooseMove(currentPlayer);
                        state = state.next(randomMove);
                    }
                    currentPlayer = 1 - currentPlayer;
                }
                
                // Record game result
                Optional<Integer> winner = state.winner();
                boolean mctsWon = winner.isPresent() && winner.get() == X;
                boolean isDraw = !winner.isPresent();
                
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

    public static void displayBoard(Position pos) {
        System.out.println(pos.render());
    }

    /**
     * Concrete implementation of Move for Tic Tac Toe.
     */
    public static class TicTacToeMove implements Move<TicTacToe> {
        private final int player;
        private final int row;
        private final int col;

        public TicTacToeMove(int player, int row, int col) {
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
            return "Move(" + (player == X ? "X" : "O") + " at " + row + "," + col + ")";
        }
    }

    public static void main(String[] args) {
        new TicTacToe().runGame();
    }
}