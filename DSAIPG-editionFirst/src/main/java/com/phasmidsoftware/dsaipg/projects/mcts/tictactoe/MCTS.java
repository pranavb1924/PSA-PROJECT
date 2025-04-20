package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class MCTS {

    private static final double EXPLORATION_PARAMETER = 1.0;

    private Consumer<String> logger;   // optional logger
    private final TicTacToeNode root;
    private final int rootPlayer;      // who moves at the root
    private final Random random;

    /**
     * Construct an MCTS using a given TicTacToeNode as root.
     */
    public MCTS(TicTacToeNode root) {
        this.root = root;
        this.rootPlayer = root.state().player();
        this.random = new Random();
    }

    /**
     * Set a logger for debugging
     */
    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
    }

    /**
     * The main MCTS method with direct threat detection.
     */
    public Node<TicTacToe> searchIterations(int iterations) {
        // First check if this is the root state
        TicTacToeState state = (TicTacToeState) root.state();
        Position position = state.getPosition();
        int player = state.player();
        
        // Parse the board to a 2D array for easier manipulation
        int[][] board = parseBoard(position);
        
        // Check for winning moves
        int[] winningMove = findWinningMove(board, player);
        if (winningMove != null) {
            if (logger != null) {
                logger.accept("Found winning move: " + winningMove[0] + "," + winningMove[1]);
            }
            return createMoveNode(winningMove[0], winningMove[1]);
        }
        
        // Check for blocking moves (where opponent would win)
        int[] blockingMove = findWinningMove(board, 1 - player);
        if (blockingMove != null) {
            if (logger != null) {
                logger.accept("Found blocking move: " + blockingMove[0] + "," + blockingMove[1]);
            }
            return createMoveNode(blockingMove[0], blockingMove[1]);
        }
        
        // No immediate threats, so perform standard MCTS
        if (root.children().isEmpty()) {
            expandNode(root);
        }
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            TicTacToeNode selected = select(root);
            double reward = simulate(selected);
            backpropagate(selected, reward);
        }

        long endTime = System.currentTimeMillis();
        if (logger != null) {
            logger.accept("MCTS completed " + iterations + " iterations in " + (endTime - startTime) + " ms.");
            logger.accept("Root has " + root.children().size() + " children.");
            
            for (Node<TicTacToe> c : root.children()) {
                TicTacToeNode child = (TicTacToeNode) c;
                logger.accept("Child at " + getMoveCoordinates(root, child) + 
                    ": wins=" + child.wins() + 
                    ", playouts=" + child.playouts() + 
                    ", win rate=" + (child.playouts() > 0 ? (double) child.wins() / child.playouts() : 0));
            }
        }
        
        // Return the best child by # of playouts
        TicTacToeNode bestChild = null;
        int bestVisits = -1;
        
        for (Node<TicTacToe> c : root.children()) {
            TicTacToeNode child = (TicTacToeNode) c;
            if (child.playouts() > bestVisits) {
                bestVisits = child.playouts();
                bestChild = child;
            }
        }
        
        if (bestChild == null) {
            return root;
        }
        
        return bestChild;
    }
    
    /**
     * Gets the move coordinates that led from root to child
     */
    private String getMoveCoordinates(TicTacToeNode root, TicTacToeNode child) {
        Position rootPos = ((TicTacToeState)root.state()).getPosition();
        Position childPos = ((TicTacToeState)child.state()).getPosition();
        
        int[][] rootBoard = parseBoard(rootPos);
        int[][] childBoard = parseBoard(childPos);
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (rootBoard[i][j] == Position.blank && childBoard[i][j] != Position.blank) {
                    return "(" + i + "," + j + ")";
                }
            }
        }
        
        return "unknown";
    }
    
    /**
     * Create a node representing a move at the given coordinates
     */
    private Node<TicTacToe> createMoveNode(int row, int col) {
        State<TicTacToe> state = root.state();
        int player = state.player();
        
        // Create the move
        Move<TicTacToe> move = new TicTacToe.TicTacToeMove(player, row, col);
        
        // Apply the move to get a new state
        State<TicTacToe> newState = state.next(move);
        
        // Create and return a node for this state
        return new TicTacToeNode(newState, move, (TicTacToeNode)root);
    }
    
    /**
     * Parse the board string to a 2D array for easier manipulation
     */
    private int[][] parseBoard(Position position) {
        int[][] board = new int[3][3];
        String[] rows = position.toString().split("\n");
        
        for (int i = 0; i < rows.length; i++) {
            String[] cells = rows[i].split(",");
            for (int j = 0; j < cells.length; j++) {
                board[i][j] = Integer.parseInt(cells[j]);
            }
        }
        
        return board;
    }
    
    /**
     * Find a winning move for the specified player on the given board.
     * Returns the coordinates of the winning move or null if none exists.
     */
    private int[] findWinningMove(int[][] board, int player) {
        // Check rows
        for (int i = 0; i < 3; i++) {
            int count = 0;
            int emptyCol = -1;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == player) count++;
                else if (board[i][j] == Position.blank) emptyCol = j;
            }
            if (count == 2 && emptyCol != -1) {
                return new int[]{i, emptyCol};
            }
        }
        
        // Check columns
        for (int j = 0; j < 3; j++) {
            int count = 0;
            int emptyRow = -1;
            for (int i = 0; i < 3; i++) {
                if (board[i][j] == player) count++;
                else if (board[i][j] == Position.blank) emptyRow = i;
            }
            if (count == 2 && emptyRow != -1) {
                return new int[]{emptyRow, j};
            }
        }
        
        // Check main diagonal (top-left to bottom-right)
        int count = 0;
        int emptyIndex = -1;
        for (int i = 0; i < 3; i++) {
            if (board[i][i] == player) count++;
            else if (board[i][i] == Position.blank) emptyIndex = i;
        }
        if (count == 2 && emptyIndex != -1) {
            return new int[]{emptyIndex, emptyIndex};
        }
        
        // Check anti-diagonal (top-right to bottom-left)
        count = 0;
        emptyIndex = -1;
        for (int i = 0; i < 3; i++) {
            int j = 2 - i;
            if (board[i][j] == player) count++;
            else if (board[i][j] == Position.blank) emptyIndex = i;
        }
        if (count == 2 && emptyIndex != -1) {
            return new int[]{emptyIndex, 2 - emptyIndex};
        }
        
        return null; // No winning move found
    }

    // Expand node by generating children from all possible moves
    private void expandNode(TicTacToeNode node) {
        State<TicTacToe> state = node.state();
        int player = state.player();
        Collection<Move<TicTacToe>> moves = state.moves(player);
        for (Move<TicTacToe> move : moves) {
            State<TicTacToe> newState = state.next(move);
            node.addChild(newState, move);
        }
        if (logger != null) {
            logger.accept("Expanded node: " + moves.size() + " children created.");
        }
    }

    // Use UCB to select a child node, recursing down until we reach a leaf
    private TicTacToeNode select(TicTacToeNode node) {
        if (node == null) {
            
            if (logger != null) logger.accept("Selection: received null node");
            return null;
        }
        
        if (node.isLeaf()) {
            if (logger != null) logger.accept("Selection: reached leaf node");
            return node;
        }
        
        if (node.children().isEmpty()) {
            // Expand this node and return one of its children
            expandNode(node);
            List<Node<TicTacToe>> children = new ArrayList<>(node.children());
            if (children.isEmpty()) {
                return node; // No valid moves, return this node
            }
            return (TicTacToeNode) children.get(random.nextInt(children.size()));
        }
        
        // Find best child by UCT
        TicTacToeNode bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (Node<TicTacToe> childNode : node.children()) {
            TicTacToeNode tChild = (TicTacToeNode) childNode;
            double score = ucbValue(tChild, node.playouts());
            if (score > bestScore) {
                bestScore = score;
                bestChild = tChild;
            }
        }

        if (bestChild == null) {
            if (logger != null) logger.accept("Selection: no best child found, returning current node");
            return node;
        }
        
        return select(bestChild);
    }

    private double ucbValue(TicTacToeNode child, int parentPlayouts) {
        if (child.playouts() == 0) {
            return Double.MAX_VALUE;  // Unexplored nodes have highest priority
        }
        
        // Normalize the win value properly
        double exploitation = (double) child.wins() / child.playouts();
        
        // UCB1 formula
        double exploration = EXPLORATION_PARAMETER * Math.sqrt(Math.log(parentPlayouts) / child.playouts());
        
        return exploitation + exploration;
    }

    // Enhanced simulation that prioritizes winning moves and blocking
    private double simulate(TicTacToeNode node) {
        State<TicTacToe> simState = node.state();
        int currentPlayer = simState.player();

        if (simState.isTerminal()) {
            // Evaluate immediately if terminal
            return evaluateTerminalState(simState);
        }
        
        // Simulate intelligently with some randomness
        while (!simState.isTerminal()) {
            Position position = ((TicTacToeState) simState).getPosition();
            int[][] board = parseBoard(position);
            
            // Try to find a winning move first
            int[] winningMove = findWinningMove(board, currentPlayer);
            
            if (winningMove != null) {
                // Found a winning move - take it
                Move<TicTacToe> move = new TicTacToe.TicTacToeMove(
                        currentPlayer, winningMove[0], winningMove[1]);
                simState = simState.next(move);
            } else {
                // Try to block opponent's winning move
                int[] blockingMove = findWinningMove(board, 1 - currentPlayer);
                if (blockingMove != null) {
                    Move<TicTacToe> move = new TicTacToe.TicTacToeMove(
                            currentPlayer, blockingMove[0], blockingMove[1]);
                    simState = simState.next(move);
                } else {
                    // No critical moves, pick randomly
                    Collection<Move<TicTacToe>> moves = simState.moves(currentPlayer);
                    if (moves.isEmpty()) break; // no moves
                    Move<TicTacToe> move = getRandomMove(moves);
                    simState = simState.next(move);
                }
            }
            
            currentPlayer = 1 - currentPlayer;
        }
        
        return evaluateTerminalState(simState);
    }

    // Evaluate the terminal state from the perspective of the root player
    private double evaluateTerminalState(State<TicTacToe> state) {
        Optional<Integer> winner = state.winner();
        
        if (winner.isPresent()) {
            int winningPlayer = winner.get();
            return (winningPlayer == rootPlayer) ? 1.0 : 0.0;
        } else {
            // Draw
            return 0.5;
        }
    }

    private Move<TicTacToe> getRandomMove(Collection<Move<TicTacToe>> moves) {
        List<Move<TicTacToe>> list = new ArrayList<>(moves);
        return list.get(random.nextInt(list.size()));
    }

    // Backpropagate the reward up the tree
    private void backpropagate(TicTacToeNode node, double reward) {
        TicTacToeNode current = node;
        double currentReward = reward;
        
        while (current != null) {
            current.updateStats(currentReward);
            current = current.getParent();
            currentReward = 1.0 - currentReward; // Flip the reward for the parent
        }
    }
}