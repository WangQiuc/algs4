/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/01/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] q;
    private int n = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        q = (Item[]) new Object[1];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid input.");
        q[n++] = item;
        if (n > 0 && n == q.length) resize(n << 1);
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        int i = StdRandom.uniform(n);
        Item item = q[i];
        swap(i, --n);
        q[n] = null;
        if (n > 0 && n == q.length >> 2) resize(n << 1);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        return q[StdRandom.uniform(n)];
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            copy[i] = q[i];
        }
        q = copy;

    }

    // swap two elements in the array q
    private void swap(int i, int j) {
        Item temp = q[i];
        q[i] = q[j];
        q[j] = temp;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {

        private int i = 0;
        private int[] idx;

        public ArrayIterator() {
            idx = new int[n];
            for (; i < n; i++) {
                idx[i] = i;
            }
            StdRandom.shuffle(idx);
        }

        public boolean hasNext() {
            return i > 0;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("Reached the end.");
            i--;
            return q[idx[i]];
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> q = new RandomizedQueue<String>();
        // Text "1 2 3 4 5 - 6 - - 7 - - 8/0"
        StdOut.println("Dequeued:");
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (s.equals("-")) StdOut.println(q.dequeue());
            else q.enqueue(s);
        }
        StdOut.printf("%d nodes left:\n", q.size());
        for (String s : q) StdOut.println(s);
    }

}
