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

    public static Position startingPosition() {
        return Position.parsePosition(". . .\n. . .\n. . .", blank);
    }

    public State<TicTacToe> runGame() {
        State<TicTacToe> state = start();
        while (!state.isTerminal()) {
            displayBoard(((TicTacToeState) state).position());
            Node<TicTacToe> rootNode = new TicTacToeNode(state);
            MCTS mcts = new MCTS(rootNode);
            Node<TicTacToe> bestNode = mcts.searchIterations(1000);
            state = bestNode.state();
        }
        displayBoard(((TicTacToeState) state).position());
        return state;
    }

    public int opener() {
        return X;
    }

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

    public static void displayBoard(Position position) {
        String rendered = position.render();
        String[] rows = rendered.split("\n");
        int numCols = rows[0].split(" ").length;
        System.out.print("    ");
        for (int j = 0; j < numCols; j++) {
            System.out.print(j + "   ");
        }
        System.out.println();
        System.out.print("  ");
        for (int j = 0; j < numCols; j++) {
            System.out.print("----");
        }
        System.out.println();
        for (int i = 0; i < rows.length; i++) {
            System.out.printf("%d | ", i);
            String[] cells = rows[i].split(" ");
            for (String cell : cells) {
                System.out.printf("%-3s ", cell);
            }
            System.out.println();
        }
    }

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

        public int[] move() {
            return new int[]{i, j};
        }
    }

    class TicTacToeState implements State<TicTacToe> {
        private final Position position;

        public TicTacToeState() {
            this.position = startingPosition();
        }

        public TicTacToeState(Position position) {
            this.position = position;
        }

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
