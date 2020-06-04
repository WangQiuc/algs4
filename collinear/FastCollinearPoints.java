/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/08/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class FastCollinearPoints {

    private final LineSegment[] segments;

    // finds all line segments containing 4 points
    public FastCollinearPoints(Point[] points) {
        Point[] pointsNO = checkSort(points);
        int n = pointsNO.length;
        Point[] pointsSO = Arrays.copyOf(pointsNO, n);
        ArrayList<LineSegment> segmentList = new ArrayList<LineSegment>();
        for (int i = 0; i < n - 3; i++) {
            Point origin = pointsNO[i];
            Arrays.sort(pointsSO, origin.slopeOrder());
            int lo = 1;
            int hi = 2;
            while (lo < n - 2) {
                double k = origin.slopeTo(pointsSO[lo]);
                while (hi < n && Double.compare(k, origin.slopeTo(pointsSO[hi])) == 0) hi++;
                if (hi - lo > 2) {
                    Point begin = origin;
                    Point end = origin;
                    for (int j = lo; j < hi; j++) {
                        if (begin.compareTo(pointsSO[j]) > 0) begin = pointsSO[j];
                        if (end.compareTo(pointsSO[j]) < 0) end = pointsSO[j];
                    }
                    // avoid adding repeated segments
                    if (origin.compareTo(begin) == 0)
                        segmentList.add(new LineSegment(begin, end));
                }
                lo = hi++;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
