package test;

import com.sun.org.apache.xerces.internal.impl.xs.util.XSObjectListImpl;
import graph.Edge;
import solver.State;
import utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TestGenerator {
    private int sum;
    private List<List<Integer>> allWays;
    private List<List<Integer>> evenWays;

    public TestGenerator(int sum) {
        this.sum = sum;
//        allWays = new ArrayList<>();
//        go(sum, 1, 1, new ArrayList<>(), allWays);
        evenWays = new ArrayList<>();
        go(sum, 2, 2, new ArrayList<>(), evenWays);

        int ind = 4;
        List<List<Pair>> states = getGluing(evenWays.get(ind));
        System.out.println("gluings count for " + evenWays.get(ind) + " is " + states.size());
        for (List<Pair> state : states) {
            System.out.println(state);
        }
        try (PrintWriter writer = new PrintWriter(new File("tests"))) {
            for (List<Integer> sizes : evenWays) {
                for (List<Pair> state : getGluing(sizes)) {
                    writer.println(sizes.size());
                    for (int s : sizes) {
                        writer.print(s);
                        writer.print(' ');
                    }
                    writer.println();
                    for (Pair p : state) {
                        writer.print(p.first.polygonId);
                        writer.print(' ');
                        writer.print(p.first.edgeId);
                        writer.print(' ');
                        writer.print(p.second.polygonId);
                        writer.print(' ');
                        writer.print(p.second.edgeId);
                        writer.println();
                    }
                    writer.println();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestGenerator t = new TestGenerator(8);

    }

    private void go(int sumToGo, int minAdd, int diffAdd,
                    List<Integer> curWay, List<List<Integer>> ways) {
        if (sumToGo == 0) {
            ways.add(new ArrayList<>(curWay));
            return;
        }
        if (sumToGo < minAdd) {
            return;
        }
        curWay.add(minAdd);
        go(sumToGo - minAdd, minAdd, diffAdd, curWay, ways);
        curWay.remove(curWay.size() - 1);
        go(sumToGo, minAdd + diffAdd, diffAdd, curWay, ways);
    }

    private List<List<Pair>> getGluing(List<Integer> sizes) {
        int cnt = sizes.size();

        List<List<Edge>> edges = new ArrayList<>();
        List<List<Boolean>> hasMatched = new ArrayList<>();
        List<List<Integer>> parities = new ArrayList<>();
        List<List<Integer>> holeNumber = new ArrayList<>();
        List<List<Edge>> holes = new ArrayList<>();

        for (int i = 0; i < cnt; i++) {
            List<Edge> oneEdges = new ArrayList<>();
            List<Boolean> oneMatches = new ArrayList<>();
            List<Integer> oneParities = new ArrayList<>();
            List<Integer> oneHoles = new ArrayList<>();

            for (int j = 0; j < sizes.get(i); j++) {
                oneEdges.add(new Edge(i, j));
                oneMatches.add(false);
                oneParities.add(-1);
                oneHoles.add(-1);
            }
            edges.add(oneEdges);
            hasMatched.add(oneMatches);
            parities.add(oneParities);
            holeNumber.add(oneHoles);
        }

        setParities(parities.get(0), 0, 0);
        setHoles(holeNumber.get(0), 0);
        holes.add(edges.get(0));

        List<List<Pair>> all = new ArrayList<>();
        recursiveEvenGluing(edges, hasMatched, parities, holeNumber, holes, new ArrayList<>(), all);
        return all;
    }

    private void setHoles(List<Integer> polygon, int holeNumber) {
        for (int hn : polygon) {
            if (hn != -1) {
                throw new RuntimeException("parities of edges must set only once");
            }
        }
        
        for (int i = 0; i < polygon.size(); i++) {
            polygon.set(i, holeNumber);
        }
    }

    private void recursiveEvenGluing(List<List<Edge>> edges,
                                 List<List<Boolean>> hasMatches,
                                 List<List<Integer>> parities,
                                 List<List<Integer>> holeNumbers,
                                 List<List<Edge>> holes,    
                                 List<Pair> current,
                                 List<List<Pair>> all) {
        int p1 = -1;
        int e1 = -1;
        run: for (int i = 0; i < hasMatches.size(); i++) {
            for (int j = 0; j < hasMatches.get(i).size(); j++) {
                if (!hasMatches.get(i).get(j) && parities.get(i).get(j) != -1) {
                    p1 = i;
                    e1 = j;
                    break run;
                }
            }
        }
        if (p1 == -1) {
//            if (current.size() == sum / 2) {
//                all.add(new State(current));
//            }
            System.out.println("ADDED");

            all.add(new ArrayList<>(current));
            return;
        }
        if (parities.get(p1).get(e1) == -1) {
            System.out.println("CANNOT BE HERE");
        }
        System.out.println("p" + p1 + "e" + e1 + " searchs for pair");

        for (int p2 = 0; p2 < edges.size(); p2++) {
            for (int e2 = 0; e2 < edges.get(p2).size(); e2++) {
                boolean us = p1 == p2 && e1 == e2;
//                            System.out.println("aaa");
                if (us) continue;
//                            System.out.println("bbb");

                boolean hasNoPair = !hasMatches.get(p2).get(e2);
//                            System.out.println(hasNoPair);
//                            assert holeNumbers.get(p1).get(e1) != -1;
                boolean notInHole = holeNumbers.get(p2).get(e2) == -1;
                boolean sameHole = holeNumbers.get(p1).get(e1).equals(holeNumbers.get(p2).get(e2));
//                            assert parities.get(p1).get(e1) != -1;
                boolean hasNoParity = parities.get(p2).get(e2) == -1;
//                            assert notInHole && hasNoParity;
//                            assert sameHole && parities.get(p2).get(e2) != -1;
                boolean otherParity = !parities.get(p1).get(e1).equals(parities.get(p2).get(e2));

//                            if (hasNoPair &&
//                               (notInHole ||
//                                       sameHole && otherParity)) {
//
//                            }

                if (hasNoPair) {
                    System.out.println("p" + p2 + "e" + e2 + " has no pair");
                    System.out.println("      searching for p" + p1 + "e" + e1);
                    System.out.println("parities = " + parities);
                    System.out.println("notInHole " + notInHole);
                    System.out.println("sameHole " + sameHole);
                    System.out.println("otherParity " + otherParity);
                    System.out.println("holeNumbers = " + holeNumbers);
                    System.out.println("holes " + holes);
                    if (notInHole) {
                        System.out.println("it has no hole yet");
                        int hn = holeNumbers.get(p1).get(e1);
                        int par = parities.get(p1).get(e1);

                        List<Integer> prevParities = new ArrayList<>(parities.get(p2)); // every is -1
                        List<Integer> prevHoleNumbers = new ArrayList<>(holeNumbers.get(p2)); // every is -1
                        List<Edge> prevHole = new ArrayList<>(holes.get(hn));

                        hasMatches.get(p1).set(e1, true);
                        hasMatches.get(p2).set(e2, true);

                        setParities(parities.get(p2), 1 - par, e2);
                        setHoles(holeNumbers.get(p2), hn);
                        insertInHole(holes, edges.get(p2), holes.get(hn).size(), p1, e1, edges.get(p2).size(), p2, e2, hn);

                        current.add(new Pair(edges.get(p1).get(e1), edges.get(p2).get(e2)));
                        System.out.println("current " + current);
                        recursiveEvenGluing(edges, hasMatches, parities, holeNumbers, holes, current, all);

                        // return previous
                        hasMatches.get(p1).set(e1, false);
                        hasMatches.get(p2).set(e2, false);

                        parities.set(p2, prevParities);

                        holeNumbers.set(p2, prevHoleNumbers);

                        holes.set(hn, prevHole);

                        current.remove(current.size() - 1);
                    } else if (sameHole && otherParity) {
                        System.out.println("it has same hole and other parity");
//                                    if (sum / 2 - current.size() == 1)
                        int holesCnt = 0;
                        for (List<Edge> h : holes) {
                            if (h.size() > 0) holesCnt++;
                        }
                        boolean allParitiesExist = true;
                        free: for (List<Integer> ontParity : parities) {
                            for (int p : ontParity) {
                                if (p == -1) {
                                    allParitiesExist = false;
                                    break free;
                                }
                            }
                        }
                        if (holesCnt > 1 || allParitiesExist) {
                            System.out.println("can be glued");
                            System.out.println("holeNumbers = " + holeNumbers);

                            int hn = holeNumbers.get(p1).get(e1);
                            List<Edge> prevHole = new ArrayList<>(holes.get(hn));
                            List<List<Integer>> prevHolesNumbers = holeNumbers.stream().map(ArrayList::new).collect(Collectors.toList());

                            hasMatches.get(p1).set(e1, true);
                            hasMatches.get(p2).set(e2, true);

                            splitHole(holeNumbers, holes, hn, p1, e1, p2, e2);
                            System.out.println("holeNumbers after = " + holeNumbers);
                            System.out.println("holes after " + holes);

                            current.add(new Pair(edges.get(p1).get(e1), edges.get(p2).get(e2)));
                            System.out.println("current = " + current);
                            recursiveEvenGluing(edges, hasMatches, parities, holeNumbers, holes, current, all);

                            hasMatches.get(p1).set(e1, false);
                            hasMatches.get(p2).set(e2, false);

                            holeNumbers = prevHolesNumbers;
                            System.out.println("returned " + holeNumbers);

                            holes.set(hn, prevHole);
                            holes.remove(holes.size() - 1);
                            holes.remove(holes.size() - 1);

                            current.remove(current.size() - 1);
                        } else {
                            System.out.println("can't be glued");
                        }
                    } else {
                        System.out.println("cannot be glued");
                    }
                }
            }
        }
    }

    private void splitHole(List<List<Integer>> holeNumbers, List<List<Edge>> holes, int hn, int p1, int e1, int p2, int e2) {
        int ind1 = holes.get(hn).indexOf(new Edge(p1, e1));
        int ind2 = holes.get(hn).indexOf(new Edge(p2, e2));
        List<Edge> newHole1 = new ArrayList<>();
        List<Edge> newHole2 = new ArrayList<>();
        int indMin = Math.min(ind1, ind2);
        int indMax = Math.max(ind1, ind2);
//        if (indMin == 0 && indMax == 1 && holes.get(hn).size() == 2) {
//            System.out.println("HEREEEE");
//        }
        for (int i = indMin + 1; i < indMax; i++) {
            newHole1.add(holes.get(hn).get(i));
        }
        for (int i = indMax + 1; i < holes.get(hn).size(); i++) {
            newHole2.add(holes.get(hn).get(i));
        }
        for (int i = 0; i < indMin; i++) {
            newHole2.add(holes.get(hn).get(i));
        }

        for (int i = indMin + 1; i < indMax; i++) {
            Edge e = holes.get(hn).get(i);
            holeNumbers.get(e.polygonId).set(e.edgeId, holes.size());
        }
        holes.add(newHole1);

        for (int i = indMax + 1; i < holes.get(hn).size(); i++) {
            Edge e = holes.get(hn).get(i);
            holeNumbers.get(e.polygonId).set(e.edgeId, holes.size());
        }
        for (int i = 0; i < indMin; i++) {
            Edge e = holes.get(hn).get(i);
            holeNumbers.get(e.polygonId).set(e.edgeId, holes.size());
        }
        holes.add(newHole2);

//        holeNumbers.get(p1).set(e1, -2);
//        holeNumbers.get(p2).set(e2, -2);

        holes.set(hn, new ArrayList<>());
    }



    private void setParities(List<Integer> parities, int parity, int edgeId) {
        for (int p : parities) {
            if (p != -1) {
                throw new RuntimeException("parities of edges must set only once");
            }
        }

        for (int i = 0; i < parities.size(); i++) {
            int p = (i % 2 == edgeId % 2) ? parity : 1 - parity;
            parities.set(i, p);
        }
    }

    private void insertInHole(List<List<Edge>> holes, List<Edge> newPolygon, int holeSize1, int p1, int e1, int polygonSize2, int p2, int e2, int hn) {
        List<Edge> hole = holes.get(hn);
        int ind1 = holes.get(hn).indexOf(new Edge(p1, e1));
        int ind2 = newPolygon.indexOf(new Edge(p2, e2));
        System.out.println("HOLE = " + hole);
        List<Edge> newHole = new ArrayList<>();
        for (int i = 0; i < ind1; i++) {
            newHole.add(hole.get(i));
        }
        for (int i = ind2 + 1; i < polygonSize2; i++) {
            newHole.add(newPolygon.get(i));
        }
        for (int i = 0; i < ind2; i++) {
            newHole.add(newPolygon.get(i));
        }
        for (int i = ind1 + 1; i < holeSize1; i++) {
            newHole.add(hole.get(i));
        }
        System.out.println("NEW POLYGON = " + newPolygon);
        System.out.println("NEW HOLE = " + newHole);
        holes.set(hn, newHole);
    }
}
