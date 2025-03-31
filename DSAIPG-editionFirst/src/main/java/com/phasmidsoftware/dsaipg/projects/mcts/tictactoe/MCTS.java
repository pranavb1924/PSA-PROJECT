package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.ArrayList;
import java.util.List;

public class MCTS {

    private Node<TicTacToe> root;
    private double c = Math.sqrt(2);

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public Node<TicTacToe> searchIterations(int times) {
        if (root.state().isTerminal()) {
            return root;
        }

        for (int i = 0; i < times; i++) {
            List<Node<TicTacToe>> visited = new ArrayList<>();
            Node<TicTacToe> current = root;
            visited.add(current);

            while (!current.isLeaf() && !current.children().isEmpty()) {
                current = pickBest(current);
                visited.add(current);
            }

            if (!current.isLeaf() && current.children().isEmpty()) {
                current.explore();
                if (!current.children().isEmpty()) {
                    current = current.children().iterator().next();
                    visited.add(current);
                }
            }

            int result = playGame(current.state());

            for (Node<TicTacToe> n : visited) {
                addStats(n, result);
            }
        }

        return getMostPlayed(root);
    }

    private Node<TicTacToe> pickBest(Node<TicTacToe> node) {
        Node<TicTacToe> best = null;
        double bestScore = -99999;

        for (Node<TicTacToe> child : node.children()) {
            double winRate = (double) child.wins() / child.playouts();
            double uct = winRate + c * Math.sqrt(Math.log(node.playouts()) / child.playouts());

            if (uct > bestScore) {
                bestScore = uct;
                best = child;
            }
        }

        return best;
    }

    private int playGame(State<TicTacToe> state) {
        State<TicTacToe> s = state;

        while (!s.isTerminal()) {
            s = s.next(s.chooseMove(s.player()));
        }

        if (s.winner().isPresent()) {
            return 2;
        } else {
            return 1;
        }
    }

    private void addStats(Node<TicTacToe> node, int score) {
        if (node instanceof TicTacToeNode t) {
            t.incrementPlayouts();
            t.addWins(score);
        }
    }

    private Node<TicTacToe> getMostPlayed(Node<TicTacToe> node) {
        Node<TicTacToe> best = null;
        int most = -1;

        for (Node<TicTacToe> child : node.children()) {
            if (child.playouts() > most) {
                most = child.playouts();
                best = child;
            }
        }

        return best;
    }
}
