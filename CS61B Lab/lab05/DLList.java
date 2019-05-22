import java.util.Objects;

/**
  * A DLList is a list of integers. Like SLList, it also hides the terrible
  * truth of the nakedness within, but with a few additional optimizations.
  */
public class DLList<BleepBlorp> {
    private class Node {
        public Node prev;
        public BleepBlorp item;
        public Node next;

        public Node(BleepBlorp i, Node p, Node n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    /* The first item (if it exists) is at sentinel.next. */
    private Node sentinel;
    private int size;

    @Override
    public int hashCode() {

        return Objects.hash(sentinel, size);
    }

    @Override
    public String toString() {
        return "DLList{" + "sentinel=" + sentinel + ", size=" + size + '}';
    }

    /** Creates an empty DLList. */
    public DLList() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    /** Returns a DLList consisting of the given values. */
    public static <BleepBlorp> DLList of(BleepBlorp... values) {
        DLList<BleepBlorp> list = new DLList<>();
        for (BleepBlorp value : values) {
            list.addLast(value);
        }
        return list;
    }

    /** Returns the size of the list. */
    public int size() {
        return size;
    }

    /** Adds item to the front of the list. */
    public void addFirst(BleepBlorp item) {
        Node n = new Node(item, sentinel, sentinel.next);
        n.next.prev = n;
        n.prev.next = n;
        size += 1;
    }

    /** Adds item to the back of the list. */
    public void addLast(BleepBlorp item) {
        Node n = new Node(item, sentinel.prev, sentinel);
        n.next.prev = n;
        n.prev.next = n;
        size += 1;
    }

    /** Adds item to the list at the specified index. */
    public void add(int index, BleepBlorp item) {
        if (size == 0) {
            addFirst(item);
        } else {
            if (index == 0) {
                addFirst(item);
            } else {
                Node n = sentinel.next;
                while (index > 1) {
                    n = n.next;
                    index -= 1;
                }
                Node insert = new Node(item, n, n.next);
                n.next = insert;
                n.next.next.prev = insert;
                size += 1;
            }
        }
    }

    /** Remove the first instance of item from this list. */
    public void remove(BleepBlorp item) {
        if (size == 0) {
            return;
        }
        Node D = sentinel;
        while (D.next != sentinel) {
            if (D.next.item == item) {
                D.next = D.next.next;
                D.next.prev = D;
                size -= 1;
                break;
            } else {
                D = D.next;
            }
        }

    }

    /** Returns whether this and the given list or object are equal. */
    public boolean equals(Object o) {
        DLList other = (DLList) o;
        if (size() != other.size()) {
            return false;
        }
        Node op = other.sentinel.next;
        for (Node p = sentinel.next; p != sentinel; p = p.next) {
            if (!(p.item.equals(op.item))) {
                return false;
            }
            op = op.next;
        }
        return true;
    }
}
