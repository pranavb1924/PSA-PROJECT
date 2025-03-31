package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class TicTacToe implements Game<TicTacToe> {

    public static final int X = 1;
    public static final int O = 0;
    public static final int blank = -1;

    public static void main(String[] args) {
        State<TicTacToe> finalState = new TicTacToe().runGame();
        if (finalState.winner().isPresent()) {
            System.out.println("TicTacToe: winner is: " + finalState.winner().get());
        } else {
            System.out.println("TicTacToe: draw");
        }
    }

    /**
     * Provides the starting board position.
     */
    public static Position startingPosition() {
        return Position.parsePosition(". . .\n. . .\n. . .", blank);
    }

    /**
     * Runs a game of Tic Tac Toe and displays the board at each move.
     */
    //public State<TicTacToe> runGame() {
        //State<TicTacToe> state = start();
        //int player = opener();
       // while (!state.isTerminal()) {
          //  displayBoard(((TicTacToeState) state).position());
           // state = state.next(state.chooseMove(player));
            //player = 1 - player;
       // }
        // Display the final board position.
       // displayBoard(((TicTacToeState) state).position());
       // return state;
   // }

    public State<TicTacToe> runGame() {
        State<TicTacToe> state = start();
        // No need to track player here if MCTS is fully handling moves.
        while (!state.isTerminal()) {
            displayBoard(((TicTacToeState) state).position());
            
            // Create a root node for the current state.
            Node<TicTacToe> rootNode = new TicTacToeNode(state);
            
            // Instantiate MCTS with the current root node.
            MCTS mcts = new MCTS(rootNode);
            
            // Run the MCTS search for a fixed number of iterations (e.g., 1000).
            Node<TicTacToe> bestNode = mcts.searchIterations(1000);
            
            // Update the game state to the state stored in the best node.
            state = bestNode.state();
        }
        // Display the final board position.
        displayBoard(((TicTacToeState) state).position());
        return state;
    }
    

    /**
     * Returns the opening player (X).
     */
    public int opener() {
        return X;
    }

    /**
     * Returns the starting state of the game.
     */
    public State<TicTacToe> start() {
        return new TicTacToeState();
    }

    private final Random random;

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
     * Displays the Tic Tac Toe board with row and column indices.
     *
     * @param position the current board position.
     */
    public static void displayBoard(Position position) {
        String rendered = position.render();
        String[] rows = rendered.split("\n");
        int numCols = rows[0].split(" ").length;

        // Print column headers.
        System.out.print("    ");
        for (int j = 0; j < numCols; j++) {
            System.out.print(j + "   ");
        }
        System.out.println();

        // Print a separator line.
        System.out.print("  ");
        for (int j = 0; j < numCols; j++) {
            System.out.print("----");
        }
        System.out.println();

        // Print each row with its row index.
        for (int i = 0; i < rows.length; i++) {
            System.out.printf("%d | ", i);
            String[] cells = rows[i].split(" ");
            for (String cell : cells) {
                System.out.printf("%-3s ", cell);
            }
            System.out.println();
        }
    }

    /**
     * Inner class representing a move in Tic Tac Toe.
     */
    static class TicTacToeMove implements Move<TicTacToe> {
        private final int player;
        private final int i;
        private final int j;

        public TicTacToeMove(int player, int i, int j) {
            this.player = player;
            this.i = i;
            this.j = j;
        }

        @Override
        public int player() {
            return player;
        }

        /**
         * Returns this move as an array: {row, column}.
         */
        public int[] move() {
            return new int[]{i, j};
        }
    }

    /**
     * Inner class representing a game state of Tic Tac Toe.
     */
    class TicTacToeState implements State<TicTacToe> {
        private final Position position;

        /**
         * Constructs a new state with the starting position.
         */
        public TicTacToeState() {
            this.position = startingPosition();
        }

        /**
         * Constructs a new state based on the given position.
         */
        public TicTacToeState(Position position) {
            this.position = position;
        }

        /**
         * Returns the current position of the board.
         */
        public Position position() {
            return position;
        }

        @Override
        public TicTacToe game() {
            return TicTacToe.this;
        }

        @Override
        public int player() {
            return switch (position.last) {
                case 0, -1 -> X;
                case 1 -> O;
                default -> blank;
            };
        }

        @Override
        public Optional<Integer> winner() {
            return position.winner();
        }

        @Override
        public Random random() {
            return random;
        }

        @Override
        public Collection<Move<TicTacToe>> moves(int player) {
            if (player == position.last) {
                throw new RuntimeException("consecutive moves by same player: " + player);
            }
            Collection<int[]> moves = position.moves(player);
            Collection<Move<TicTacToe>> result = new ArrayList<>();
            for (int[] coords : moves) {
                result.add(new TicTacToeMove(player, coords[0], coords[1]));
            }
            return result;
        }

        @Override
        public State<TicTacToe> next(Move<TicTacToe> move) {
            TicTacToeMove ttm = (TicTacToeMove) move;
            int[] coords = ttm.move();
            return new TicTacToeState(position.move(move.player(), coords[0], coords[1]));
        }

        @Override
        public boolean isTerminal() {
            return position.full() || position.winner().isPresent();
        }

        @Override
        public String toString() {
            return "TicTacToeState:\n" + position.toString();
        }
    }
}
