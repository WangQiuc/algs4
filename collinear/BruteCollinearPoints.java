/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/08/2020
 *******************************************************************************/

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private final LineSegment[] segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        Point[] pointsNO = checkSort(points);
        int n = pointsNO.length;
        ArrayList<LineSegment> segmentList = new ArrayList<LineSegment>();
        for (int a = 0; a < n - 3; a++) {
            for (int b = a + 1; b < n - 2; b++) {
                for (int c = b + 1; c < n - 1; c++) {
                    for (int d = c + 1; d < n; d++) {
                        double k1 = pointsNO[a].slopeTo(pointsNO[b]);
                        double k2 = pointsNO[a].slopeTo(pointsNO[c]);
                        if (Double.compare(k1, k2) == 0
                                && Double.compare(k1, pointsNO[a].slopeTo(pointsNO[d])) == 0) {
                            segmentList.add(new LineSegment(pointsNO[a], pointsNO[d]));
                        }
                    }
                }
            }
        }
        segments = segmentList.toArray(new LineSegment[0]);
    }

    // check if "points" is illegal and then sort it by natural order
    private Point[] checkSort(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Null constructor");
        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException("Null point");
        }
        Point[] pointsNO = Arrays.copyOf(points, points.length);
        Arrays.sort(pointsNO);
        for (int i = 1; i < points.length; i++) {
            if (pointsNO[i - 1].compareTo(pointsNO[i]) == 0)
                throw new IllegalArgumentException("Repeated point");
        }
        return pointsNO;
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments().length;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] copy = Arrays.copyOf(segments, segments.length);
        return copy;
    }

    public static void main(String[] args) {

        // read the n points from a file
        int n = StdIn.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(StdIn.readInt(), StdIn.readInt());
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
