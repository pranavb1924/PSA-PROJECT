/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each sub-space corresponds to a fixed value for the middle index of the three values.
 * Each sub-space is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each sub-space can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * NOTE: The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadratic implements ThreeSum {
    /**
     * Construct a ThreeSumQuadratic on a.
     *
     * @param a a sorted array.
     */
    public ThreeSumQuadratic(int[] a) {
        this.a = a;
        length = a.length;
    }

    /**
     * Retrieves an array of unique Triples. Each Triple represents a unique combination of three integers from
     * the source array that sum to zero.
     *
     * @return an array of distinct Triples, sorted in natural order, where each Triple satisfies the condition that
     * the sum of its three integers is zero.
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) triples.addAll(getTriples(i));
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a list of Triples such that the middle index is the given value j.
     *
     * @param j the index of the middle value.
     * @return a Triple such that
     */
     List<Triple> getTriples(int j) {
         List<Triple> triples = new ArrayList<>();
        // TO BE IMPLEMENTED  : for each candidate, test if a[i] + a[j] + a[k] = 0.
         int left = j + 1, right = this.a.length - 1;
         while (left < right) {

             long currentSum = (long) this.a[left] + (long) this.a[right] + (long) this.a[j];

//             System.out.println("a[i]: " + this.a[left] + " a[k]: " + this.a[right] + " a[j]: " + this.a[j] + " sum: " + currentSum);

             if (currentSum == 0) {
                 int[] triplet = {a[left], a[j], a[right]};
                 java.util.Arrays.sort(triplet); // Sort the triplet values
                 triples.add(new Triple(triplet[0], triplet[1], triplet[2]));
                 left++;
                 right--;

             } else if (currentSum < 0) {
                 left++;
             }else {
                 right--;
             }
         }
         return triples;

//throw new RuntimeException("implementation missing");
    }

    private final int[] a;
    private final int length;
}