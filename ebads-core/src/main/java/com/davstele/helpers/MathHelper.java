package com.davstele.helpers;

/**
 * Created by dx on 5/18/16.
 */
public class MathHelper {

    public static double logX (double a, double b) {
        return Math.log(a) / Math.log(b);
    }

    public static double log2 (double a) {
        return logX(a, 2);
    }
}
