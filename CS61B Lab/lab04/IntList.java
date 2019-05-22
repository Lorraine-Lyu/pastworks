/**
 * A data structure to represent a Linked List of Integers.
 * Each IntList represents one node in the overall Linked List.
 */
public class IntList {
    public int first;
    public IntList rest;

    //    public static void main(String[] args) {
//        IntList L = new IntList(5,
//                new IntList(10,
//                        new IntList(15, null)));
//        IntList Q = new IntList(5,
//                new IntList(11,
//                        new IntList(15, null)));
//        System.out.print(L.toString());
//        IntList origL = IntList.of(1, 2, 3);
//        dSquareList(origL);
//        origL is now (1, 4, 9)
//        System.out.print(L.get(2));
//        L.toString();
    // System.out.print(L.size());
//    }
    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /**
     * Returns an IntList consisting of the given values.
     */
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

    /**
     * Returns the size of the list.
     */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + rest.size();
    }

    /**
     * Returns [position]th value in this list.
     */
    public int get(int position) {

        if (position == 0) {
            return first;
        } else {

            position--;
            return rest.get(position);
        }

    }

    /**
     * Returns the string representation of the list.
     */
    public String toString() {


        IntList p = this;
        String str = "";
        while (p != null) {
            if (p.rest == null) {
                str = str + p.first;
                break;
            } else {
                str = str + p.first + " ";
                p = p.rest;
            }
        }
        return str;
    }

    /**
     * Returns whether this and the given list or object are equal.
     */
    public boolean equals(Object o) {
        IntList other = (IntList) o;

        IntList p = this;
        if (p == null && other == null) {
            return true;
        } else if (p == null && other != null || (p != null && other == null)) {
            return false;
        }
        if (p.size() != other.size()) {
            return false;
        }
        int size = p.size();
        for (int i = 0; i < size; i++) {
            if (p.first != other.first) {
                return false;
            } else {
                p = p.rest;
                other = other.rest;
            }
        }
        return true;
    }

    /**
     * add a new value at the end of the intList
     **/
    public void add(int value) {
        IntList p = this;
        while (true) {
            if (p.rest == null) {
                p.rest = new IntList(value, null);
                break;
            } else {
                p = p.rest;
            }
        }
    }

    /**
     * return the smallest number in the intList
     **/
    public int smallest() {
        IntList p = this;
        int smallest = p.first;
        p = p.rest;
        while (p != null) {
            if (p.first < smallest) {
                smallest = p.first;
            }
            p = p.rest;
        }
        return smallest;
    }

    /**
     * return the sum of the square of all elements in the intList
     **/
    public int squaredSum() {
        IntList p = this;
        int sum = 0;
        while (p != null) {
            sum += Math.pow(p.first, 2);
            p = p.rest;
        }
        return sum;
    }

    public static void dSquareList(IntList L) {
        while (L != null) {
            L.first = L.first * L.first;
            L = L.rest;
        }
    }

    public static IntList dcatenate(IntList A, IntList B) {
        if (A == null) {
            return B;
        }
        while (B != null) {
            A.add(B.first);
            B = B.rest;
        }
        return A;
    }

    public static IntList catenate(IntList A, IntList B) {
        IntList C = null;
        if (A == null) {
            if (B == null) {
                return C;
            }
            C = new IntList(B.first, null);
            B = B.rest;
            while (B != null) {
                C.add(B.first);
                B = B.rest;
            }
        } else {
            C = new IntList(A.first, null);
            A = A.rest;
            while (A != null) {
                C.add(A.first);
                A = A.rest;
            }
            if (B == null) {
                return C;
            } else {
                C.add(B.first);
                B = B.rest;
                while (B != null) {
                    C.add(B.first);
                    B = B.rest;
                }
            }

        }
        return C;

    }
}
