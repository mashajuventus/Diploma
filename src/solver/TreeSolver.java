package solver;

import graph.Graph;
import graph.Polygon;

import java.util.ArrayList;
import java.util.List;

public class TreeSolver {
    public Graph startGraph;
    public int maxVerticesCount;
    public int bestHeight; // the more the better
    public List<List<DCJ>> bestOperations;
    public int already = 0;
    public int bestVerticesResult;
    public long closestBestStates = 0;
    public List<State> bestStates = new ArrayList<>();
    public boolean isFirst = true;
    private long startTime;

    public TreeSolver(graph.Graph graph) {
        this.startGraph = graph;
        maxVerticesCount = -1;
//        bestHeight = Integer.MAX_VALUE;
        bestOperations = new ArrayList<>();
        bestVerticesResult = graph.bestAnswer();
        startTime = System.currentTimeMillis();
//        System.out.println("bestVerticesPossible is " + bestVerticesResult);
    }

    public List<DCJ> solve() {
//        System.out.println("here");
        int cntEdges = 0;
        for (Polygon polygon : startGraph.polygons) {
            cntEdges += polygon.size;
        }
        bestHeight = cntEdges / 2 - 1;
        buildTree();
        if (bestOperations.isEmpty()) {
            throw new RuntimeException("should find at least one best state");
        }
        return bestOperations.get(0);
    }

    public void buildTree() {
        // calculate the answer
        // if the answer better than current, set new bestOperations
        // if the answer equals to current then check the height
        // if it is better than current, set new
        // if it equals to current, add new operations
        // if height is 0 or best answer is achieved then return

        // create all possible dcj
        // for every do the follow
        // do dcj
        // build tree with height - 1
        // undo dcj
        helpBuild(bestHeight, new ArrayList<>());
    }

    private void helpBuild(int height, List<DCJ> opers) {
        already++;
        if (already % 10000000 == 0) {
            System.out.println("    " + already + " graphs checked");
        }
        int newVerticesCount = startGraph.calculate3dVertices();
        if (newVerticesCount > maxVerticesCount) {
            maxVerticesCount = newVerticesCount;
            bestHeight = height;
//            System.out.println("bestHeight now is " + bestHeight);
            bestOperations = new ArrayList<>();
            bestOperations.add(opers);
        } else if (newVerticesCount == maxVerticesCount) {
            if (height > bestHeight) { // it means we did CNT - height operations
                bestHeight = height;
                bestOperations = new ArrayList<>();
                bestOperations.add(opers);
            } else if (height == bestHeight) {
                bestOperations.add(opers);
            }
        }

        if (newVerticesCount == bestVerticesResult && height >= bestHeight) {
//            System.err.println("    this is best configuration after " + (maxHeight - height) + " step(s)");
//            System.out.println("    after checking " + already + " graphs");
            return;
        }
        if (height == 0) return;

        List<DCJ> possibleDCJs = startGraph.state.genAllChildrenDcj();
        for (DCJ dcj : possibleDCJs) {
            startGraph.doDCJ(dcj);
            List<DCJ> newOpers = new ArrayList<>(opers);
            newOpers.add(dcj);
            helpBuild(height - 1, newOpers);
            startGraph.undoDCJ(dcj);
        }
    }

}
