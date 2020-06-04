/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/17/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
    private static class Node {
        private final Point2D p;
        private final RectHV realm;
        private final boolean vert;
        private Node left;
        private Node right;

        public Node(Point2D p, RectHV realm, boolean vert) {
            this.p = p;
            this.realm = realm;
            this.vert = vert;
            this.left = null;
            this.right = null;
        }
    }

    private static final double DOT = 0.01;
    private static final double LINE = 0.001;
    private Node root;
    private int cnt;
    private final RectHV canvas;

    // construct an empty set of points
    public KdTree() {
        root = null;
        cnt = 0;
        canvas = new RectHV(0, 0, 1, 1);
    }

    // is the set empty?
    public boolean isEmpty() {
        return cnt == 0;
    }

    // number of points in the set
    public int size() {
        return cnt;
    }

    private boolean toLeft(Point2D p, Point2D q, boolean vert) {
        return vert ? p.x() < q.x() : p.y() < q.y();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        root = insert(root, p, canvas, true);
    }

    private Node insert(Node node, Point2D p, RectHV realm, boolean vert) {
        if (node == null) {
            cnt++;
            return new Node(p, realm, vert);
        }
        if (node.p.equals(p)) return node;

        RectHV nextRealm;
        if (toLeft(p, node.p, vert)) {
            nextRealm = (node.left != null) ? node.left.realm : subR(node.p, realm, true, vert);
            node.left = insert(node.left, p, nextRealm, !vert);
        }
        else {
            nextRealm = (node.right != null) ? node.right.realm : subR(node.p, realm, false, vert);
            node.right = insert(node.right, p, nextRealm, !vert);
        }
        return node;
    }

    private RectHV subR(Point2D p, RectHV realm, boolean left, boolean vert) {
        double x1 = (!left && vert) ? p.x() : realm.xmin();
        double y1 = (left || vert) ? realm.ymin() : p.y();
        double x2 = (left && vert) ? p.x() : realm.xmax();
        double y2 = (left && !vert) ? p.y() : realm.ymax();
        return new RectHV(x1, y1, x2, y2);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        if (node == null) return false;
        if (node.p.equals(p)) return true;
        return toLeft(p, node.p, node.vert) ? contains(node.left, p) : contains(node.right, p);
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    private void draw(Node node) {
        if (node == null) throw new IllegalArgumentException("Invalid input");
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(DOT);
        node.p.draw();
        StdDraw.setPenRadius(LINE);
        if (node.vert) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.realm.ymin(), node.p.x(), node.realm.ymax());
        }
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.realm.xmin(), node.p.y(), node.realm.xmax(), node.p.y());
        }
        if (node.left != null) draw(node.left);
        if (node.right != null) draw(node.right);

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("Invalid input");
        Queue<Point2D> points = new Queue<Point2D>();
        if (root != null) range(root, points, rect);
        return points;
    }

    private void range(Node node, Queue<Point2D> q, RectHV rect) {
        if (node.left != null && rect.intersects(node.left.realm)) range(node.left, q, rect);
        if (rect.contains(node.p)) q.enqueue(node.p);
        if (node.right != null && rect.intersects(node.right.realm)) range(node.right, q, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("Invalid input");
        Point2D nearest = null;
        double minSD = Double.POSITIVE_INFINITY;
        Stack<Node> q = new Stack<Node>();
        q.push(root);
        while (!q.isEmpty()) {
            Node node = q.pop();
            if (node == null || node.realm.distanceSquaredTo(p) >= minSD) continue;  // pruning
            double currSD = p.distanceSquaredTo(node.p);
            if (currSD < minSD) {
                minSD = currSD;
                nearest = node.p;
            }
            boolean left = toLeft(p, node.p, node.vert);
            q.push(left ? node.right : node.left);
            q.push(left ? node.left : node.right);
        }
        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);
        KdTree points = new KdTree();
        while (!in.isEmpty()) {
            Point2D p = new Point2D(in.readFloat(), in.readFloat());
            if (points.contains(p))
                StdOut.printf("Point (%f,%f) already in the set\n", p.x(), p.y());
            else points.insert(p);
        }
        StdOut.printf("Set contains %d points\n", points.size());
        points.draw();

        StdDraw.setPenRadius(LINE);
        StdDraw.setPenColor(StdDraw.GREEN);
        RectHV rect = new RectHV(0.3, 0.1, 0.6, 0.5);
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

