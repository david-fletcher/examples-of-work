import java.util.NoSuchElementException;

/**
 * PSKDTree is a Point collection that provides nearest neighbor searching using
 * 2d tree
 */
public class PSKDTree<Value> implements PointSearch<Value> {

    private Node root;
    private int size;
    private Point min, max;

    private class Node {
        Point p;
        Value v;
        Node left, right;
        Partition.Direction dir;

        public Node(Point p, Value v, Node left, Node right, Partition.Direction dir) {
            this.p = p;
            this.v = v;
            this.left = left;
            this.right = right;
            this.dir = dir;
        }
    }

    // constructor makes empty kD-tree
    public PSKDTree() {
        root = null;
    }

    // add the given Point to KDTree
    public void put(Point p, Value v) {
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        if(root == null) {
            root = new Node(p, v, null, null, Partition.Direction.LEFTRIGHT);
        } else {
            put(root, p, v);
        }

        if(min == null) { min = p; }
        else { min = Point.min(p, min); }

        if(max == null) { max = p; }
        else { max = Point.max(p, max); }

        size++;
    }

    private void put(Node root, Point p, Value v) {
        if(root.dir == Partition.Direction.LEFTRIGHT) {
            if(Point.xyComparator().compare(p, root.p) < 0) { // if the point is lesser in the horizontal direction
                if(root.left == null) {
                    root.left = new Node(p, v, null, null, Partition.Direction.DOWNUP);
                } else { put(root.left, p, v); }
            } else if(Point.xyComparator().compare(p, root.p) > 0) { // if the point is greater in the horizontal direction
                if(root.right == null) {
                    root.right = new Node(p, v, null, null, Partition.Direction.DOWNUP);
                } else { put(root.right, p, v); }
            } else { root.v = v; } // if we have a duplicate point, overwrite the value
        } else { // root.dir == Partition.Direction.DOWNUP
            if(Point.yxComparator().compare(p, root.p) < 0) { // if the point is lesser in the vertical direction
                if(root.left == null) {
                    root.left = new Node(p, v, null, null, Partition.Direction.LEFTRIGHT);
                } else { put(root.left, p, v); }
            } else if(Point.yxComparator().compare(p, root.p) > 0) { // if the point is greater in the vertical direction
                if(root.right == null) {
                    root.right = new Node(p, v, null, null, Partition.Direction.LEFTRIGHT);
                } else { put(root.right, p, v); }
            } else { root.v = v; } // if we have a duplicate point, overwrite the value
        }
    }

    public Value get(Point p) {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        if(root.p.equals(p)) { return root.v; }
        else { return get(root, p); }
    }

    private Value get(Node root, Point p) {
        if(root == null) { return null; }

        if(root.dir == Partition.Direction.LEFTRIGHT) {
            if(Point.xyComparator().compare(p, root.p) < 0) {
                return get(root.left, p);
            } else if(Point.xyComparator().compare(p, root.p) > 0) {
                return get(root.right, p);
            } else {
                return root.v;
            }
        } else { // root.dir == Partition.Direction.DOWNUP
            if(Point.yxComparator().compare(p, root.p) < 0) {
                return get(root.left, p);
            } else if(Point.yxComparator().compare(p, root.p) > 0) {
                return get(root.right, p);
            } else {
                return root.v;
            }
        }
    }

    public boolean contains(Point p) {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        if(root.p == p) { return true; }
        else { return contains(root, p); }
    }

    private boolean contains(Node root, Point p) {
        if(root == null) { return false; }

        if(root.dir == Partition.Direction.LEFTRIGHT) {
            if(Point.xyComparator().compare(p, root.p) < 0) {
                return contains(root.left, p);
            } else if(Point.xyComparator().compare(p, root.p) > 0) {
                return contains(root.right, p);
            } else {
                return true;
            }
        } else { // root.dir == Partition.Direction.DOWNUP
            if(Point.yxComparator().compare(p, root.p) < 0) {
                return contains(root.left, p);
            } else if(Point.yxComparator().compare(p, root.p) > 0) {
                return contains(root.right, p);
            } else {
                return true;
            }
        }
    }

    public Value getNearest(Point p) {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        return get(nearest(p));
    }

    // return an iterable of all points in collection
    public Iterable<Point> points() {
        Queue<Point> points = new Queue<>();
        traverseInOrder(root, points);
        return points;
    }

    private void traverseInOrder(Node root, Queue<Point> q) {
        if(root == null) { return; }
        traverseInOrder(root.left, q);
        q.enqueue(root.p);
        traverseInOrder(root.right, q);
    }

    // return an iterable of all partitions that make up the KDTree
    public Iterable<Partition> partitions() {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        Queue<Partition> q = new Queue<>();
        q.enqueue(new Partition(root.p.x(), max.y(), root.p.x(), min.y(), root.dir)); // root is always LEFTRIGHT
        partitions(root.left,  q, min, new Point(root.p.x(), max.y()));
        partitions(root.right, q, new Point(root.p.x(), min.y()), max);
        return q;
    }

    private void partitions(Node root, Queue<Partition> q, Point min, Point max) {
        if(root == null) { return; }

        if(root.dir == Partition.Direction.LEFTRIGHT) {
            q.enqueue(new Partition(root.p.x(), max.y(), root.p.x(), min.y(), root.dir));
            partitions(root.left,  q, min, new Point(root.p.x(), max.y()));
            partitions(root.right, q, new Point(root.p.x(), min.y()), max);
        } else { // root.dir == Partition.Direction.DOWNUP
            q.enqueue(new Partition(min.x(), root.p.y(), max.x(), root.p.y(), root.dir));
            partitions(root.left,  q, min, new Point(max.x(), root.p.y()));
            partitions(root.right, q, new Point(min.x(), root.p.y()), max);
        }
    }

    // return the Point that is closest to the given Point
    public Point nearest(Point p) {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        Point nearest = root.p;
        return nearest(root, p, nearest);
    }

    private Point nearest(Node root, Point p, Point nearest) {
        if(root == null) { return nearest; }

        if(nearest.dist(p) > root.p.dist(p)) { nearest = root.p; }

        if(root.dir == Partition.Direction.LEFTRIGHT) {
            if(Point.xyComparator().compare(p, root.p) < 0) {
                return nearest(root.left, p, nearest);
            } else if(Point.xyComparator().compare(p, root.p) > 0) {
                return nearest(root.right, p, nearest);
            } else {
                return p;
            }
        } else { // root.dir == Partition.Direction.DOWNUP
            if(Point.yxComparator().compare(p, root.p) < 0) {
                return nearest(root.left, p, nearest);
            } else if(Point.yxComparator().compare(p, root.p) > 0) {
                return nearest(root.right, p, nearest);
            } else {
                return p;
            }
        }

    }

    // return the k nearest Points to the given Point
    public Iterable<Point> nearest(Point p, int k) {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        if(p == null) { throw new NullPointerException("Point passed in as arguement is null!"); }
        MaxPQ<PointDist> pq = new MaxPQ<>();

        for(Point point : points()) {
            if(pq.isEmpty()) { pq.insert(new PointDist(point, point.dist(p))); }
            else if(pq.max().d() > point.dist(p)) {
                if(pq.size() >= k) { pq.delMax(); }
                pq.insert(new PointDist(point, point.dist(p)));
            }
        }

        Queue<Point> points = new Queue<>();
        for(PointDist i : pq) {
            points.enqueue(i.p());
        }

        return points;
    }

    // return the min and max for all Points in collection.
    // The min-max pair will form a bounding box for all Points.
    // if KDTree is empty, return null.
    public Point min() {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        return min;
    }
    public Point max() {
        if(isEmpty()) { throw new NoSuchElementException("The list is empty!"); }
        return max;
    }

    // return the number of Points in KDTree
    public int size() { return size; }

    // return whether the KDTree is empty
    public boolean isEmpty() { return size == 0; }

    // place your timing code or unit testing here
    public static void main(String[] args) {
        In in = new In("tests/input100K.txt");

        PSKDTree<String> ps = new PSKDTree<>();

        for(int i = 0; i < 100000; i++) {
            ps.put(new Point(in.readDouble(), in.readDouble()), "YO");
        }

        int[] avg = new int[5];

        for(int test = 0; test < 5; test++) {
            Stopwatch stopwatch = new Stopwatch();
            int numOps = 0;

            while (stopwatch.elapsedTime() < 1.0) {
                Point randomPoint = Point.uniform();
                ps.nearest(randomPoint);
                numOps++;
            }
            StdOut.println("Test: " + test + ", Ops: " + numOps);
            avg[test] = numOps;
        }

        int sum = 0;
        for(int i : avg) { sum += i; }
        double average = sum / 5.0;

        StdOut.println("Average: " + average);
    }

}
