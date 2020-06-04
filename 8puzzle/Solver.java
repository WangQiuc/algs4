/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/13/2020
 *******************************************************************************/

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class Solver {

    private class Status implements Comparable<Status> {

        private final Status prev;
        private final Board board;
        private final int moves;
        private final int f;

        public Status(Status prev, Board board, int moves) {
            this.prev = prev;
            this.board = board;
            this.moves = moves;
            this.f = moves + board.manhattan();
        }

        public int compareTo(Status that) {
            return Integer.compare(this.f, that.f);
        }
    }

    private Status solve = null;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Invalid input");
        // alternate explore two game trees: initial and its twin, only one is solvable
        ArrayList<MinPQ<Status>> duel = new ArrayList<MinPQ<Status>>();
        duel.add(new MinPQ<Status>(new Status[] { new Status(null, initial, 0) }));
        duel.add(new MinPQ<Status>(new Status[] { new Status(null, initial.twin(), 0) }));
        int i = 0;
        while (!duel.get(i).isEmpty()) {
            Status node = duel.get(i).delMin();
            if (node.board.isGoal()) {
                if (i == 0) solve = node;
                break;
            }
            for (Board neiBoard : node.board.neighbors()) {
                if (node.prev != null && neiBoard.equals(node.prev.board)) continue;
                duel.get(i).insert(new Status(node, neiBoard, node.moves + 1));
            }
            i = 1 - i;
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solve != null;
    }

    // min number of moves to solve initial board
    public int moves() {
        return (isSolvable()) ? solve.moves : -1;
    }

    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> stack = new Stack<Board>();
        Status node = solve;
        while (node != null) {
            stack.push(node.board);
            node = node.prev;
        }
        Queue<Board> sol = new Queue<Board>();
        while (!stack.isEmpty()) sol.enqueue(stack.pop());
        return sol;
    }

    // test client
    public static void main(String[] args) {

        // create initial board from file
        int n = StdIn.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = StdIn.readInt();
            }
        }
        Board initial = new Board(tiles);
        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Object board : solver.solution())
                StdOut.println(board.toString());
        }
    }

}
