/*
 * Copyright (c) 2017-2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.misc;

class NewtonApproximation {
    public static void main(String[] args) {
        // Newton's Approximation to solve cos(x) = x
        double x = 1.0;
        int left = 200;
//        int left = 50000;
        for (; left > 0; left--) {
//            final double y = Math.cos(x) - x;
            final double y = ( 1 / Math.cos(x) ) - x;
//            System.out.println(y);
            if (Math.abs(y) < 1E-7) {
//                System.out.println("the solution to cos(x)=x is: " + x);
                System.out.println("the solution to sex(x)=x is: " + x);
                System.exit(0);
            }
//            x = x + y / (Math.sin(x) + 1);
            x = x - ( y / ( ( 1 / Math.cos(x) ) * Math.tan(x) - 1 ) );
        }
    }
}