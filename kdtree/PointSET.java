/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/17/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;


public class PointSET {

    private final TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET()  {
        set = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size()  {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Invalid input");
        Queue<Point2D> points = new Queue<Point2D>();
        for (Point2D q : set) {
            if (rect.contains(q)) points.enqueue(q);
        }
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p)  {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        Point2D nearest = null;
        double minSD = Double.POSITIVE_INFINITY;
        for (Point2D q : set) {
            double currSD = p.distanceSquaredTo(q);
            if (currSD < minSD) {
                minSD = currSD;
                nearest = q;
            }
        }
        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);
        PointSET points = new PointSET();
        while (!in.isEmpty()) {
            Point2D p = new Point2D(in.readFloat(), in.readFloat());
            if (points.contains(p)) StdOut.printf("Point (%f,%f) already in the set\n", p.x(), p.y());
            else points.insert(p);
        }
        StdOut.printf("Set contains %d points\n", points.size());
        StdDraw.setPenRadius(0.01);
        points.draw();

        StdDraw.setPenColor(StdDraw.BLUE);
        RectHV rect = new RectHV(0.01, 0.01, 0.7, 0.7);
        rect.draw();
        StdOut.printf("Points in the rectangle:\n");
        for (Point2D p : points.range(rect)) {
            StdOut.printf("%f %f\n", p.x(), p.y());
        }

        Point2D p = new Point2D(0.1, 0.3);
        StdDraw.setPenColor(StdDraw.RED);
        p.draw();
        Point2D q = points.nearest(p);
        StdDraw.setPenColor(StdDraw.GREEN);
        q.draw();
        StdOut.printf("Nearest points to the blue dot: (%f, %f)\n", q.x(), q.y());
    }
}
