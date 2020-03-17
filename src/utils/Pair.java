package utils;

import graph.Edge;

import java.util.ArrayList;
import java.util.List;

public class Pair {
    public Edge first;
    public Edge second;

    public Pair(Edge first, Edge second) {
        if (Edge.comparator.compare(first, second) < 0) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
    }

    public boolean equals(Pair pair) {
        return this.first.equals(pair.first) && this.second.equals(pair.second)
                || this.first.equals(pair.second) && this.second.equals(pair.first);
    }

    public String toString() {
        return "Pair of edge " + this.first.toString() + " and edge " + this.second.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static List<List<Pair>> shuffle(List<Pair> toShuffle) {
        Pair firstPair = toShuffle.get(0);
        Pair secondPair = toShuffle.get(1);
        List<List<Pair>> newOrder = new ArrayList<>();

        // f + f, s + s
        {
            Pair ff = new Pair(firstPair.first, secondPair.first);
            Pair ss = new Pair(firstPair.second, secondPair.second);
            List<Pair> newGlueFirst = new ArrayList<>();
            newGlueFirst.add(ff);
            newGlueFirst.add(ss);
            newOrder.add(newGlueFirst);
        }
        // f + s, s + f
        {
            Pair fs = new Pair(firstPair.first, secondPair.second);
            Pair sf = new Pair(firstPair.second, secondPair.first);
            List<Pair> newGlueSecond = new ArrayList<>();
            newGlueSecond.add(fs);
            newGlueSecond.add(sf);
            newOrder.add(newGlueSecond);
        }

        return newOrder;
    }
}
