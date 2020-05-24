package test;

import graph.Edge;
import graph.Graph;
import solver.AllWaysSolver;
import solver.EvenPolygonsSolver;
import utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        TestGenerator t = new TestGenerator(6);
        try (Scanner scannerGraph = new Scanner(new File("tests"))) {
            while (scannerGraph.hasNext()) {
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

                AllWaysSolver allWaysSolver = new AllWaysSolver(graph.copy());
                EvenPolygonsSolver evenPolygonsSolver = new EvenPolygonsSolver(graph.copy());
                int allWays = allWaysSolver.solve().size();
                int evenPoly = evenPolygonsSolver.solve().size();
                if (allWays > evenPoly) {
                    System.out.println("CANNOT BE HERE");
                    System.out.println(graph.state);
                }
                if (allWays == evenPoly) {
                    System.out.println("OK");
                }
                if (allWays < evenPoly) {
                    System.out.println("EVEN SOLUTION IS BAD");
                    System.out.println(graph.state);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
