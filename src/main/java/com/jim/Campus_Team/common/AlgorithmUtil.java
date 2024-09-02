package com.jim.Campus_Team.common;

import java.util.List;
import java.util.Objects;

public class AlgorithmUtil {

    public static int minDistance(List<String> list1, List<String> list2) {
        int[][] dp = new int[list1.size()][list2.size()];
        return minDistance(list1, list2, list1.size()-1, list2.size()-1, dp);
    }

    private static int minDistance(List<String> list1, List<String> list2, int i, int j, int[][] dp) {
        if (i == -1) return j + 1;
        if (j == -1) return i + 1;
        if (dp[i][j] != 0) return dp[i][j];
        if (Objects.equals(list1.get(i),list2.get(j))) {
            dp[i][j] = minDistance(list1, list2, i - 1, j - 1, dp);
        } else {
            dp[i][j] = Math.min(minDistance(list1, list2, i - 1, j - 1, dp),
                    Math.min(minDistance(list1, list2, i - 1, j, dp), minDistance(list1, list2, i, j - 1, dp))) + 1;
        }
        return dp[i][j];
    }
}
