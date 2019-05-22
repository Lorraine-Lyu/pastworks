public class RedBlackTree<T extends Comparable<T>> {

    /* Root of the tree. */
    RBTreeNode<T> root;

    /* Creates an empty RedBlackTree. */
    public RedBlackTree() {
        root = null;
    }

    /* Creates a RedBlackTree from a given BTree (2-3-4) TREE. */
    public RedBlackTree(BTree<T> tree) {
        Node<T> btreeRoot = tree.root;
        root = buildRedBlackTree(btreeRoot);
    }

    /* Builds a RedBlackTree that has isometry with given 2-3-4 tree rooted at
       given node R, and returns the root node. */
    RBTreeNode<T> buildRedBlackTree(Node<T> r) {
        if (r == null) {
            return null;
        }
        if (r.getItemCount() == 1) {
            if (r.getChildrenCount() == 0) {
                return new RBTreeNode<>(true, r.getItemAt(0));
            }
            return new RBTreeNode<>(true, r.getItemAt(0), buildRedBlackTree(r.getChildAt(0)),
                    buildRedBlackTree(r.getChildAt(1)));
        }
        if (r.getItemCount() == 2) {
            if (r.getChildrenCount() == 0) {
                RBTreeNode<T> second = new RBTreeNode<T>(false, r.getItemAt(1));
                return new RBTreeNode<T>(true, r.getItemAt(0), null, second);
            }
            RBTreeNode<T> second = new RBTreeNode<T>(false, r.getItemAt(1),
                    buildRedBlackTree(r.getChildAt(1)), buildRedBlackTree(r.getChildAt(2)));
            return new RBTreeNode<T>(true, r.getItemAt(0),
                    buildRedBlackTree(r.getChildAt(0)), second);
        }

        if (r.getItemCount() == 3) {
            if (r.getChildrenCount() == 0) {
                RBTreeNode<T> first = new RBTreeNode<>(false, r.getItemAt(0));
                RBTreeNode<T> third = new RBTreeNode<>(false, r.getItemAt(2));
                return new RBTreeNode<T>(true, r.getItemAt(1), first, third);
            }
            RBTreeNode<T> first = new RBTreeNode<>(false, r.getItemAt(0),
                    buildRedBlackTree(r.getChildAt(0)), buildRedBlackTree(r.getChildAt(1)));
            RBTreeNode<T> third = new RBTreeNode<>(false, r.getItemAt(2),
                    buildRedBlackTree(r.getChildAt(2)), buildRedBlackTree(r.getChildAt(3)));
            return new RBTreeNode<>(true, r.getItemAt(1), first, third);
        }
        return null;
    }

    /* Flips the color of NODE and its children. Assume that NODE has both left
       and right children. */
    void flipColors(RBTreeNode<T> node) {
        node.isBlack = !node.isBlack;
        node.left.isBlack = !node.left.isBlack;
        node.right.isBlack = !node.right.isBlack;
    }

    /* Rotates the given node NODE to the right. Returns the new root node of
       this subtree. */
    RBTreeNode<T> rotateRight(RBTreeNode<T> node) {
//        RBTreeNode<T> trans = node.left;
////        RBTreeNode<T> newRoot = node.left;
//        node.left = trans.right;
//        trans.right = node;
//        return trans;
        if (node.left == null) {
            return node;
        } else {
            RBTreeNode<T> trans = node.left;
            if (trans.right == null) {
                trans.right = node;
                trans.isBlack = node.isBlack;
                node.left = null;
                node.isBlack = false;
            } else {
                node.left = trans.right;
                trans.isBlack = node.isBlack;
                trans.right = node;
                node.isBlack = false;
            }
            return trans;
        }

    }

    /* Rotates the given node NODE to the left. Returns the new root node of
       this subtree. */
    RBTreeNode<T> rotateLeft(RBTreeNode<T> node) {
//        RBTreeNode<T> trans = node.right;
//        node.right = trans.left;
//        trans.left = node;
//        return trans;
        if (node.right == null) {
            return node;
        }
        RBTreeNode<T> trans = node.right;
        if (trans.left == null) {
            trans.left = node;
            trans.isBlack = node.isBlack;
            node.right = null;
            node.isBlack = false;
        } else {
            node.right = trans.left;
            trans.isBlack = node.isBlack;
            trans.left = node;
            node.isBlack = false;
        }
        return trans;
    }

    /* Insert ITEM into the red black tree, rotating
       it accordingly afterwards. */
    void insert(T item) {
        if (root == null) {
            root = new RBTreeNode<T>(true, item);
        } else {
            root = helper(root, item);
            if (!root.isBlack) {
                root.isBlack = true;
            }
        }

    }

    private RBTreeNode<T> helper(RBTreeNode<T> node, T item) {
        if (item.compareTo(node.item) == 0) {
            return node;
        }
        if (item.compareTo(node.item) < 0) {
            if (node.right == null && node.left == null) {
                node.left = new RBTreeNode<>(false, item);
                return node;
            } else if (node.left != null && node.right == null) {
                if (item.compareTo(node.left.item) < 0) {
                    node.left.left = new RBTreeNode<>(false, item);
                    node = rotateRight(node);
                    flipColors(node);
                    return node;
                }
                if (item.compareTo(node.left.item) > 0) {
                    node.left.right = new RBTreeNode<>(false, item);
                    node.left = rotateLeft(node.left);
                    node = rotateRight(node);
                    flipColors(node);
                    return node;
                }
            } else {
                node.left = helper(node.left, item);
                if (isRed(node.right) && isRed(node.left)) {
                    flipColors(node);
                }
//                if (isRed(node.left)) {
//                    node = rotateRight(node);
//                }
//                if (node.left.right != null && isRed(node.left) && isRed(node.left.right)) {
//                    node = rotateRight(node);
//                }
            }
        }
        if (item.compareTo(node.item) > 0) {
            if (node.left == null && node.right == null) {
                node.right = new RBTreeNode<>(false, item);
                node = rotateLeft(node);
                return node;
            } else if (node.left != null && node.right == null) {
                node.right = new RBTreeNode<>(false, item);
                flipColors(node);
                return node;
            } else {
                node.right = helper(node.right, item);
                if (isRed(node.right) && isRed(node.left)) {
                    flipColors(node);
                }
                if (isRed(node.right)) {
                    node = rotateLeft(node);
                }
//                if (node.right.left != null && isRed(node.right) && isRed(node.right.left)) {
//                    node = rotateRight(node);
//                }
            }
        }
        return node;
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
        tree.insert(5);
        tree.insert(2);
        tree.insert(7);
        tree.insert(4);
        tree.insert(1);
        tree.insert(6);
    }

    /* Returns whether the given node NODE is red. Null nodes (children of leaf
       nodes are automatically considered black. */
    private boolean isRed(RBTreeNode<T> node) {
        return node != null && !node.isBlack;
    }

    static class RBTreeNode<T> {

        final T item;
        boolean isBlack;
        RBTreeNode<T> left;
        RBTreeNode<T> right;

        /* Creates a RBTreeNode with item ITEM and color depending on ISBLACK
           value. */
        RBTreeNode(boolean isBlack, T item) {
            this(isBlack, item, null, null);
        }

        /* Creates a RBTreeNode with item ITEM, color depending on ISBLACK
           value, left child LEFT, and right child RIGHT. */
        RBTreeNode(boolean isBlack, T item, RBTreeNode<T> left,
                   RBTreeNode<T> right) {
            this.isBlack = isBlack;
            this.item = item;
            this.left = left;
            this.right = right;
        }
    }

}
