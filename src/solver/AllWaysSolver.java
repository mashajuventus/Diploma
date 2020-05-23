package solver;

import graph.Edge;
import graph.Graph;
import utils.Pair;

import java.util.*;

public class AllWaysSolver {

    public Graph startGraph;
    public int maxVerticesCount;
    public int bestHeight; // the more the better
    public List<List<DCJ>> bestOperations;
    public int already = 0;
    public int bestVerticesResult;
    public long closestBestStates = 0;
    public List<State> bestStates = new ArrayList<>();
    public boolean isFirst = true;

    public AllWaysSolver(Graph graph) {
        this.startGraph = graph;
        maxVerticesCount = -1;
        bestHeight = Integer.MAX_VALUE;
        bestOperations = new ArrayList<>();
        bestVerticesResult = graph.bestAnswer();
//        System.out.println("bestVerticesPossible is " + bestVerticesResult);
    }

    public void buildTree(int height) {
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

    public List<DCJ> solve() {
//        List<State> bestStates = startGraph.genBestStates();

        // find one closest state
        closestBestStates = 0;
        int bestDistance = Integer.MAX_VALUE;
        State bestState = null;
        List<State> newBestStates = new ArrayList<>();

        if (isFirst) {
//            System.out.println("first time");
            isFirst = false;
            // from genBestStates
            List<List<List<Pair>>> gluingsForOddAndEven = startGraph.yeildBestStates();
            List<Integer> polSizes = new ArrayList<>();
            for (List<List<Pair>> onePolygon : gluingsForOddAndEven) {
                polSizes.add(onePolygon.size());
            }
            List<List<Integer>> whichGluingForEveryPolygons = startGraph.genAllChoiceOfGluings(polSizes);
//            int l = 0;
            for (List<Integer> indices : whichGluingForEveryPolygons) {
//                if (l % 50000 == 0) {
//                    System.out.println(l + " ops");
//                }
                List<Pair> oneGluing = new ArrayList<>();
                for (int i = 0; i < indices.size(); i++) {
                    int indOfPol = indices.get(i);
                    oneGluing.addAll(gluingsForOddAndEven.get(i).get(indOfPol));
                }
//                l++;
//            System.out.println();
//            System.out.println(oneGluing);
//            System.out.println();
                State state = new State(oneGluing);
//            answer.add(new State(oneGluing));
//        }

//        for (State state : startGraph.genBestStates()) {
//            int thisStateDistance = startGraph.state.distanceTo(state, false);
                List<DCJ> way = wayToBestGlue(startGraph.state, state);
                int thisStateDistance = way.size();
                if (thisStateDistance < bestDistance) {
                    bestDistance = thisStateDistance;
                    bestState = state;
                    closestBestStates = 0;
                    newBestStates = new ArrayList<>();
                }
                if (thisStateDistance == bestDistance) {
                    closestBestStates++;
                    newBestStates.add(state);
                }
            }
        } else {
//            System.out.println("only from previous");
            for (State state : bestStates) {
//            int thisStateDistance = startGraph.state.distanceTo(state, false);
                List<DCJ> way = wayToBestGlue(startGraph.state, state);
                int thisStateDistance = way.size();
                if (thisStateDistance < bestDistance) {
                    bestDistance = thisStateDistance;
                    bestState = state;
                    closestBestStates = 0;
                    newBestStates = new ArrayList<>();
                }
                if (thisStateDistance == bestDistance) {
                    closestBestStates++;
                    newBestStates.add(state);
                }
            }
        }
        bestStates = newBestStates;
        return wayToBestGlue(startGraph.state, bestState);
    }

    public List<DCJ> wayToBestGlue(State currentState, State bestState) {
        Map<Edge, Edge> currentEdges = currentState.stateToMap();
        Map<Edge, Edge> bestEdges = bestState.stateToMap();

        if (!new HashSet<>(currentEdges.keySet()).equals(new HashSet<>(bestEdges.keySet())))
            throw new AssertionError();

        Map<Edge, Boolean> isInCycle = new HashMap<>();
        for (Edge edge : currentEdges.keySet()) {
            isInCycle.put(edge, false);
        }

        List<List<Edge>> cycles = new ArrayList<>();
        while (true) {
            boolean hasUnmatched = false;
            // find some unmatched
            Edge unmatchedEdge = null;
            for (Map.Entry<Edge, Boolean> edgeBooleanEntry : isInCycle.entrySet()) {
                if (!edgeBooleanEntry.getValue()) {
                    unmatchedEdge = edgeBooleanEntry.getKey();
                    hasUnmatched = true;
                    break;
                }
            }
//            System.out.println("unmatched edge is " + unmatchedEdge);
            if (hasUnmatched) {
                List<Edge> currentCycle = new ArrayList<>();
                boolean previousInCurrent = false;
                while (!isInCycle.get(unmatchedEdge)) {
                    currentCycle.add(unmatchedEdge);
                    isInCycle.put(unmatchedEdge, true);
                    unmatchedEdge = (previousInCurrent) ? bestEdges.get(unmatchedEdge) : currentEdges.get(unmatchedEdge);
                    previousInCurrent = !previousInCurrent;
//                    System.out.println("  next unmatched is " + unmatchedEdge);
                }
                if (currentCycle.size() > 2) {
                    cycles.add(currentCycle);
                }
            } else {
                break;
            }
        }

        List<DCJ> answer = new ArrayList<>();
        for (List<Edge> cycle : cycles) {
            while (cycle.size() > 2) {
                Edge e0 = cycle.get(0);
                Edge e1 = cycle.get(1);
                Edge e2 = cycle.get(2);
                Edge e3 = cycle.get(3);

                List<Pair> toCut = new ArrayList<>();
                toCut.add(new Pair(e0, e1));
                toCut.add(new Pair(e2, e3));

                List<Pair> toGlue = new ArrayList<>();
                toGlue.add(new Pair(e1, e2));
                toGlue.add(new Pair(e0, e3));

                cycle.remove(e1);
                cycle.remove(e2);

                answer.add(new DCJ(toCut, toGlue));
            }
        }
        return answer;
    }
}
