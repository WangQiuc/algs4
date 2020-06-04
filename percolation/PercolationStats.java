/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     MM/DD/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private static final double CONFIDENCE_95 = 1.96;
    private final double[] counts;
    private final int m;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("Invalid inputs");
        m = trials;
        counts = new double[m];
        for (int i = 0; i < m; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates())
                perc.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
            counts[i] = (double) perc.numberOfOpenSites() / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(counts);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(counts);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (CONFIDENCE_95 * stddev()) / Math.sqrt(m);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (CONFIDENCE_95 * stddev()) / Math.sqrt(m);
    }

    public static void main(String[] args) {
        PercolationStats stats = new PercolationStats(StdIn.readInt(), StdIn.readInt());
        StdOut.printf("mean\t\t\t\t\t= %f\n", stats.mean());
        StdOut.printf("stddev\t\t\t\t\t= %f\n", stats.stddev());
        StdOut.printf("95%% confidence interval = [%f, %f]\n",
                      stats.confidenceLo(), stats.confidenceHi());
    }
}
