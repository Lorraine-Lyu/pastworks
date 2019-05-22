import java.util.LinkedList;
import java.util.Iterator;

public class BST<T> {

    BSTNode<T> root;

    public BST(LinkedList<T> list) {
        root = sortedIterToTree(list.iterator(), list.size());
    }

    /* Returns the root node of a BST (Binary Search Tree) built from the given
       iterator ITER  of N items. ITER will output the items in sorted order,
       and ITER will contain objects that will be the item of each BSTNode. */
    private BSTNode<T> sortedIterToTree(Iterator<T> iter, int N) {
        if (N == 1) {
            return new BSTNode<>(iter.next());
        }
        if (N == 0) {
            return null;
        }
        BSTNode<T> left = sortedIterToTree(iter, Math.floorDiv(N, 2));
        BSTNode<T> toReturn = new BSTNode<>(iter.next());
        BSTNode<T> right = sortedIterToTree(iter, N - 1 - Math.floorDiv(N, 2));
        toReturn.left = left;
        toReturn.right = right;
        return toReturn;
    }

    /* Prints the tree represented by ROOT. */
    private void print() {
        print(root, 0);
    }

    private void print(BSTNode<T> node, int d) {
        if (node == null) {
            return;
        }
        for (int i = 0; i < d; i++) {
            System.out.print("  ");
        }
        System.out.println(node.item);
        print(node.left, d + 1);
        print(node.right, d + 1);
    }

    class BSTNode<T> {
        T item;
        BSTNode<T> left;
        BSTNode<T> right;

        BSTNode(T item) {
            this.item = item;
        }

//        public BSTNode<T> insertionHelper(BSTNode<T> node, T ins) {
//            if (node.left == null && node.right == null) {
//                node.left = new BSTNode<>(node.item);
//                node.item = ins;
//            } else if (node.left != null && node.right == null) {
//                node.right = new BSTNode<>(item);
//            } else {
//                node.right = insertionHelper(node.right, item);
//            }
//            if (countLeft(node) == countRight(node) - 2) {
//                BSTNode<T> trans = node.left;
//                node.left = trans.right;
//                trans.left = node;
//                node = trans;
//            }
//            return node;
//        }

//        private int countLeft(BSTNode<T> node) {
//            if (node == null) {
//                return 0;
//            }
//
//            return 1 + countLeft(node.left);
//        }
//
//        private int countRight(BSTNode<T> node) {
//            if (node == null) {
//                return 0;
//            }
//            if (node.left != null && node.right == null) {
//                return 1;
//            }
//            return 1 + countLeft(node.right);
//        }
    }
}
