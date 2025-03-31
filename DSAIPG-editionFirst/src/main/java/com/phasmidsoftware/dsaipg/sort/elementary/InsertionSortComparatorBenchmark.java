/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.HelperFactory;
import com.phasmidsoftware.dsaipg.sort.Sort;
import com.phasmidsoftware.dsaipg.sort.SortWithHelper;
import com.phasmidsoftware.dsaipg.util.Config;
import com.phasmidsoftware.dsaipg.util.Config_Benchmark;

import java.io.IOException;
import java.util.Comparator;

import static com.phasmidsoftware.dsaipg.sort.InstrumentedComparatorHelper.getRunsConfig;
import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.setupConfigFixes;


import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.TimeLogger;
import com.phasmidsoftware.dsaipg.util.Utilities;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * The ThreeSumBenchmark class provides a framework for evaluating and comparing
 * the performance of various implementations of the Three-Sum algorithm. It supports
 * benchmarking for cubic, quadratic, and quadrithmic implementations and logs the
 * runtime results for analysis.
 * <p>
 * Benchmarks are executed on randomly generated input arrays, with configurable parameters
 * like the number of runs, input size, and value range. The results are normalized
 * according to theoretical time complexities (e.g., n^3, n^2 log n, n^2) for better
 * interpretability.
 */

public class InsertionSortComparatorBenchmark {
    /**
     * Constructs an InsertionSortComparatorBenchmark instance to facilitate the performance evaluation
     * of an implementation of the Insertion Sort algorithm.
     *
     * @param runs the number of benchmark runs to perform. Represents how many
     *             times the benchmark will be executed to ensure consistency.
     * @param n    the size of the input array to the InsertionSort algorithm. Represents
     *             the number of integers in the generated array for testing.
     * @param m    the range of integers in the input array, where each integer lies within
     *             the range of -m through m-1. Determines the distribution of values in
     *             the generated test arrays.
     */
    public InsertionSortComparatorBenchmark(int runs, int n, int m) {
        this.runs = runs;
        this.supplier = new Source(n, m).intsSupplier(10);
        this.n = n;
    }

    /**
     * Executes a series of performance benchmarks to evaluate the runtime efficiency
     * of various implementations of the Three-Sum algorithm. The benchmarks are executed
     * for three approaches: cubic, quadratic, and quadrithmic, with results logged for
     * analysis purposes.
     * <p>
     * The method performs the following tasks:
     * 1. Logs the problem size `n` being tested.
     * 2. Benchmarks the "ThreeSumQuadratic" implementation using its corresponding
     *    algorithm and time logger.
     * 3. Benchmarks the "ThreeSumQuadrithmic" implementation with its respective
     *    algorithm and time logger.
     * 4. Benchmarks the "ThreeSumCubic" implementation, but skips execution if the
     *    problem size exceeds a predefined limit (e.g., for scalability reasons).
     * <p>
     * Each benchmark internally utilizes a supplier to generate input arrays, and
     * applies a predefined number of runs to obtain averaged runtime measures.
     */
    public void runBenchmarks() {
        System.out.println("InsertionSortComparatorBenchmark: N=" + n);

//        benchmarkThreeSum("InsertionSortComparatorBenchmark", (xs) -> new ThreeSumQuadratic(xs).getTriples(), n, timeLoggersQuadratic);

        Comparator<Integer> comparator = Integer::compareTo;

        benchmarkInsertionSortComparator("InsertionSortComparatorBenchmark", (xs) -> {
            Helper<Integer> helper = HelperFactory.createGeneric("Test", comparator, xs.length, 1, setupConfigFixes());
            InsertionSortComparator<Integer> sorter = new InsertionSortComparator<>(helper);
            sorter.sort(xs, 0, xs.length);
        } , n, timeLoggers);
    }

    /**
     * The main method serves as the entry point to the application. It sequentially executes performance
     * benchmarks for various configurations of the Three-Sum problem. Each configuration specifies
     * the number of runs, the input size, and the range of randomly generated integers for the Three-Sum algorithm.
     * <p>
     * The benchmarks are designed to evaluate and log the performance of different algorithmic implementations:
     * cubic, quadratic, and quadrithmic.
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(100, 250, 250).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(50, 500, 500).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(20, 1000, 1000).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(10, 2000, 2000).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(5, 4000, 4000).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(3, 8000, 8000).runBenchmarks();
        new com.phasmidsoftware.dsaipg.sort.elementary.InsertionSortComparatorBenchmark(2, 16000, 16000).runBenchmarks();
    }


    /**
     * Benchmarks the performance of a specified Three-Sum algorithm implementation
     * using a provided function, input size, and time loggers for result recording.
     * CONSIDER redefining function as an instance of ThreeSum.
     *
     * @param description  a textual description of the Three-Sum algorithm being benchmarked.
     *                     Used for identification and logging purposes.
     * @param function     the specific implementation of the Three-Sum algorithm to test.
     *                     Encapsulated as a Consumer accepting an array of integers as input.
     * @param n            the size of the input array to generate and test the algorithm with.
     * @param timeLoggers  an array of TimeLogger instances responsible for logging the performance
     *                     results of the benchmark.
     */
    private void benchmarkInsertionSortComparator(final String description, final Consumer<Integer[]> function, int n, final TimeLogger[] timeLoggers) {
        if (description.equals("Insertion Sort") && n > 4000) return;
        // TO BE IMPLEMENTED
        Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>(
                description,
                null,              // No pre-function needed as we're using a supplier
                function,          // The TwoSum implementation to benchmark
                null              // No post-function needed
        );

        // Run the benchmark using our supplier and get the average time
        double time = timer.runFromSupplier(supplier, runs);

        // Log the results using both time loggers
        // First logger shows raw time, second shows normalized time
        for (TimeLogger timeLogger : timeLoggers) {
            timeLogger.log(description, time, n);
        }
    }


    /**
     * Represents an array of {@code TimeLogger} instances specifically used for logging
     * performance metrics of the quadratic implementation of the Insertion Sort algorithm.
     * Each {@code TimeLogger} in this array performs a distinct type of logging:
     * 1. Logs the raw execution time (milliseconds) per run.
     * 2. Logs the normalized execution time, accounting for the quadratic growth factor (n^2).
     * This constant is immutable and statically defined, providing reusable time
     * logging mechanisms for quadratic benchmark tests within the {@code InsertionSortComparatorBenchmark} class.
     */
    private final static TimeLogger[] timeLoggers = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^2 log n): ", n -> n * n * Utilities.lg(n))
    };



    private final int runs;
    private final Supplier<Integer[]> supplier;
    private final int n;
}



