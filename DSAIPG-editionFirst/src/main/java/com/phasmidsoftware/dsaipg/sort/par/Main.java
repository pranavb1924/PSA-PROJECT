/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        processArgs(args);
//        System.out.println("Degree of parallelism: " + ForkJoinPool.getCommonPoolParallelism());

        Random random = new Random();
        int[] array = new int[2000000];
//        ArrayList<Long> timeList = new ArrayList<>();

        int maxThreads = Runtime.getRuntime().availableProcessors();
        int exponent = (int) ( Math.log( maxThreads ) / Math.log(2) + 1e-10 );
        int maxDegreeOfParallelism = Math.min(maxThreads * 2, 1 << exponent);

        for(int k = 2; k <= maxDegreeOfParallelism; k <<= 1) {

            int degreeOfParallelism = Math.max(2, k);

            ParSort.recursions = (int)( Math.log( degreeOfParallelism ) / Math.log(2) + 1e-10 ) ;

            // Shutdown previous thread pool if necessary
            if (ParSort.threadPool != null) {
                ParSort.threadPool.shutdown();
            }

            // Create a new ForkJoinPool
            ParSort.threadPool = new ForkJoinPool(degreeOfParallelism);

            System.out.println("Degree of parallelism: " + degreeOfParallelism );


            for (int j = 50; j < 100; j++) {
                ParSort.cutoff = 10000 * (j + 1);
                // for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                long time;
                long startTime = System.currentTimeMillis();
                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                    ParSort.sort(array, 0, array.length);
                }
                long endTime = System.currentTimeMillis();
                time = (endTime - startTime);
//                timeList.add(time);

                System.out.println("cutoff：" + (ParSort.cutoff) + "\t\t10times Time:" + time + "ms");
            }
        }

//        try {
//            FileOutputStream fis = new FileOutputStream("./src/result.csv");
//            OutputStreamWriter isr = new OutputStreamWriter(fis);
//            BufferedWriter bw = new BufferedWriter(isr);
//            int j = 0;
//            for (long i : timeList) {
//                String content = (double) 10000 * (j + 1) / 2000000 + "," + (double) i / 10 + "\n";
//                j++;
//                bw.write(content);
//                bw.flush();
//            }
//            bw.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs); // seems incorrect
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0]; // this seems incorrect
        // I sugest
        // String[] result = new String[xs.length - 2];

        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]); // seems incorrect

        // I suggest
        // String command = xs[0].startsWith("-") ? xs[0].substring(1) : xs[0];
        // processCommand(command, xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}