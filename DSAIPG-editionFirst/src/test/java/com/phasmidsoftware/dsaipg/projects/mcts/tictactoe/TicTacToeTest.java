//package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;
//
//import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
//import org.junit.Test;
//
//import java.util.Optional;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
//public class TicTacToeTest {
//
//    /**
//     *
//     */
//    @Test
//    public void runGame() {
//        long seed = 0L;
//        TicTacToe target = new TicTacToe(seed); // games run here will all be deterministic.
//        State<TicTacToe> state = target.runGame();
//        Optional<Integer> winner = state.winner();
//        if (winner.isPresent()) assertEquals(Integer.valueOf(TicTacToe.X), winner.get());
//        else fail("no winner");
//    }
//}

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicTacToeTest {

    /**
     * Test a specific game scenario with a horizontal win
     */
    @Test
    public void testSpecificGameScenario() {
        TicTacToe game = new TicTacToe(0L); // Fixed seed
        State<TicTacToe> state = game.start();

        // Make a series of predetermined moves for a horizontal win
        // X plays top-left
        state = state.next(new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 0));

        // O plays center
        state = state.next(new TicTacToe.TicTacToeMove(TicTacToe.O, 1, 1));

        // X plays top-middle
        state = state.next(new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 1));

        // O plays bottom-right
        state = state.next(new TicTacToe.TicTacToeMove(TicTacToe.O, 2, 2));

        // X plays top-right to win horizontally
        state = state.next(new TicTacToe.TicTacToeMove(TicTacToe.X, 0, 2));

        // Print the final board state for debugging
        System.out.println("Final board state:");
        System.out.println(((TicTacToeState)state).getPosition().render());

        // Check if X won
        Optional<Integer> winner = state.winner();
        assertTrue("Game should be over", state.isTerminal());
        assertTrue("X should win with horizontal strategy", winner.isPresent());
        assertEquals("X should be the winner", Integer.valueOf(TicTacToe.X), winner.get());
    }
}