# DSAIPG FINAL PROJECT

## Introduction and Provenance

This repository is a fork of the companion repository to:
"Data Structures, Algorithms, and Invariants--A Practical Guide"
by Robin Hillyard, College of Engineering, Northeastern University, Boston, MA, USA.
Published by Cognella.

The only difference can be found in the projects package, where you will find an implementation of the games TicTacToe and Othello using Monte Carlo Tree Search.


## Collaborators

This project was implemented by [Pranav Bhoopal](bhoopal.p@northeastern.edu) and [Dany Sigha](nohonetsigha.d@northeastern.edu).

## Installation

The repository is designed to be cloned from https://github.com/pranavb1924/PSA-PROJECT.git

The Java repository contains a Maven project (see the pom.xml file in the top level). Ideally, you will use an IDE that is suited to Maven projects. I recommend IntelliJ IDEA for Java work.


## Navigation
The simplest way to find code is just to use the `Navigate/Class` menu.

You want to look inside the following directory

`DSAIPG-editionFirst/src/main/java/com/phasmidsoftware/dsaipg/projects/mcts`

The classes introduced for the purpose of this project are listed bellow:


```
main/
  java/
    com/phasmidsoftware/dsaipg/
      projects/
        mcts/
          core/
            Game.java
            MCTSStatistics.java
            Move.java
            Node.java
            RandomState.java
            State.java
          othello/
            MCTS.java
            Othello.java
            OthelloNode.java
            OthelloState.java
            Position.java
          tictactoe/
            MCTS.java
            Position.java
            TicTacToe.java
            TicTacToeNode.java
            TicTacToeState.java
test/
  java/
    com/phasmidsoftware/dsaipg/
      projects/
        mcts/
          core/
            RandomStateTest.java
          othello/
            MCTSTest.java
            OthelloNodeTest.java
            OthelloStateTest.java
            OthelloTest.java
            PositionTest.java
          tictactoe/
            MCTSTest.java
            PositionTest.java
            TicTacToeNodeTest.java
            TicTacToeTest.java
```


## Rules of Othello

For the uninitiated, it is worthwile taking some time to refer to the [MATTEL OTHELLO RULES](https://service.mattel.com/instruction_sheets/T8130-Eng.pdf).


## Building and Testing

If you have cloned (or forked) the repository into IDEA, it should build the project
for you without much intervention on your part.
You will need at least Java 17 as your SDK.
Recommended: Oracle OpenJDK 18.0.2


### Running a game of TicTacToe

1. Navigate to the `TicTacToe` class (please refer to Navigation) and run the main function

2. Select a game mode

    ```
    Select game mode:
    1. Human vs. Computer
    2. Computer vs. Computer
    3. Benchmark MCTS Performance
    Enter 1, 2, or 3: 
    ```

3. Enter the number of games to run and the number of iterations within each game (number of simulations to find the next best move)
    ```
    Enter number of games to run per iteration setting (default 10): 
    Enter iterations to test (comma-separated, e.g. 100,500,1000):
    ```

4. If you choose to play against the computer, you will be prompted to enter your next move as a comma separated value

    ```
    Starting Tic Tac Toe game
    You are playing as X (first player)
    . . .
    . . .
    . . .
    Player X's turn.
    Enter your move (row,col):
    ```


### Running a game of Othello

1. Navigate to the `Othello` class (please refer to Navigation) and run the main function

2. Select a game mode

    ```
    Select game mode:
    1. Human vs. Computer
    2. Computer vs. Computer
    3. Benchmark MCTS Performance
    Enter 1, 2, or 3: 
    ```

3. Enter the number of games to run and the number of iterations within each game (number of simulations to find the next best move)
    ```
    Enter number of games to run per iteration setting (default 5): 
    Enter iterations to test (comma-separated, e.g. 10,50,100): 
    ```

4. If you choose to play against the computer, you will be prompted to enter your next move as a comma separated value
    
    ```
    Starting Othello game

    You are playing as Black (B), computer is White (W)
    Enter moves as 'row,col' (e.g., '3,4')

      0 1 2 3 4 5 6 7
    0 . . . . . . . . 
    1 . . . . . . . . 
    2 . . . . . . . . 
    3 . . . W B . . . 
    4 . . . B W . . . 
    5 . . . . . . . . 
    6 . . . . . . . . 
    7 . . . . . . . . 

    Black: 2, White: 2
    Player Black's turn.
    Enter your move (row,col):
    ``` 
