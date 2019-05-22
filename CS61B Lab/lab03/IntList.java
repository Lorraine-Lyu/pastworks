/**
 * A data structure to represent a Linked List of Integers.
 * Each IntList represents one node in the overall Linked List.
 */
public class IntList {
    public int first;
    public IntList rest;

    public static void main(String[] args){
      IntList L = new IntList(5,
                                new IntList(10,
                                  new IntList(15, null)));
      IntList Q = new IntList(5,
                                new IntList(11,
                                  new IntList(15, null)));
      System.out.print(L.toString());
      // System.out.print(L.get(2));
      // L.toString();
      // System.out.print(L.size());
    }
    public IntList(int f, IntList r) {
      first = f;
      rest = r;
    }

    /** Returns an IntList consisting of the given values. */
    public static IntList of(int... values) {
        if (values.length == 0) {
            return null;
        }
        IntList p = new IntList(values[0], null);
        IntList front = p;
        for (int i = 1; i < values.length; i++) {
            p.rest = new IntList(values[i], null);
            p = p.rest;
        }
        return front;
    }

    /** Returns the size of the list. */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + rest.size();
    }

    /** Returns [position]th value in this list. */
    public int get(int position) {
        // TODO: YOUR CODE HERE
        if(position == 0){
          return first;
        }else{
          position --;
          return rest.get(position);
        }

    }

    /** Returns the string representation of the list. */
    public String toString() {
        // TODO: YOUR CODE HERE

        IntList p = this;
        String str = "";
        while(p != null){
          if (p.rest == null){
              str = str + p.first;
              break;
          }else{
              str = str + p.first + " ";
              p = p.rest;
            }
        }
        return str;
    }

    /** Returns whether this and the given list or object are equal. */
    public boolean equals(Object o) {
        IntList other = (IntList) o;
        // TODO: YOUR CODE HERE
        IntList p = this;
        if (p.size() != other.size()){
          return false;
        }
        int size = p.size();
        for (int i = 0; i < size ; i ++) {
          if (p.first != other.first){
            return false;
          }else{
            p = p.rest;
            other = other.rest;
          }
        }
        return true;
    }
}
