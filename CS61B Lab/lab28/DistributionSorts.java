import java.util.Arrays;

public class DistributionSorts {

    /* Destructively sorts ARR using counting sort. Assumes that ARR contains
       only 0, 1, ..., 9. */
    public static void countingSort(int[] arr) {
        int[] count = new int[10];
        int[] positions = new int[10];
        for (int i : arr) {
            count[i] += 1;
        }
        positions[0] = count[0];
        for (int n = 1; n < 10; n++) {
            positions[n] = count[n] + positions[n - 1];
        }
        for (int j = 0; j < arr.length; j++) {
            for (int pos = 0; pos < 10; pos++) {
                if (j < positions[pos]) {
                    arr[j] = pos;
                    break;
                }
            }
        }
    }

    /* Destructively sorts ARR using LSD radix sort. */
    public static void lsdRadixSort(int[] arr) {
        int maxDigit = mostDigitsIn(arr);
        for (int d = 0; d < maxDigit; d++) {
            countingSortOnDigit(arr, d);
        }
    }

    /* A helper method for radix sort. Modifies ARR to be sorted according to
       DIGIT-th digit. When DIGIT is equal to 0, sort the numbers by the
       rightmost digit of each number. */
    private static void countingSortOnDigit(int[] arr, int digit) {
        int[] count = new int[10];
        int[] positions = new int[10];
        for (int i : arr) {
            int d = getDigit(i, digit);
            count[d] += 1;
        }
        positions[0] = 0;
        for (int n = 1; n < 10; n++) {
            positions[n] = count[n - 1] + positions[n - 1];
        }
        int[] temp = new int[arr.length];
        for (int j = 0; j < arr.length; j++) {
            int d = getDigit(arr[j], digit);
            temp[positions[d]] = arr[j];
            positions[d] += 1;
        }
        System.arraycopy(temp, 0, arr, 0, temp.length);

    }

    public static int getDigit(int num, int digit) {
        int d = Math.floorDiv(Math.floorMod(num,
                (int) Math.pow(10, digit + 1)), (int) Math.pow(10, digit));
        return d;
    }

    /* Returns the largest number of digits that any integer in ARR has. */
    private static int mostDigitsIn(int[] arr) {
        int maxDigitsSoFar = 0;
        for (int num : arr) {
            int numDigits = (int) (Math.log10(num) + 1);
            if (numDigits > maxDigitsSoFar) {
                maxDigitsSoFar = numDigits;
            }
        }
        return maxDigitsSoFar;
    }

    /* Returns a random integer between 0 and 9999. */
    private static int randomInt() {
        return (int) (10000 * Math.random());
    }

    /* Returns a random integer between 0 and 9. */
    private static int randomDigit() {
        return (int) (10 * Math.random());
    }

    private static void runCountingSort(int len) {
        int[] arr1 = new int[len];
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = randomDigit();
        }
        System.out.println("Original array: " + Arrays.toString(arr1));
        countingSort(arr1);
        if (arr1 != null) {
            System.out.println("Should be sorted: " + Arrays.toString(arr1));
        }
    }

    private static void runLSDRadixSort(int len) {
        int[] arr2 = new int[len];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = randomDigit();
        }
        System.out.println("Original array: " + Arrays.toString(arr2));
        lsdRadixSort(arr2);
        System.out.println("Should be sorted: " + Arrays.toString(arr2));

    }

    public static void main(String[] args) {
        runCountingSort(20);
//        int[] d = new int[]{191, 235, 892, 45, 1};
//        lsdRadixSort(d);
//        System.out.println(Arrays.toString(d));
        runLSDRadixSort(3);
        runLSDRadixSort(30);
//        System.out.println(getDigit(103, 2));
    }
}
