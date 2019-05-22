// import edu.princeton.cs.algs4.QuickFindUF;
// import edu.princeton.cs.algs4.QuickUnionUF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    public boolean[][] open_or_not;
    public int num_open;
    public WeightedQuickUnionUF wq;

    /* Creates an N-by-N grid with all sites initially blocked. */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        open_or_not = new boolean[N][N];
        num_open = 0;
        wq = new WeightedQuickUnionUF(N * N + 2);
    }

    /* Opens the site (row, col) if it is not open already. */
    public void open(int row, int col) {
        if (!valid(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        if (!open_or_not[row][col]) {
            open_or_not[row][col] = true;
            num_open++;
        }
        if (row == 0) {
            wq.union(xyTo1D(row, col), open_or_not.length * open_or_not.length);
        }
        if (row == open_or_not.length - 1) {
            wq.union(xyTo1D(row, col), open_or_not.length * open_or_not.length + 1);
        }
        if (row > 0 && isOpen(row - 1, col)) {
            wq.union(xyTo1D(row, col), xyTo1D(row - 1, col));
        }
        if (row < open_or_not.length - 1 && isOpen(row + 1, col)) {
            wq.union(xyTo1D(row, col), xyTo1D(row + 1, col));
        }
        if (col < open_or_not.length - 1 && isOpen(row, col + 1)) {
            wq.union(xyTo1D(row, col), xyTo1D(row, col + 1));
        }
        if (col > 0 && isOpen(row, col - 1)) {
            wq.union(xyTo1D(row, col), xyTo1D(row, col - 1));
        }
    }

    /* Returns true if the site at (row, col) is open. */
    public boolean isOpen(int row, int col) {
        if (!valid(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        return open_or_not[row][col];
    }

    /* Returns true if the site (row, col) is full. */
    public boolean isFull(int row, int col) {
        if (!valid(row, col)) {
            throw new IndexOutOfBoundsException();
        }
//        for (int i = 0; i < open_or_not.length; i++) {
//            if (isOpen(0, i) && wq.connected(xyTo1D(0, i), xyTo1D(row, col))) {
//                return true;
//            }
//        }
        return wq.connected(xyTo1D(row, col), open_or_not.length * open_or_not.length);
    }

    /* Returns the number of open sites. */
    public int numberOfOpenSites() {
        return num_open;
    }

    /* Returns true if the system percolates. */
    public boolean percolates() {
//        for (int j = 0; j < open_or_not.length; j++) {
//            for (int i = 0; i < open_or_not.length; i++) {
//                if (isOpen(0, j) && isOpen(open_or_not.length - 1, i)
//                        && wq.connected(xyTo1D(0, j), xyTo1D(open_or_not.length - 1, i))) {
//                    return true;
//                }
//            }
//        }

        return wq.connected(open_or_not.length * open_or_not.length,
                open_or_not.length * open_or_not.length + 1);
    }

    /* Converts row and column coordinates into a number. This will be helpful
       when trying to tie in the disjoint sets into our NxN grid of sites. */
    private int xyTo1D(int row, int col) {
        return row * open_or_not.length + col;
    }
    /* Returns true if (row, col) site exists in the NxN grid of sites.
       Otherwise, return false. */
    private boolean valid(int row, int col) {
        return row < open_or_not.length && row >= 0 && col < open_or_not.length && col >= 0;
    }
}
