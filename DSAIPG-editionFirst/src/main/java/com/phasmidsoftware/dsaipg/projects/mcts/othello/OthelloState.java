package com.phasmidsoftware.dsaipg.projects.mcts.othello;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

public class OthelloState implements State<Othello> {

    private final Othello game;
    private final Position position;

    public OthelloState(Position position) {
        this.game = new Othello();
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public Othello game() {
        return game;
    }

    @Override
    public boolean isTerminal() {
        // Game is over if the board is full or neither player can make a move
        return position.isGameOver();
    }

    @Override
    public int player() {
        int lastPlayer = position.lastPlayer();
        // If no one has moved yet, or if the last player was white, then it's black's turn
        // If the last player was blank (game start), black goes first
        if (lastPlayer == Othello.WHITE || lastPlayer == Othello.EMPTY) {
            return Othello.BLACK;
        } else {
            return Othello.WHITE;
        }
    }

    @Override
    public Optional<Integer> winner() {
        return position.winner();
    }

    @Override
    public Random random() {
        return game.getRandom();
    }

    @Override
    public Collection<Move<Othello>> moves(int player) {
        Collection<int[]> coords;
        
        // Allow the requested player (fix the error when choosing a random move)
        coords = position.moves(player);
        
        // If the requested player has no moves, we need to handle this specially
        if (coords.isEmpty()) {
            // No moves for the requested player - this is a valid pass situation
            // Return an empty collection to indicate no moves are available
            return new ArrayList<>();
        }
        
        // Convert coordinates to moves
        Collection<Move<Othello>> moves = new ArrayList<>();
        for (int[] rc : coords) {
            moves.add(new Othello.OthelloMove(player, rc[0], rc[1]));
        }
        
        return moves;
    }

    @Override
    public State<Othello> next(Move<Othello> move) {
        if (!(move instanceof Othello.OthelloMove othelloMove)) {
            throw new IllegalArgumentException("Expected OthelloMove");
        }
        
        int[] rc = othelloMove.move();
        try {
            Position newPos = position.move(move.player(), rc[0], rc[1]);
            return new OthelloState(newPos);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Invalid move: " + e.getMessage());
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OthelloState)) return false;
        OthelloState that = (OthelloState) o;
        return position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}