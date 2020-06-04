/* *****************************************************************************
 *  Name: Qiucheng Wang
 *  Date: 02/14/2020
 *  Description: Assignment 3 Baseball Elimination
 *******************************************************************************/

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashMap;

public class BaseballElimination {

    private final int teamNum;
    private int capFromSrc;
    private final HashMap<String, Integer> teamMap;
    private final String[] teams;
    // stats[i]: team i's {win, loss, remain}
    private final int[][] stats;
    private final int[][] games;

    // create a baseball division from given filename in format: team_i win loss remain games[i]
    public BaseballElimination(String filename) {
        In in = new In(filename);
        teamNum = in.readInt();
        teamMap = new HashMap<String, Integer>();
        teams = new String[teamNum];
        stats = new int[teamNum][3];
        games = new int[teamNum][teamNum];
        int i = 0;
        while (!in.isEmpty()) {
            teams[i] = in.readString();
            for (int j = 0; j < 3; j++) stats[i][j] = in.readInt();
            for (int j = 0; j < teamNum; j++) games[i][j] = in.readInt();
            teamMap.put(teams[i], i++);
        }
    }

    // number of teams
    public int numberOfTeams() {
        return teamNum;
    }

    // all teams
    public Iterable<String> teams() {
        return teamMap.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!teamMap.containsKey(team)) throw new IllegalArgumentException("No such team");
        return stats[teamMap.get(team)][0];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!teamMap.containsKey(team)) throw new IllegalArgumentException("No such team");
        return stats[teamMap.get(team)][1];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!teamMap.containsKey(team)) throw new IllegalArgumentException("No such team");
        return stats[teamMap.get(team)][2];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!teamMap.containsKey(team1) || !teamMap.containsKey(team2))
            throw new IllegalArgumentException("No such team");
        return games[teamMap.get(team1)][teamMap.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!teamMap.containsKey(team)) throw new IllegalArgumentException("No such team");
        if (!trivialElimination(teamMap.get(team)).isEmpty()) return true;
        return (int) maxFlow(team).value() < capFromSrc;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!teamMap.containsKey(team)) throw new IllegalArgumentException("No such team");
        Bag<String> certificates = trivialElimination(teamMap.get(team));
        if (certificates.isEmpty()) {
            FordFulkerson maxFlow = maxFlow(team);
            // if all edges from s are full, then x is not eliminated
            if ((int) maxFlow.value() == capFromSrc) return null;
            // else any team vertices on the s side of min-cut eliminates x
            int x = teamMap.get(team);
            for (int i = 0; i < teamNum; i++)
                if (i != x && maxFlow.inCut(t2v(i, x))) certificates.add(teams[i]);
        }
        return certificates;
    }

    // trivial elimination:
    // maximum number of games team x can win is less than the number of wins of some other team i
    private Bag<String> trivialElimination(int x) {
        Bag<String> trivial = new Bag<String>();
        int maxWins = stats[x][0] + stats[x][2];
        for (int i = 0; i < teamNum; i++)
            if (maxWins < stats[i][0]) trivial.add(teams[i]);
        return trivial;
    }

    // n vertices allocation: s -> 0; t -> n-1;
    // target teamId: x; team vertices: [0,x) -> [1,x], [x+1,sz) -> [x+1,sz); game vertices: rest
    private FordFulkerson maxFlow(String team) {
        capFromSrc = 0;
        // s, t, sz-1 team vertices, (sz-1)*(sz-2)/2 game vertices
        int n = teamNum + 1 + (teamNum - 1) * (teamNum - 2) / 2;
        FlowNetwork flow = new FlowNetwork(n);
        int x = teamMap.get(team);
        int maxWins = stats[x][0] + stats[x][2];
        int v = 1;
        // teams vertices to t
        while (v < teamNum)
            flow.addEdge(new FlowEdge(v, n - 1, maxWins - stats[v2t(v++, x)][0]));
        // game vertices from s to team vertices
        for (int i = 0; i < teamNum - 1; i++) {
            for (int j = i + 1; j < teamNum; j++) {
                if (i == x || j == x) continue;
                capFromSrc += games[i][j];
                flow.addEdge(new FlowEdge(0, v, games[i][j]));
                flow.addEdge(new FlowEdge(v, t2v(i, x), Double.POSITIVE_INFINITY));
                flow.addEdge(new FlowEdge(v++, t2v(j, x), Double.POSITIVE_INFINITY));
            }
        }
        return new FordFulkerson(flow, 0, n - 1);
    }

    // team vertex id to team id
    private int v2t(int v, int x) {
        return v <= x ? v - 1 : v;
    }

    // team id to team vertex id
    private int t2v(int t, int x) {
        return t < x ? t + 1 : t;
    }

    //  unit testing
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) StdOut.print(t + " ");
                StdOut.println("}");
            }
            else StdOut.println(team + " is not eliminated");
        }
    }
}