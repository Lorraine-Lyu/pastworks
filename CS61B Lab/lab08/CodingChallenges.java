import java.util.Arrays;

public class CodingChallenges {

    /**
     * Return the missing number from an array of length N - 1 containing all
     * the values from 0 to N except for one missing number.
     */
    public static int missingNumber(int[] values) {
        int missingNum = values.length;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != i) {
                missingNum = i;
                break;
            }
        }
        return missingNum;
    }

    /** Returns true if and only if two integers in the array sum up to n. */
    public static boolean sumTo(int[] values, int n) {
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                if (values[i] + values[j] == n) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if and only if s1 is a permutation of s2. s1 is a
     * permutation of s2 if it has the same number of each character as s2.
     */
    public static boolean isPermutation(String s1, String s2) {
        s1.toLowerCase();
        s2.toLowerCase();
        char[] s1char = s1.toCharArray();
        char[] s2char = s2.toCharArray();
        Arrays.sort(s1char);
        Arrays.sort(s2char);
        return Arrays.equals(s1char, s2char);
    }
}
