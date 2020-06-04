/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/29/2020
 *  Description: Assignment 5 Burrows–Wheeler
 ******************************************************************************/

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront {

    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList<Integer> seq = new LinkedList<Integer>();
        for (int i = 0; i < R; i++) seq.add(i);
        while (!BinaryStdIn.isEmpty()) {
            int x = seq.indexOf((int) BinaryStdIn.readChar());
            BinaryStdOut.write(x, 8);
            seq.add(0, seq.remove(x));
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList<Integer> seq = new LinkedList<Integer>();
        for (int i = 0; i < R; i++) seq.add(i);
        while (!BinaryStdIn.isEmpty()) {
            int x = seq.remove(BinaryStdIn.readChar());
            BinaryStdOut.write((char) x, 8);
            seq.add(0, x);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
    }
}
