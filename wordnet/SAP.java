/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/03/2020
 *  Description: Assignment 1 WordNet
 ******************************************************************************/

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SAP {
    private final int V;
    private final Digraph G;
    private final Map<String, int[]> cache;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException("G is null");
        V = G.V();
        this.G = new Digraph(G);
        cache = new HashMap<>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        String h = hashPair(v, w);
        if (!cache.containsKey(h)) cache.put(h, findSAP(v, w));
        return cache.get(h)[0];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        String h = hashPair(v, w);
        if (!cache.containsKey(h)) cache.put(h, findSAP(v, w));
        return cache.get(h)[1];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        String h = hashPair(v, w);
        if (!cache.containsKey(h)) cache.put(h, findSAP(v, w));
        return cache.get(h)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        String h = hashPair(v, w);
        if (!cache.containsKey(h)) cache.put(h, findSAP(v, w));
        return cache.get(h)[1];
    }

    // check vertex is within G
    private void validate(Integer v) {
        if (v == null) throw new IllegalArgumentException("null value");
        if (v < 0 || v >= V) throw new IllegalArgumentException("out of bound");
    }

    private void validate(Iterable<Integer> it) {
        if (it == null) throw new IllegalArgumentException("Iterable is null");
        for (Integer v : it) validate(v);
    }

    // hashing pair of input
    private String hashPair(int v, int w) {
        return (v < w) ? v + " " + w : w + " " + v;
    }

    private String hashPair(Iterable<Integer> v, Iterable<Integer> w) {
        String vs = String.valueOf(v);
        String ws = String.valueOf(w);
        return (vs.compareTo(ws) < 0) ? vs + " " + ws : ws + " " + vs;
    }

    // return a pair of {length, ancestor}
    private int[] findSAP(int u, int v) {
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();
        q1.enqueue(u);
        q2.enqueue(v);
        return findSAP(q1, q2);
    }

    private int[] findSAP(Iterable<Integer> it1, Iterable<Integer> it2) {
        validate(it1);
        validate(it2);
        int[][] dist = new int[2][V];
        Arrays.fill(dist[0], Integer.MAX_VALUE);
        Arrays.fill(dist[1], Integer.MAX_VALUE);
        ArrayList<Queue<Integer>> q = new ArrayList<Queue<Integer>>();
        q.add(new Queue<Integer>());
        q.add(new Queue<Integer>());
        for (Integer v : it1) {
            dist[0][v] = 0;
            q.get(0).enqueue(v);
        }
        for (Integer v : it2) {
            if (dist[0][v] == 0) return new int[] {0, v};
            dist[1][v] = 0;
            q.get(1).enqueue(v);
        }
        int d = 1;
        int[] result = new int[] {Integer.MAX_VALUE, -1};
        while (!q.get(0).isEmpty() || !q.get(1).isEmpty()) {
            for (int i = 0; i < 2; i++) {
                if (!q.get(i).isEmpty()) {
                    Queue<Integer> nq = new Queue<Integer>();
                    for (int v : q.get(i)) {
                        for (int w : G.adj(v)) {
                            if (dist[i][w] != Integer.MAX_VALUE) continue;
                            dist[i][w] = d;
                            nq.enqueue(w);
                            if (dist[1-i][w] != Integer.MAX_VALUE && d + dist[1-i][w] < result[0]) {
                                result[0] = d + dist[1-i][w];
                                result[1] = w;
                            }
                        }
                    }
                    q.set(i, nq);
                }
            }
            if (++d >= result[0]) break;
        }
        if ((result[1]) == -1) result[0] = -1;
        return result;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        while (!StdIn.isEmpty()) {
            Digraph G = new Digraph(new In(StdIn.readString()));
            SAP sap = new SAP(G);
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
