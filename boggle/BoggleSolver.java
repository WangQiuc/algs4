/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/20/2020
 *  Description: Assignment 4 Boggle Solver
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BoggleSolver
{
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    private static final int R = 26;  // 26 English Letters
    private static final int Q = 16;  // int index of Q (Qu)
    private int m;                    // number of rows
    private int n;                    // number of columns
    private int[][] boardVal;         // the m-by-n array of int
    private HashMap<String, Integer> boardWords; // cache the precomputed searched word in boggle
    private final Node root;          // root of trie

    private static class Node {
        private boolean end = false;
        private final Node[] next = new Node[R];
    }

    public BoggleSolver(String[] dictionary) {
        // build trie from dictionary
        root = new Node();
        for (String word : dictionary) {
            if (!checkQu(word)) continue;
            Node x = root;
            int i = 0;
            while (i < word.length()) {
                int j = c2i(word.charAt(i++));
                if (j == Q) i++;        // "Q" replaces "Qu" in trie
                if (x.next[j] == null) x.next[j] = new Node();
                x = x.next[j];
            }
            x.end = true;
        }
        boardWords = new HashMap<String, Integer>();
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        m = board.rows();
        n = board.cols();
        boardVal = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                boardVal[i][j] = c2i(board.getLetter(i, j));
            }
        }
        boardWords = new HashMap<String, Integer>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                search(root, "", i, j);
            }
        }
        return boardWords.keySet();
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int d = (boardWords.containsKey(word)) ? boardWords.get(word) : match(root, word, 0);
        if (d > 7) return 11;
        return Math.max(0, d - 2 - (d == 3 || d == 7 ? 0 : 1));
    }

    private int c2i(char c) {
        return c - 'A';
    }

    private char i2c(int i) {
        return (char) ('A' + i);
    }

    // check whether a 'Q' in the word is followed with a 'U'
    private boolean checkQu(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == 'Q' && (i == word.length()-1 || word.charAt(i+1) != 'U'))
                return false;
        }
        return true;
    }

    // dfs in the boggle
    private void search(Node x, String prefix, int i, int j) {
        int k = boardVal[i][j];
        x = x.next[k];
        if (x == null) return;
        prefix += i2c(k);
        if (k == Q) prefix += "U";
        boardVal[i][j] = -1;        // avoid use one dice more than once
        if (x.end && prefix.length() > 2) boardWords.put(prefix, prefix.length());
        for (int ni = i-1; ni < i+2; ni++) {
            for (int nj = j-1; nj < j+2; nj++) {
                if (ni >= 0 && ni < m && nj >= 0 && nj < n && boardVal[ni][nj] >= 0)
                    search(x, prefix, ni, nj);
            }
        }
        boardVal[i][j] = k;
    }

    // match a word in the trie
    private int match(Node x, String word, int i) {
        if (!checkQu(word)) return 0;
        if (i == word.length()) return x.end ? i : 0;
        int k = c2i(word.charAt(i));
        x = x.next[k];
        if (x == null) return 0;
        return k == Q ? match(x, word, i+2) : match(x, word, i+1);
    }

    // unit testing
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}