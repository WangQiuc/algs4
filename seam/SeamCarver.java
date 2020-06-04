/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/11/2020
 *  Description: Assignment 2 Seam Carver
 ******************************************************************************/


import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private static final double BOUNDENERGY = 1000;
    private int[][] pixels;  // store int rgb value instead of Picture to save memory
    private double[] energy; // cache energy value
    private int W;
    private int H;
    private boolean isV = true;  // used for lazy transpose, don't transpose until necessary

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Null input");
        W = picture.width();
        H = picture.height();
        pixels = new int[H][W];
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                pixels[y][x] = picture.getRGB(x, y);
            }
        }
        energy = new double[W * H];
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                energy[x + y * W] = energy(x, y);
            }
        }
    }

    // current picture
    public Picture picture() {
        if (!isV) transpose();
        Picture picture = new Picture(W, H);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                picture.set(x, y, new Color(pixels[y][x]));
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return isV ? W : H;
    }

    // height of current picture
    public int height() {
        return isV ? H : W;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (!isV) {
            int t = x;
            x = y;
            y = t;
        }
        if (x < 0 || x >= W || y < 0 || y >= H) throw new IllegalArgumentException("Out of bound");
        if (x == 0 || x == W - 1 || y == 0 || y == H - 1) return BOUNDENERGY;
        return Math.sqrt(gradientSquared(x, y, true) + gradientSquared(x, y, false));
    }

    // square of x-gradient and y-gradient of pixel (x, y)
    private int gradientSquared(int x, int y, boolean isX) {
        int c1 = isX ? pixels[y][x - 1] : pixels[y - 1][x];
        int c2 = isX ? pixels[y][x + 1] : pixels[y + 1][x];
        int rDiff = (c1 >> 16 & 0xFF) - (c2 >> 16 & 0xFF);
        int gDiff = (c1 >> 8 & 0xFF) - (c2 >> 8 & 0xFF);
        int bDiff = (c1 & 0xFF) - (c2 & 0xFF);
        return rDiff * rDiff + gDiff * gDiff + bDiff * bDiff;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (isV) transpose();
        return findSeam();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (!isV) transpose();
        return findSeam();
    }

    private int[] findSeam() {
        int[] path = buildPath();
        int[] seam = new int[H];
        for (int v = path[H * W + 1]; v > 0; v = path[v])
            seam[(v - 1) / W] = (v - 1) % W;
        return seam;
    }

    private void transpose() {
        int[][] pixelsT = new int[W][H];
        double[] energyT = new double[W * H];
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                pixelsT[x][y] = pixels[y][x];
                energyT[y + x * H] = energy[x + y * W];
            }
        }
        pixels = pixelsT;
        energy = energyT;
        W = H;
        H = pixels.length;
        isV = !isV;
    }

    private int[] buildPath() {
        int[] path = new int[W * H + 2];
        double[] dist = new double[W * H + 1];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[0] = 0;
        // first row connected to 0
        for (int x = 0; x < W; x++) relax(0, x + 1, path, dist);
        for (int y = 1; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int from = x + (y - 1) * W + 1;
                if (x > 0) relax(from, x + y * W, path, dist);
                relax(from, x + y * W + 1, path, dist);
                if (x < W - 1) relax(from, x + y * W + 2, path, dist);
            }
        }
        // last row connected to W*H+1
        double end = Double.POSITIVE_INFINITY;
        int endX = 0;
        for (int x = 0; x < W; x++) {
            if (end > dist[x + (H - 1) * W + 1]) {
                end = dist[x + (H - 1) * W + 1];
                endX = x;
            }
        }
        path[W * H + 1] = endX + (H - 1) * W + 1;
        return path;
    }

    private void relax(int from, int to, int[] path, double[] dist) {
        if (dist[to] > dist[from] + energy[to - 1]) {
            dist[to] = dist[from] + energy[to - 1];
            path[to] = from;
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (isV) transpose();
        removeSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (!isV) transpose();
        removeSeam(seam);
    }

    private void removeSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Null input");
        if (W <= 1) throw new IllegalArgumentException("No space to remove");
        if (seam.length != H) throw new IllegalArgumentException("Invalid seam");

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] >= W || (i < H - 1 && Math.abs(seam[i] - seam[i + 1]) > 1))
                throw new IllegalArgumentException("Invalid seam value");
        }
        int[][] newPixels = new int[H][W - 1];
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] > 0)
                System.arraycopy(pixels[i], 0, newPixels[i], 0, seam[i]);
            if (seam[i] < W - 1)
                System.arraycopy(pixels[i], seam[i] + 1, newPixels[i], seam[i], W - 1 - seam[i]);
        }
        pixels = newPixels;
        W--;
        double[] newEnergy = new double[W * H];
        for (int y = 0; y < seam.length; y++) {
            for (int x = 0; x < W; x++) {
                if (x < seam[y] - 1) newEnergy[x + y * W] = energy[x + y * (W + 1)];
                else if (x > seam[y]) newEnergy[x + y * W] = energy[x + 1 + y * (W + 1)];
                // only need to update the energy adjacent to the seam units
                else newEnergy[x + y * W] = isV ? energy(x, y) : energy(y, x);
            }
        }
        energy = newEnergy;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);
        // int[] seam = sc.findVerticalSeam();
        int[] seam = sc.findHorizontalSeam();
        StdOut.println("Seam:");
        for (int v : seam) StdOut.printf("%d ", v);
        Picture pic = sc.picture();
        StdOut.println("Pixels");
        for (int i = 0; i < pic.height(); i++) {
            for (int j = 0; j < pic.width(); j++) {
                StdOut.printf((i == seam[j] ? "%9d*\t" : "%9d\t"), pic.getRGB(j, i));
            }
            StdOut.println();
        }
        StdOut.println("Energy");
        for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
                StdOut.printf((i == seam[j] ? "%9.2f*\t" : "%9.2f\t"),
                              sc.energy[j + i * sc.width()]);
            }
            StdOut.println();
        }
        // sc.removeVerticalSeam(seam);
        sc.removeHorizontalSeam(seam);
        Picture picr = sc.picture();
        StdOut.println("Pixels");
        for (int i = 0; i < picr.height(); i++) {
            for (int j = 0; j < picr.width(); j++) {
                StdOut.printf("%9d\t", picr.getRGB(j, i));
            }
            StdOut.println();
        }
        StdOut.println("Energy");
        for (int i = 0; i < sc.height(); i++) {
            for (int j = 0; j < sc.width(); j++) {
                StdOut.printf("%9.2f\t", sc.energy[j + i * sc.width()]);
            }
            StdOut.println();
        }
    }
}
