/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/01/2020
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private class Node {
        Item item;
        Node prev;
        Node next;
    }

    private int n;
    private Node head;
    private Node tail;

    // construct an empty deque
    public Deque() {
        n = 0;
        head = null;
        tail = null;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }


    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid input.");
        Node oldHead = head;
        head = new Node();
        head.item = item;
        if (isEmpty()) tail = head;
        else {
            head.next = oldHead;
            oldHead.prev = head;
        }
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) throw new IllegalArgumentException("Invalid input.");
        Node oldTail = tail;
        tail = new Node();
        tail.item = item;
        if (isEmpty()) head = tail;
        else {
            tail.prev = oldTail;
            oldTail.next = tail;
        }
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        Item item = head.item;
        Node oldHead = head;
        head = head.next;
        oldHead.next = null;
        n--;
        if (!isEmpty()) head.prev = null;
        else tail = null;
        return item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty.");
        Item item = tail.item;
        Node oldTail = tail;
        tail = tail.prev;
        oldTail.prev = null;
        n--;
        if (!isEmpty()) tail.next = null;
        else head = null;
        return item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node curr = head;

        public boolean hasNext() {
            return curr != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException("Reached the end.");
            Item item = curr.item;
            curr = curr.next;
            return item;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deque = new Deque<String>();
        // Text "to be or not to - be - - that - - is/0"
        StdOut.println("Removed:");
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (s.equals("-")) StdOut.println(deque.removeFirst());
            else deque.addLast(s);
        }
        StdOut.printf("%d nodes left:\n", deque.size());
        for (String s: deque) StdOut.println(s);
    }

}
