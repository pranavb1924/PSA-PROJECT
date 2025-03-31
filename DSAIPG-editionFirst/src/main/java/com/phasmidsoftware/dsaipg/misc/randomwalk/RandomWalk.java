/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc.randomwalk;

import java.util.Random;

/**
 * The RandomWalk class simulates a two-dimensional random walk. A "drunkard"
 * moves in a random direction for a specified number of steps, and the distance
 * from the starting point is measured. Additionally, multiple random walk
 * experiments can be performed to compute average distances.
 */
public class RandomWalk {

    /**
     * Method to compute the distance from the origin (the lamp-post where the drunkard starts) to his current position.
     *
     * @return the (Euclidean) distance from the origin to the current position.
     */
    public double distance() {
        // TO BE IMPLEMENTED

         return Math.sqrt( Math.pow(x, 2) + Math.pow(y, 2) );
        // END SOLUTION
    }

    /**
     * Private method to move the current position, that's to say the drunkard moves
     *
     * @param dx the distance he moves in the x direction
     * @param dy the distance he moves in the y direction
     */
    private void move(int dx, int dy) {
        // TO BE IMPLEMENTED  do move
        if(dx != 0)
            x += dx;

        if(dy != 0)
            y += dy;
//         throw new RuntimeException("Not implemented");
        // END SOLUTION
    }

    /**
     * Perform a random walk of m steps
     *
     * @param m the number of steps the drunkard takes
     */
    private void randomWalk(int m) {
        // TO BE IMPLEMENTED
        for(int i = 0; i < m; i++) {
            randomMove();
        }
//throw new RuntimeException("implementation missing");
    }

    /**
     * Private method to generate a random move according to the rules of the situation.
     * That's to say, moves can be (+-1, 0) or (0, +-1).
     */
    private void randomMove() {
        boolean ns = random.nextBoolean();
        int step = random.nextBoolean() ? 1 : -1;
        move(ns ? step : 0, ns ? 0 : step);
    }

    private int x = 0;
    private int y = 0;

    private final Random random = new Random();

    /**
     * Perform multiple random walk experiments, returning the mean distance.
     *
     * @param m the number of steps for each experiment
     * @param n the number of experiments to run
     * @return the mean distance
     */
    public static double randomWalkMulti(int m, int n) {
        double totalDistance = 0;
        for (int i = 0; i < n; i++) {
            RandomWalk walk = new RandomWalk();
            walk.randomWalk(m);
            totalDistance = totalDistance + walk.distance();
        }
        return totalDistance / n;
    }

    /**
     * The main method serves as the entry point to the RandomWalk program. It performs
     * either a single random walk experiment or several experiments, based on the
     * provided input arguments, and prints the mean distance.
     *
     * @param args command-line arguments where:
     *             args[0] specifies the number of steps for a random walk (required),
     *             and args[1] optionally specifies the number of experiments (default is 30).
     *             If args is empty, the method throws a RuntimeException indicating invalid syntax.
     */
    public static void main(String[] args) {
        if (args.length < 6)
            throw new RuntimeException("Syntax: RandomWalk steps_1 steps_2 steps_3 steps_4 steps_5 steps_6");
        int m1 = Integer.parseInt(args[0]);
        int m2 = Integer.parseInt(args[1]);
        int m3 = Integer.parseInt(args[2]);
        int m4 = Integer.parseInt(args[3]);
        int m5 = Integer.parseInt(args[4]);
        int m6 = Integer.parseInt(args[5]);

        int n = 10;
//        if (args.length > 1) n = Integer.parseInt(args[1]);

        double meanDistance1 = randomWalkMulti(m1, n);
        double meanDistance2 = randomWalkMulti(m2, n);
        double meanDistance3 = randomWalkMulti(m3, n);
        double meanDistance4 = randomWalkMulti(m4, n);
        double meanDistance5 = randomWalkMulti(m5, n);
        double meanDistance6 = randomWalkMulti(m6, n);

        System.out.println(m1 + " steps: " + meanDistance1 + " over " + n + " experiments");
        System.out.println(m2 + " steps: " + meanDistance2 + " over " + n + " experiments");
        System.out.println(m3 + " steps: " + meanDistance3 + " over " + n + " experiments");
        System.out.println(m4 + " steps: " + meanDistance4 + " over " + n + " experiments");
        System.out.println(m5 + " steps: " + meanDistance5 + " over " + n + " experiments");
        System.out.println(m6 + " steps: " + meanDistance6 + " over " + n + " experiments");
    }
}