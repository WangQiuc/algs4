/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/03/2020
 *  Description: Assignment 1 WordNet
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet net;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        net = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int n = nouns.length;
        int[] distances = new int[n];
        for (int i = 0; i < n-1; i++) {
            for (int j = i+1; j < n; j++) {
                int d = net.distance(nouns[i], nouns[j]);
                distances[i] += d;
                distances[j] += d;
            }
        }
        String outcast = "";
        int maxD = 0;
        for (int i = 0; i < n; i++) {
            if (maxD < distances[i]) {
                maxD = distances[i];
                outcast = nouns[i];
            }
        }
        return outcast;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
