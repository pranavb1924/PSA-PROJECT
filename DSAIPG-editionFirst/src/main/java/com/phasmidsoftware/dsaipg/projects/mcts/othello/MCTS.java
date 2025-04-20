package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Enhanced Monte Carlo Tree Search for Othello with strategic heuristics.
 */
public class MCTS {

    private static final double EXPLORATION_PARAMETER = 1.0;
    
    // Board position weights for evaluation
    private static final int[][] POSITION_WEIGHTS = {
        {100, -20, 10, 5, 5, 10, -20, 100},
        {-20, -50, -2, -2, -2, -2, -50, -20},
        {10, -2, 1, 1, 1, 1, -2, 10},
        {5, -2, 1, 1, 1, 1, -2, 5},
        {5, -2, 1, 1, 1, 1, -2, 5},
        {10, -2, 1, 1, 1, 1, -2, 10},
        {-20, -50, -2, -2, -2, -2, -50, -20},
        {100, -20, 10, 5, 5, 10, -20, 100}
    };

    private Consumer<String> logger;
    private final OthelloNode root;
    private final int rootPlayer;
    private final Random random;

    /**
     * Construct an MCTS for Othello
     */
    public MCTS(OthelloNode root) {
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
     * The main MCTS search with strategic enhancements for Othello
     */
    public Node<Othello> searchIterations(int iterations) {
        // Check for strategic moves first
        OthelloState state = (OthelloState) root.state();
        Position position = state.getPosition();
        int player = state.player();
        
        // Get the available moves for the player
        List<int[]> availableMoves = position.moves(player);
        
        // Check if any corner moves are available
        Move<Othello> cornerMove = findCornerMove(position, player, availableMoves);
        if (cornerMove != null) {
            if (logger != null) {
                logger.accept("Found strategic corner move");
            }
            return createMoveNode(cornerMove);
        }
        
        // Expand root if needed
        if (root.children().isEmpty()) {
            expandNode(root);
        }
        
        // Standard MCTS iterations
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            OthelloNode selected = select(root);
            double reward = simulate(selected);
            backpropagate(selected, reward);
        }
        long endTime = System.currentTimeMillis();
        
        if (logger != null) {
            logger.accept("MCTS completed " + iterations + " iterations in " + (endTime - startTime) + " ms.");
            logger.accept("Root has " + root.children().size() + " children.");
            
            // Log statistics for top children
            List<OthelloNode> sortedChildren = new ArrayList<>();
            for (Node<Othello> c : root.children()) {
                sortedChildren.add((OthelloNode) c);
            }
            
            // Sort by number of playouts (descending)
            sortedChildren.sort((a, b) -> Integer.compare(b.playouts(), a.playouts()));
            int count = Math.min(5, sortedChildren.size()); // Log top 5 moves
            
            for (int i = 0; i < count; i++) {
                OthelloNode child = sortedChildren.get(i);
                logger.accept("Child " + i + ": playouts=" + child.playouts() + 
                    ", wins=" + child.wins() + 
                    ", win rate=" + (child.playouts() > 0 ? (double) child.wins() / child.playouts() : 0));
            }
        }
        
        // Return best child by playouts
        return findBestChild();
    }
    
    /**
     * Find the best child based on number of playouts
     */
    private OthelloNode findBestChild() {
        OthelloNode bestChild = null;
        int bestVisits = -1;
        
        for (Node<Othello> c : root.children()) {
            OthelloNode child = (OthelloNode) c;
            if (child.playouts() > bestVisits) {
                bestVisits = child.playouts();
                bestChild = child;
            }
        }
        
        return bestChild != null ? bestChild : root;
    }
    
    /**
     * Find a move that captures a corner if available
     * Only checks corners from the list of available moves
     */
    private Move<Othello> findCornerMove(Position position, int player, List<int[]> availableMoves) {
        // These are the corner coordinates
        int[][] corners = {{0,0}, {0,7}, {7,0}, {7,7}};
        
        // Check if any of the available moves are corner moves
        for (int[] move : availableMoves) {
            for (int[] corner : corners) {
                if (move[0] == corner[0] && move[1] == corner[1]) {
                    // This is a valid corner move
                    return new Othello.OthelloMove(player, move[0], move[1]);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get pieces that would be flipped with a move at the given position
     */
    private List<int[]> getFlipsInPosition(Position position, int player, int row, int col) {
        List<int[]> flips = new ArrayList<>();
        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
        };
        
        int opponent = 1 - player;
        int size = Position.getSize();
        int[][] board = getBoardArray(position);
        
        // First check if cell is empty
        if (board[row][col] != Position.EMPTY) {
            return flips; // Return empty list if cell is occupied
        }
        
        // Check each direction
        for (int[] dir : directions) {
            List<int[]> dirFlips = new ArrayList<>();
            int r = row + dir[0];
            int c = col + dir[1];
            
            // Continue in this direction as long as we find opponent's pieces
            while (r >= 0 && r < size && c >= 0 && c < size && board[r][c] == opponent) {
                dirFlips.add(new int[]{r, c});
                r += dir[0];
                c += dir[1];
            }
            
            // If we hit our own piece, all opponent pieces in between get flipped
            if (r >= 0 && r < size && c >= 0 && c < size && board[r][c] == player && !dirFlips.isEmpty()) {
                flips.addAll(dirFlips);
            }
        }
        
        return flips;
    }
    
    /**
     * Create a node representing this move
     */
    private Node<Othello> createMoveNode(Move<Othello> move) {
        State<Othello> state = root.state();
        try {
            State<Othello> newState = state.next(move);
            return new OthelloNode(newState, move, root);
        } catch (Exception e) {
            if (logger != null) {
                logger.accept("Error creating move node: " + e.getMessage());
            }
            // If there's an error, just return the root node
            return root;
        }
    }
    
    /**
     * Get the board as a 2D array for easier manipulation
     */
    private int[][] getBoardArray(Position position) {
        return position.getBoard();
    }

    // Expand node by generating children from all possible moves
    private void expandNode(OthelloNode node) {
        State<Othello> state = node.state();
        int player = state.player();
        
        try {
            Collection<Move<Othello>> moves = state.moves(player);
            
            for (Move<Othello> move : moves) {
                State<Othello> newState = state.next(move);
                node.addChild(newState, move);
            }
            
            if (logger != null) {
                logger.accept("Expanded node: " + moves.size() + " children created.");
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.accept("Error expanding node: " + e.getMessage());
            }
        }
    }

    // Use UCB to select a child node, recursing down until we reach a leaf
    private OthelloNode select(OthelloNode node) {
        // Fix #1: Add null check at the beginning
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
            List<Node<Othello>> children = new ArrayList<>(node.children());
            if (children.isEmpty()) {
                return node; // No valid moves, return this node
            }
            return (OthelloNode) children.get(random.nextInt(children.size()));
        }
        
        // Find best child by UCT
        OthelloNode bestChild = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (Node<Othello> childNode : node.children()) {
            OthelloNode oChild = (OthelloNode) childNode;
            double score = ucbValue(oChild, node.playouts());
            if (score > bestScore) {
                bestScore = score;
                bestChild = oChild;
            }
        }
        
        // Fix #2: Check if bestChild is null before recursing
        if (bestChild == null) {
            if (logger != null) logger.accept("Selection: no best child found, returning current node");
            return node;
        }
        
        return select(bestChild);
    }

    private double ucbValue(OthelloNode child, int parentPlayouts) {
        if (child.playouts() == 0) {
            return Double.MAX_VALUE;  // Unexplored nodes get priority
        }
        
        // Normalize win rate
        double exploitation = (double) child.wins() / child.playouts();
        
        // UCB1 formula
        double exploration = EXPLORATION_PARAMETER * Math.sqrt(Math.log(parentPlayouts) / child.playouts());
        
        return exploitation + exploration;
    }

    // Enhanced simulation with strategic play - but without using moves() from the State
    private double simulate(OthelloNode node) {
        // Create a copy of the state for simulation
        OthelloState originalState = (OthelloState) node.state();
        Position originalPosition = originalState.getPosition();
        
        if (originalState.isTerminal()) {
            return evaluateTerminalState(originalState);
        }
        
        // We'll use the Position directly for simulation, bypassing the OthelloState turn validation
        Position currentPosition = originalPosition;
        int currentPlayer = originalState.player();
        int depth = 0;
        int maxDepth = 30; // Limit simulation depth
        
        while (!currentPosition.isGameOver() && depth < maxDepth) {
            // Get valid moves for the current player directly from the Position
            List<int[]> availableMoves = currentPosition.moves(currentPlayer);
            
            if (availableMoves.isEmpty()) {
                // No moves available, switch players
                currentPlayer = 1 - currentPlayer;
                continue;
            }
            
            // Choose a move using our heuristics
            int[] move;
            if (random.nextDouble() < 0.9) {
                move = selectHeuristicMoveCoords(currentPosition, availableMoves, currentPlayer);
            } else {
                move = availableMoves.get(random.nextInt(availableMoves.size()));
            }
            
            // Apply the move directly to the Position
            try {
                currentPosition = currentPosition.move(currentPlayer, move[0], move[1]);
                // Switch players
                currentPlayer = 1 - currentPlayer;
                depth++;
            } catch (Exception e) {
                // Invalid move, switch players
                currentPlayer = 1 - currentPlayer;
            }
        }
        
        // If we hit max depth or the game is over, evaluate the position
        if (depth >= maxDepth) {
            return evaluatePositionHeuristically(currentPosition);
        } else {
            // Game is over, evaluate the terminal position
            return evaluateTerminalPosition(currentPosition);
        }
    }
    
    /**
     * Select move coordinates based on heuristics
     */
    private int[] selectHeuristicMoveCoords(Position position, List<int[]> moves, int player) {
        if (moves.isEmpty()) {
            throw new IllegalArgumentException("No moves available");
        }
        
        // Default to first move
        int[] bestMove = moves.get(0);
        double bestScore = Double.NEGATIVE_INFINITY;
        
        for (int[] move : moves) {
            double score = evaluateMoveHeuristically(position, move, player);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
    
    /**
     * Evaluate a potential move using heuristics
     */
    private double evaluateMoveHeuristically(Position position, int[] move, int player) {
        int row = move[0];
        int col = move[1];
        
        // Start with position weight
        double score = POSITION_WEIGHTS[row][col];
        
        // Prefer corners
        if ((row == 0 || row == 7) && (col == 0 || col == 7)) {
            score += 1000;
        }
        
        // Avoid squares next to corners if we don't control the corner
        if ((row <= 1 || row >= 6) && (col <= 1 || col >= 6)) {
            // Dangerous squares near corners
            if (row <= 1 && col <= 1 && position.getBoard()[0][0] == Position.EMPTY) score -= 100;
            if (row <= 1 && col >= 6 && position.getBoard()[0][7] == Position.EMPTY) score -= 100;
            if (row >= 6 && col <= 1 && position.getBoard()[7][0] == Position.EMPTY) score -= 100;
            if (row >= 6 && col >= 6 && position.getBoard()[7][7] == Position.EMPTY) score -= 100;
        }
        
        // Evaluate the resulting position
        try {
            Position newPosition = position.move(player, row, col);
            
            // Consider piece difference after the move
            int ownPieces = newPosition.countPieces(player);
            int opponentPieces = newPosition.countPieces(1 - player);
            score += (ownPieces - opponentPieces) * 0.5;
            
            // Consider mobility (number of moves available to opponent after this move)
            int opponentMoves = newPosition.moves(1 - player).size();
            score -= opponentMoves * 2; // Prefer moves that limit opponent mobility
        } catch (Exception e) {
            // Invalid move, assign low score
            return Double.NEGATIVE_INFINITY;
        }
        
        return score;
    }
    
    /**
     * Evaluate a position heuristically
     */
    private double evaluatePositionHeuristically(Position position) {
        int myCount = position.countPieces(rootPlayer);
        int oppCount = position.countPieces(1 - rootPlayer);
        
        double pieceDiffRatio = (myCount + oppCount == 0) ? 0.5 :
            (double) myCount / (myCount + oppCount);
        
        // Evaluate position value
        double positionValue = 0;
        double totalWeight = 0;
        int[][] board = position.getBoard();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == rootPlayer) {
                    positionValue += POSITION_WEIGHTS[i][j];
                } else if (board[i][j] == (1 - rootPlayer)) {
                    positionValue -= POSITION_WEIGHTS[i][j];
                }
                totalWeight += Math.abs(POSITION_WEIGHTS[i][j]);
            }
        }
        
        // Normalize position value
        double positionRatio = (positionValue + totalWeight) / (2 * totalWeight);
        
        // Combined evaluation
        return 0.7 * positionRatio + 0.3 * pieceDiffRatio;
    }
    
    /**
     * Evaluate a terminal position
     */
    private double evaluateTerminalPosition(Position position) {
        int myCount = position.countPieces(rootPlayer);
        int oppCount = position.countPieces(1 - rootPlayer);
        
        if (myCount > oppCount) return 1.0;
        if (oppCount > myCount) return 0.0;
        return 0.5; // Draw
    }

    // Evaluate a terminal state
    private double evaluateTerminalState(State<Othello> state) {
        Optional<Integer> winner = state.winner();
        
        if (winner.isPresent()) {
            int winningPlayer = winner.get();
            return (winningPlayer == rootPlayer) ? 1.0 : 0.0;
        } else {
            // Draw (very rare in Othello, but possible)
            return 0.5;
        }
    }

    // Backpropagate the reward up the tree
    private void backpropagate(OthelloNode node, double reward) {
        OthelloNode current = node;
        double currentReward = reward;
        
        while (current != null) {
            current.updateStats(currentReward);
            current = current.getParent();
            currentReward = 1.0 - currentReward; // Flip the reward
        }
    }
}