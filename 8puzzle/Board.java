/* *****************************************************************************
 *  Name:              Qiucheng Wang
 *  Coursera User ID:  bb0fc6db707edd85177f6ea1b9d64f12
 *  Last modified:     01/13/2020
 *******************************************************************************/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int n;
    private int i0;
    private int j0;
    private final int[][] board;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException("Invalid input");
        n = tiles.length;
        board = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    i0 = i;
                    j0 = j;
                }
                board[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder s = new StringBuilder(Integer.toString(n) + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(board[i][j] + "\t");
            }
            s.append("\n");
        }
        return s.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // return manhattan distance of cell(i, j) from its goal
    // return 0 if current cell is the blank square
    private int dist(int i, int j) {
        int v = board[i][j] - 1;
        return (v < 0) ? 0 : Math.abs(v / n - i) + Math.abs(v % n - j);
    }

    // number of tiles out of place
    public int hamming() {
        int d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (dist(i, j) > 0) d++;
            }
        }
        return d;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int d = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d += dist(i, j);
            }
        }
        return d;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null || this.getClass() != y.getClass()) return false;
        Board boardY = (Board) y;
        if (boardY.dimension() != n) return false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] != boardY.board[i][j]) return false;
            }
        }
        return true;

    }

    // make one swap between (i1, j1) and (i2, j2) and generate a new board
    private Board swapBoard(int i1, int j1, int i2, int j2) {
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == i1 && j == j1) tiles[i][j] = board[i2][j2];
                else if (i == i2 && j == j2) tiles[i][j] = board[i1][j1];
                else tiles[i][j] = board[i][j];
            }
        }
        return new Board(tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        Queue<Board> neiBoards = new Queue<Board>();
        if (i0 > 0) neiBoards.enqueue(swapBoard(i0, j0, i0-1, j0));
        if (i0 < n-1) neiBoards.enqueue(swapBoard(i0, j0, i0+1, j0));
        if (j0 > 0) neiBoards.enqueue(swapBoard(i0, j0, i0, j0-1));
        if (j0 < n-1) neiBoards.enqueue(swapBoard(i0, j0, i0, j0+1));
        return neiBoards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int i = 1 - (i0 & 1);
        int sj = 0;
        int dj = 1;
        return swapBoard(i, sj, i, dj);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        int [][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = StdIn.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.print("Board string representation:\n");
        StdOut.print(board.toString());
        StdOut.printf("Blank square postion:(%d, %d)\n", board.i0, board.j0);
        StdOut.printf("H dist: %d; M dist: %d\n", board.hamming(), board.manhattan());
        StdOut.print("Test neighbor boards:\n");
        for (Board neiBoard : board.neighbors()) {
            StdOut.print(neiBoard.toString());
        }
        StdOut.print("Test a twin board:\n");
        StdOut.print(board.twin().toString());

    }

}
