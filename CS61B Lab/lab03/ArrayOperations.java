public class ArrayOperations {
   public static void main(String[] args){
      int[] values = {1, 2, 3, 4, 5};
      insert(values, 3, 8);
      delete(values, 2);
      for(int i : values){
        System.out.println(i);
      }
    }
    /**
     * Delete the value at the given position in the argument array, shifting
     * all the subsequent elements down, and storing a 0 as the last element of
     * the array.
     */
    public static void delete(int[] values, int pos) {
        if (pos < 0 || pos >= values.length) {
            return;
        }
        // TODO: YOUR CODE HERE
        for (int i = pos; i < values.length - 1; i++){
          values[i] = values[i + 1];
        }
        values[values.length-1] = 0;
    }

    /**
     * Insert newInt at the given position in the argument array, shifting all
     * the subsequent elements up to make room for it. The last element in the
     * argument array is lost.
     */
    public static void insert(int[] values, int pos, int newInt) {
        if (pos < 0 || pos >= values.length) {
            return;
        }
        // TODO: YOUR CODE HERE
        for (int i = values.length - 1; i > pos; i --){
          values[i]= values[i - 1];
        }
        values[pos] = newInt;
    }
}
