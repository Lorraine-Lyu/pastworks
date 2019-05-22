//import jdk.nashorn.api.tree.Tree;

public class BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    /* Creates an empty BST. */
    public BinarySearchTree() {
        super();
    }

    /* Creates a BST with root as ROOT. */
    public BinarySearchTree(TreeNode root) {
        super(root);
    }

    /* Returns true if the BST contains the given KEY. */
    public boolean contains(T key) {
        if (root == null) {
            return false;
        }
        boolean contains = containsHelper(root, key);
        return contains;
    }

    private boolean containsHelper(TreeNode node, T key) {
        boolean bool = false;
        if (key.compareTo(node.item) > 0) {
            if (node.right == null) {
                return bool;
            }
            bool = containsHelper(node.right, key);
        } else if (key.compareTo(node.item) < 0) {
            if (node.left == null) {
                return bool;
            }
            bool = containsHelper(node.left, key);
        } else {
            bool = true;
        }

        return bool;
    }

    /* Adds a node for KEY iff KEY isn't in the BST already. */
    public void add(T key) {
        if (root == null) {
            root = new TreeNode(key);
        }
        addHelper(root, key);
    }

    private void addHelper(TreeNode node, T key) {
        if (node.item.compareTo(key) == 0) {
            return;
        }
        if (node.item.compareTo(key) > 0) {
            if (node.left == null) {
                node.left = new TreeNode(key);
            }
            addHelper(node.left, key);
        } else {
            if (node.right == null) {
                node.right = new TreeNode(key);
            }
            addHelper(node.right, key);
        }


    }

    /* Deletes a node from the BST. */
    public T delete(T key) {
        TreeNode parent = null;
        TreeNode curr = root;
        TreeNode delNode = null;
        TreeNode replacement = null;
        boolean rightSide = false;

        while (curr != null && !curr.item.equals(key)) {
            if (((Comparable<T>) curr.item).compareTo(key) > 0) {
                parent = curr;
                curr = curr.left;
                rightSide = false;
            } else {
                parent = curr;
                curr = curr.right;
                rightSide = true;
            }
        }
        delNode = curr;
        if (curr == null) {
            return null;
        }

        if (delNode.right == null) {
            if (root == delNode) {
                root = root.left;
            } else {
                if (rightSide) {
                    parent.right = delNode.left;
                } else {
                    parent.left = delNode.left;
                }
            }
        } else {
            curr = delNode.right;
            replacement = curr.left;
            if (replacement == null) {
                replacement = curr;
            } else {
                while (replacement.left != null) {
                    curr = replacement;
                    replacement = replacement.left;
                }
                curr.left = replacement.right;
                replacement.right = delNode.right;
            }
            replacement.left = delNode.left;
            if (root == delNode) {
                root = replacement;
            } else {
                if (rightSide) {
                    parent.right = replacement;
                } else {
                    parent.left = replacement;
                }
            }
        }
        return delNode.item;
    }
}
