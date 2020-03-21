package main;

import graph.*;
import solver.AllWaysSolver;
import solver.DCJ;
import solver.State;
import utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scannerGraph = new Scanner(new File("graph_building"))) {
            int cnt = scannerGraph.nextInt();
            int cntEdges = 0;
            List<Integer> sizes = new ArrayList<>();
            for (int ind = 0; ind < cnt; ind++) {
                int size = scannerGraph.nextInt();
                sizes.add(size);
                cntEdges += size;
            }
            cntEdges /= 2;
            Graph graph = new Graph(sizes);
            for (int ind = 0; ind < cntEdges; ind++) {
                Edge e0 = new Edge(scannerGraph.nextInt(), scannerGraph.nextInt());
                Edge e1 = new Edge(scannerGraph.nextInt(), scannerGraph.nextInt());
                Pair pair = new Pair(e0, e1);
                graph.glueEdges(pair);
            }

            // all ways solver checking -- generate all best states having graph structure
            // then find the distances between start gluing and each best one
            // choose one minimum and find the dcj operation to it

            AllWaysSolver solver = new AllWaysSolver(graph);
            List<DCJ> dcjOpers = solver.solve();
            for (DCJ dcj : dcjOpers) {
                System.out.println(dcj);
                System.out.println("------------------------");
            }
            System.out.println("distance is " + dcjOpers.size());

//            System.out.println("Before dcj");
//            for (Polygon polygon : graph.polygons) {
//                System.out.println("Polygon " + polygon.id + ":");
//                for (Vertex vertex : polygon.getVertices()) {
//                    System.out.print("  vertex " + vertex.id + " order = ");
//                    List<HalfEdge> order = vertex.cyclicOrder();
//                    for (HalfEdge halfEdge : order) {
//                        System.out.print(halfEdge.toString() + ", ");
//                    }
//                    System.out.println();
//                }
//                System.out.println();
//            }
//            System.out.println("answer is " + graph.calculate3dVertices());
//
//            System.out.println("all best states are ");
//            graph.genBestStates();
//

//            BEST GLUINGS GENERATION TEST
//            for (int s = 3; s < 4; s += 2) {
//                List<Edge> test1 = new ArrayList<>();
//                for (int i = 0; i < s; i++) {
//                    test1.add(new Edge(0, i));
//                }
//                for (int t = 3; t < 4; t += 2) {
//                    List<Edge> test2 = new ArrayList<>();
//                    for (int i = 0; i < t; i++) {
//                        test2.add(new Edge(1, i));
//                    }
//                    List<List<Pair>> allBestGlues = graph.genOddPolygons(test1, test2);
//
//                    for (List<Pair> oneBest : allBestGlues) {
//                        System.out.println(oneBest);
//                    }
//                    System.out.println("polygon with " + s + " vers, size = " + allBestGlues.size());
//                    System.out.println();
//                }
//            }


//            System.out.println(graph.genBestStates());
//            List<Integer> sizesGluings = new ArrayList<>();
//            sizesGluings.add(3);
//            sizesGluings.add(2);
//            sizesGluings.add(4);
//            System.out.println(graph.genAllChoiceOfGluings(sizesGluings));

//            System.out.println(graph.genAllOddPairs(indices));

//            Pair pair1 = new Pair(new Edge(0, 1), new Edge(2, 4));
//            Pair pair2 = new Pair(new Edge(1, 2), new Edge(2, 1));
//            Pair pair3 = new Pair(new Edge(0, 1), new Edge(2, 1));
//            Pair pair4 = new Pair(new Edge(1, 2), new Edge(2, 4));
//            List<Pair> cut = new ArrayList<>();
//            cut.add(pair1);
//            cut.add(pair2);
//            List<Pair> gl = new ArrayList<>();
//            gl.add(pair3);
//            gl.add(pair4);
////            0 1 2 4
////            1 2 2 1
////            0 1 1 2
////            2 4 2 1
//            graph.test(new DCJ(cut, gl));

//            AllWaysSolver solver = new AllWaysSolver(graph);
//            solver.buildTree(cntEdges);
//            System.out.println("dcj operations made " + (cntEdges - solver.bestHeight));
//            System.out.println("best possible answer is " + graph.bestAnswer());
//            System.out.println("best found answer is " + solver.maxVerticesCount + " 3d vertices");
//            System.out.println(solver.bestOperations.size() + " ways");
//            for (List<DCJ> way : solver.bestOperations) {
//                System.out.println("new way is");
//                for (DCJ dcj : way) {
//                    System.out.println(dcj);
//                    System.out.println("  ->");
//                }
//                System.out.println("\n");
//            }

//            try (PrintWriter writer = new PrintWriter(new File("cyclic_orders"));
//            try (Scanner scanner = new Scanner(new File("dcj_opers"))) {
//                State beforeSCJ = new State(graph.state.edges);
//
//                int cntOpers = scanner.nextInt();
//                for (int ind = 0; ind < cntOpers; ind++) {
//
//                    List<List<Pair>> edgesForDCJ = new ArrayList<>();
//                    for (int it = 0; it < 2; it++) {
//                        List<Pair> edgesTo = new ArrayList<>();
//                        for (int k = 0; k < 2; k++) {
//                            Edge e0 = new Edge(scanner.nextInt(), scanner.nextInt());
//                            Edge e1 = new Edge(scanner.nextInt(), scanner.nextInt());
//                            edgesTo.add(new Pair(e0, e1));
//                        }
//                        edgesForDCJ.add(edgesTo);
//                    }
//                    DCJ dcj = new DCJ(edgesForDCJ.get(0), edgesForDCJ.get(1));
//                    graph.doDCJ(dcj);
//                }
//
//                State afterDCJ = new State(graph.state.edges);
//                System.out.println("after dcj distance is " + beforeSCJ.distanceTo(afterDCJ));
//            }
//                    writer.println("after dcj");
//                    for (Polygon polygon : graph.polygons) {
//                        writer.println("Polygon " + polygon.id + ":");
//                        for (Vertex vertex : polygon.getVertices()) {
//                            writer.print("  vertex " + vertex.id + " order = ");
//                            List<HalfEdge> order = vertex.cyclicOrder();
//                            for (HalfEdge halfEdge : order) {
//                                writer.print(halfEdge.toString() + ", ");
//                            }
//                            writer.println();
//                        }
//                        writer.println();
//                    }
//                    System.out.println("answer is " + graph.calculate3dVertices());
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
