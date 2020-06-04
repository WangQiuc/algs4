/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/29/2020
 *  Description: Assignment 5 Burrows–Wheeler
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private static final int CUTOFF = 10;
    private final int n;
    private final String s;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("Null input");
        this.s = s;
        n = s.length();
        index = new int[n];
        for (int i = 0; i < n; i++)
            index[i] = i;
        threeWaysQuickSort(0, n-1);
    }

    // length of s
    public int length() {
        return n;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= n) throw new IllegalArgumentException("Out of bound");
        return index[i];
    }

    private int compare(int x, int y) {
        for (int i = 0; i < n; i++) {
            int cmp = Character.compare(s.charAt((x + i) % n), s.charAt((y + i) % n));
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private void exch(int i, int j) {
        int t = index[i];
        index[i] = index[j];
        index[j] = t;
    }

    private void insertionSort(int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && compare(index[j], index[j - 1]) < 0; j--) {
                exch(j, j - 1);
            }
    }

    private void threeWaysQuickSort(int lo, int hi) {
        if (hi - lo > CUTOFF) {
            int lt = lo;
            int gt = hi;
            int v = index[lo];
            int i = lo + 1;
            while (i <= gt) {
                int cmp = compare(index[i], v);
                if (cmp < 0) exch(lt++, i++);
                else if (cmp > 0) exch(i, gt--);
                else i++;
            }
            threeWaysQuickSort(lo, lt - 1);
            threeWaysQuickSort(gt + 1, hi);
        }
        else insertionSort(lo, hi);
    }

    // unit testing (required)
    public static void main(String[] args) {
        In in = new In(args[0]);
        CircularSuffixArray csa = new CircularSuffixArray(in.readString());
        for (int i = 0; i < csa.length(); i++)
            StdOut.println(csa.index(i));
    }
}
