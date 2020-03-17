package solver;

import graph.Graph;

import java.util.ArrayList;
import java.util.List;

public class AllWaysSolver {

    public Graph startGraph;
    public int maxVerticesCount;
    public int bestHeight; // the more the better
    public List<List<DCJ>> bestOperations;
    public int already = 0;
    public int bestVerticesResult;

    public AllWaysSolver(Graph graph) {
        this.startGraph = graph;
        maxVerticesCount = -1;
        bestHeight = Integer.MAX_VALUE;
        bestOperations = new ArrayList<>();
        this.bestVerticesResult = graph.bestAnswer();
        System.out.println("bestVerticesPossible is " + bestVerticesResult);
    }

    public void buildTree(int height) {
        // calculate the answer
        // if the answer better than current, set new bestOperations
        // if the answer equals to current then check the height
            // if it is better than current, set new
            // if it equals to current, add new operations
        // if height is 0 or best answer achieved then return

        // create all possible dcj
        // for every do the follow
            // do dcj
            // build tree with height - 1
            // undo dcj
        helpBuild(height, height, new ArrayList<>());
    }

    private void helpBuild(int height, int maxHeight, List<DCJ> opers) {
        already++;
        if (already % 100000 == 0) {
            System.err.println("already " + already + " graphs checked");
        }
        int newVerticesCount = startGraph.calculate3dVertices();
        if (newVerticesCount > maxVerticesCount) {
            maxVerticesCount = newVerticesCount;
            bestHeight = height;
            System.out.println("bestHeight now is " + bestHeight);
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
            System.err.println("    this is best configuration after " + (maxHeight - height) + " step(s)");
            System.out.println("    after checking " + already + " graphs");
            return;
        }
        if (height == 0) return;

        List<DCJ> possibleDCJs = startGraph.state.genAllChildrenDcj();
        for (DCJ dcj : possibleDCJs) {
            startGraph.doDCJ(dcj);
            List<DCJ> newOpers = new ArrayList<>(opers);
            newOpers.add(dcj);
            helpBuild(height - 1, maxHeight, newOpers);
            startGraph.undoDCJ(dcj);
        }
    }
}
