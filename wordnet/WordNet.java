/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/03/2020
 *  Description: Assignment 1 WordNet
 ******************************************************************************/

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class WordNet {
    private final Digraph G;
    private final SAP sap;
    private final HashMap<Integer, Queue<String>> idMap;
    private final HashMap<String, Bag<Integer>> wordMap;
    private int V;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        idMap = new HashMap<Integer, Queue<String>>();
        wordMap = new HashMap<String, Bag<Integer>>();
        readSynsets(synsets);
        G = new Digraph(V);
        readHypernyms(hypernyms);
        sap = new SAP(G);
        checkDAG();
        checkRoot();
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return wordMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException("Null input.");
        return wordMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException("is not noun");
        return sap.length(wordMap.get(nounA), wordMap.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        StringBuilder ancestors = new StringBuilder();
        for (String a : idMap.get(sap.ancestor(wordMap.get(nounA), wordMap.get(nounB)))) {
            ancestors.append(a).append(" ");
        }
        return ancestors.toString().trim();
    }

    // create map from synsets
    private void readSynsets(String synsets) {
        if (synsets == null) throw new IllegalArgumentException("synsets is null");
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String [] tokens = line.split(",");
            int id = Integer.parseInt(tokens[0]);
            String[] nouns = tokens[1].split(" ");
            Queue<String> words = new Queue<String>();
            for (int i = 0; i < nouns.length; i++) {
                words.enqueue(nouns[i]);
                Bag<Integer> ids = wordMap.getOrDefault(nouns[i], new Bag<Integer>());
                ids.add(id);
                wordMap.put(nouns[i], ids);
            }
            idMap.put(id, words);
            V++;
        }
    }

    // create a Digraph from hypernyms
    private void readHypernyms(String hypernyms) {
        if (hypernyms == null) throw new IllegalArgumentException("hypernyms is null");
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String [] tokens = line.split(",");
            int u = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                G.addEdge(u, Integer.parseInt(tokens[i]));
            }
        }
    }

    // check graph is a DAG;
    private void checkDAG() {
        DirectedCycle cycle = new DirectedCycle(G);
        if (cycle.hasCycle()) throw new IllegalArgumentException("G has cycle");
    }

    // check graph has one root
    private void checkRoot() {
        boolean root = false;
        for (int u = 0; u < V; u++) {
            if (G.outdegree(u) == 0) {
                if (root) throw new IllegalArgumentException("1+ roots");
                root = true;
            }
        }
        if (!root) throw new IllegalArgumentException("no root");
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet net = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String v = StdIn.readString();
            String w = StdIn.readString();
            StdOut.printf("length: %d, ancestor: %s\n", net.distance(v, w), net.sap(v, w));
        }
    }
}
