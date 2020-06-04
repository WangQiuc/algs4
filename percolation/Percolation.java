/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     12/26/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int m;
    private byte[] grid;
    private final WeightedQuickUnionUF uf;
    private int count;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("Invalid input");
        // 1-index, 0 used as virtual top;
        // no virtual bottom node to avoid backwash, use link-to-bottom status(2) instead,
        // then separated bottom open sites won't be connected to 0 via virtual bottom node,
        // status(2) is transitive so the system percolates when 0's root is status(2);
        m = n + 1;
        grid = new byte[m * m];  // status of each site: 0-block, 1-open; 2-link to bottom
        uf = new WeightedQuickUnionUF(m * m);
    }

    // check if input is outside its prescribed range
    private void validate(int i, int j) {
        if (i <= 0 || i >= m) throw new IllegalArgumentException("Row index out of boundary");
        if (j <= 0 || j >= m) throw new IllegalArgumentException("Col index out of boundary");
    }

    // union two neighbor sites; link-to-bottom status(2) is transitive
    private void union(int u, int v) {
        int pu = uf.find(u);
        int pv = uf.find(v);
        uf.union(pu, pv);
        if (grid[pu] == 2 || grid[pv] == 2) grid[uf.find(pu)] = 2;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);
        if (isOpen(row, col)) return;
        int i = row * m + col;
        grid[i] = (byte) (row < m - 1 ? 1 : 2);
        count++;
        if (row == 1) union(0, i);
        if (row > 1 && isOpen(row - 1, col)) union(i, uf.find((row - 1) * m + col));
        if (row < m - 1 && isOpen(row + 1, col)) union(i, uf.find((row + 1) * m + col));
        if (col > 1 && isOpen(row, col - 1)) union(i, uf.find(row * m + col - 1));
        if (col < m - 1 && isOpen(row, col + 1)) union(i, uf.find(row * m + col + 1));
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return grid[row * m + col] > 0;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return uf.connected(0, row * m + col);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return count;
    }

    // does the system percolate?
    public boolean percolates() {
        return grid[uf.find(0)] == 2;
    }

    // test client (optional)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        Percolation perc = new Percolation(n);
        while (!StdIn.isEmpty()) {
            int i = StdIn.readInt();
            int j = StdIn.readInt();
            perc.open(i, j);
            if (perc.percolates()) break;
        }
        StdOut.println(perc.numberOfOpenSites() + " open sites");
        if (perc.percolates()) StdOut.println("Percolates");
        else StdOut.println("Doesn't Percolate");
    }
}
