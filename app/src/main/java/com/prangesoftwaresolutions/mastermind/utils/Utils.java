package com.prangesoftwaresolutions.mastermind.utils;

import java.util.List;

public class Utils {
    public static char getLastChar(String string) {
        return string.charAt(string.length() - 1);
    }

    public static int getTargetIndex(String tag) {
        return Character.getNumericValue(getLastChar(tag));
    }

    /*
     * Get sum of a list of integers
     */
    public static int sum(List<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        return sum;
    }
}
