/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.pq;
import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.TimeLogger;
import com.phasmidsoftware.dsaipg.util.Utilities;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Supplier;
/**
 * The PriorityQueueBenchmark class provides a framework for evaluating and comparing
 * the performance of various implementations of the Heap algorithm. It supports
 * benchmarking for the Basic binary heap and the 4-ary heap with and without Floyd's
 * sink-to-bottom trick and logs the runtime results for analysis.
 * <p>
 * Benchmarks are executed on randomly generated input elements, with configurable parameters
 * like the number of runs, input size, and value range. The results are normalized
 * according to theoretical time complexities (e.g., n^3, n^2 log n, n^2) for better
 * interpretability.
 */
public class PriorityQueueBenchmark {
    /**
     * Constructs an PriorityQueueBenchmark instance to facilitate the performance evaluation
     * of an implementation of the Heap algorithm.
     *
     * @param runs the number of benchmark runs to perform. Represents how many
     *             times the benchmark will be executed to ensure consistency.
     * @param numberOfElementsToInsert    the size of the input array to the Heap algorithm. Represents
     *             the number of integers in the generated array for testing.
     * @param rangeOfValues    the range of integers in the input array, where each integer lies within
     *             the range of -m through m-1. Determines the distribution of values in
     *             the generated test arrays.
     * @param numberOfElementsToRemove    the number of elements to remove from the heap as we test the algorithm.
     */
    public PriorityQueueBenchmark(int runs, int numberOfElementsToInsert, int rangeOfValues, int sizeOfHeap, int numberOfElementsToRemove) {
        this.runs = runs;
        this.supplier = new Source(numberOfElementsToInsert, rangeOfValues).intsSupplier();
        this.numberOfElementsToInsert = numberOfElementsToInsert;
        this.numberOfElementsToRemove = numberOfElementsToRemove;
        this.sizeOfHeap = sizeOfHeap;
    }

    /**
     * Executes a series of performance benchmarks to evaluate the runtime efficiency
     * of various implementations of the Heap algorithm. The benchmarks are executed
     * for one approach: logarithmic, with results logged for
     * analysis purposes.
     * <p>
     * The method performs the following tasks:
     * 1. Logs the problem size `n` being tested.
     * 2. Benchmarks the "Basic binary heap" implementation using its corresponding
     *    algorithm and time logger.
     * 3. Benchmarks the "Basic binary heap with Floyd's sink-to-bottom trick" implementation
     *    with its respective algorithm and time logger.
     * 4. Benchmarks the "4-ary heap" implementation
     * 5. Benchmarks the "4-ary heap with Floyd's sink-to-bottom trick" implementation
     * <p>
     * Each benchmark internally utilizes a supplier to generate input arrays, and
     * applies a predefined number of runs to obtain averaged runtime measures.
     */
    public void runBenchmarks() {
        Comparator<Integer> comparator = Integer::compareTo;

        System.out.println("PriorityQueueBenchmark: N=" + numberOfElementsToInsert);
//        benchmarkPriorityQueue("Basic binary heap", (xs) -> new PriorityQueue(true, xs, 0, 0, comparator, false), n, timeLoggersQuadratic);

        benchmarkPriorityQueue("Basic binary heap", (xs) -> {
            PriorityQueue<Integer> pq = new PriorityQueue(sizeOfHeap, true, comparator, false);
            for(Integer i : xs) {
                pq.give(i);
            }
            for(int i = 0; i < numberOfElementsToRemove; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
        } , numberOfElementsToInsert, timeLoggersHeap);

        benchmarkPriorityQueue("Basic binary heap with Floyd's sink-to-bottom trick", (xs) -> {
            PriorityQueue<Integer> pq = new PriorityQueue(sizeOfHeap, true, comparator, true);
            for(Integer i : xs) {
                pq.give(i);
            }
            for(int i = 0; i < numberOfElementsToRemove; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
        } , numberOfElementsToInsert, timeLoggersHeap);

        benchmarkPriorityQueue("4-ary heap", (xs) -> {
            PriorityQueue<Integer> pq = new PriorityQueue(sizeOfHeap, true, comparator, false);
            for(Integer i : xs) {
                pq.give(i);
            }
            for(int i = 0; i < numberOfElementsToRemove; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
        } , numberOfElementsToInsert, timeLoggersHeap);

        benchmarkPriorityQueue("4-ary heap with Floyd's sink-to-bottom trick", (xs) -> {
            PriorityQueue<Integer> pq = new PriorityQueue(sizeOfHeap, true, comparator, true);
            for(Integer i : xs) {
                pq.give(i);
            }
            for(int i = 0; i < numberOfElementsToRemove; i++) {
                try {
                    pq.take();
                } catch (PQException e) {
                    throw new RuntimeException(e);
                }
            }
        } , numberOfElementsToInsert, timeLoggersHeap);

//        benchmarkPriorityQueueBenchmark("4-ary heap", (xs) -> new ThreeSumCubic(xs).getTriples(), n, timeLoggersCubic);
//        benchmarkPriorityQueueBenchmark("4-ary heap with Floyd's sink-to-bottom trick", (xs) -> new ThreeSumCubic(xs).getTriples(), n, timeLoggersCubic);
    }

    /**
     * The main method serves as the entry point to the application. It sequentially executes performance
     * benchmarks for various configurations of the Heap algorithm. Each configuration specifies
     * the number of runs, the input size, and the range of randomly generated integers for the Three-Sum algorithm.
     * <p>
     * The benchmarks are designed to evaluate and log the performance of different algorithmic implementations:
     * the Basic binary heap and the 4-ary heap with and without Floyd's sink-to-bottom trick.
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
//        new PriorityQueueBenchmark(100, 15, 250, 15, 5).runBenchmarks();
        new PriorityQueueBenchmark(100, 250, 250, 4095, 62).runBenchmarks();
        new PriorityQueueBenchmark(100, 500, 500, 4095, 125).runBenchmarks();
        new PriorityQueueBenchmark(100, 1000, 1000, 4095, 250).runBenchmarks();
        new PriorityQueueBenchmark(100, 2000, 2000, 4095, 500).runBenchmarks();
        new PriorityQueueBenchmark(100, 4000, 4000, 4095, 1000).runBenchmarks();
        new PriorityQueueBenchmark(100, 8000, 8000, 4095, 2000).runBenchmarks();
        new PriorityQueueBenchmark(100, 16000, 16000, 4095, 4000).runBenchmarks();
    }

    /**
     * Benchmarks the performance of a specified Heap algorithm implementation
     * using a provided function, input size, and time loggers for result recording.
     * CONSIDER redefining function as an instance of Heap.
     *
     * @param description  a textual description of the Three-Sum algorithm being benchmarked.
     *                     Used for identification and logging purposes.
     * @param function     the specific implementation of the Three-Sum algorithm to test.
     *                     Encapsulated as a Consumer accepting an array of integers as input.
     * @param n            the size of the input array to generate and test the algorithm with.
     * @param timeLoggers  an array of TimeLogger instances responsible for logging the performance
     *                     results of the benchmark.
     */
    private void benchmarkPriorityQueue(final String description, final Consumer<Integer[]> function, int n, final TimeLogger[] timeLoggers) {
        // TO BE IMPLEMENTED
        Benchmark_Timer<Integer[]> timer = new Benchmark_Timer<>(
                description,
                null,              // No pre-function needed as we're using a supplier
                function,          // The Heap implementation to benchmark
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
     * An array of {@link TimeLogger} instances used for benchmarking the cubic implementation
     * of the Three-Sum algorithm. This array contains:
     * 1. A logger for raw execution time per run in milliseconds.
     * 2. A logger for normalized execution time, based on a cubic growth factor (n^3).
     * These loggers record performance data to facilitate analysis and comparison across
     * different implementations of the Three-Sum algorithm.
     */
    private final static TimeLogger[] timeLoggersCubic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^3): ", n -> 1.0 / 6 * n * n * n)
    };

    /**
     * An array of predefined TimeLogger instances used for benchmarking the performance
     * of the Three-Sum algorithm with a focus on a quadrithmic implementation.
     * The TimeLoggers in this array log both raw execution times and normalized times adjusted
     * for the expected theoretical complexity of the algorithm (n^2 log n).
     * - The first TimeLogger records raw execution times for benchmarking purposes.
     * - The second TimeLogger calculates normalized times, using the formula n^2 * log(n)
     *   (where log is computed to the base 2 via Utilities.lg).
     */
    private final static TimeLogger[] timeLoggersQuadrithmic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^2 log n): ", n -> n * n * Utilities.lg(n))
    };

    /**
     * Represents an array of {@code TimeLogger} instances specifically used for logging
     * performance metrics of the quadratic implementation of the Three-Sum algorithm.
     * Each {@code TimeLogger} in this array performs a distinct type of logging:
     * 1. Logs the raw execution time (milliseconds) per run.
     * 2. Logs the normalized execution time, accounting for the quadratic growth factor (n^2).
     * This constant is immutable and statically defined, providing reusable time
     * logging mechanisms for quadratic benchmark tests within the {@code ThreeSumBenchmark} class.
     */
    private final static TimeLogger[] timeLoggersQuadratic = {
            new TimeLogger("Raw time per run (mSec): ", null),
            new TimeLogger("Normalized time per run (n^2): ", n -> 1.0 / 2 * n * n)
    };

    /**
     * Represents an array of {@code TimeLogger} instances specifically used for logging
     * performance metrics of the heap algorithm.
     * Each {@code TimeLogger} in this array performs a distinct type of logging:
     * 1. Logs the raw execution time (milliseconds) per run.
     * 2. Logs the normalized execution time, accounting for the logarithmic growth factor (logn).
     * This constant is immutable and statically defined, providing reusable time
     * logging mechanisms for quadratic benchmark tests within the {@code ThreeSumBenchmark} class.
     */
    private final static TimeLogger[] timeLoggersHeap = {
            new TimeLogger("Raw time per operation (mSec): ", null),
            new TimeLogger("Normalized time per operation (log n): ", n -> Math.log(n) / Math.log(2))
    };

    private final int runs;
    private final Supplier<Integer[]> supplier;
    private final int numberOfElementsToInsert;
    private final int numberOfElementsToRemove;
    private final int sizeOfHeap;
}



