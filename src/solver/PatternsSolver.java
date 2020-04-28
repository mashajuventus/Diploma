package solver;

import graph.Graph;
import graph.Polygon;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class PatternsSolver {
    public Graph startGraph;
    public List<State> allBestStates;
    public List<Integer> startDistances;
    private AllWaysSolver sol;

    public PatternsSolver(Graph graph) {
        startGraph = graph;
        allBestStates = startGraph.genBestStates();
        startDistances = new ArrayList<>();
        sol = new AllWaysSolver(startGraph);
        for (State state : allBestStates) {
            int x = sol.wayToBestGlue(startGraph.state, state).size();
            startDistances.add(x);
        }
//        System.out.println("startDistances are");
//        System.out.println(startDistances);
    }

//    public <T> T argmax(List<T> ts, Function<T, Integer> func) {
//        T res = null;
//        Integer resValue = null;
//        for (T t : ts) {
//            Integer tValue = func.apply(t);
//            if (res == null || resValue < tValue) {
//                res = t;
//                resValue = tValue;
//            }
//        }
//        return res;
//    }

    public void solve(int bestAnswer) {
        Polygon specialPolygon = maxSizeEvenPolygon();
        Graph graph = startGraph.copy();
        helper(graph, specialPolygon, 0, new ArrayList<>(), bestAnswer);
    }

    private void helper(Graph graph, Polygon specialPolygon, int depth, List<DCJ> opers, int bestAnswer) {
        List<DCJ> potentialDCJs = potentialDCJs(graph, specialPolygon);
//        System.out.println("depth = " + depth);
//        System.out.println(potentialDCJs);
        if (potentialDCJs.size() == 0) {
//            System.out.println("after " + depth + " steps distances are");
//            List<Integer> newDistances = new ArrayList<>();
//            for (State state : allBestStates) {
//                int len = sol.wayToBestGlue(graph.state, state).size();
//                newDistances.add(len);
//                if (len == 0) {
//                    System.out.println("depth is " + depth);
//                    System.out.println(opers);
//                }
//            }
//            System.out.println(newDistances);
//            System.out.println("then");
            AllWaysSolver solver = new AllWaysSolver(graph);
//            System.out.println();
            System.out.println(solver.solve().size() + depth);
            return;
        } else {
//            System.out.println("else");
            if (depth >= bestAnswer) {
                System.out.print("-");
                return;
            }
            for (DCJ dcj : potentialDCJs) {
                graph.doDCJ(dcj);
                opers.add(dcj);
                helper(graph, specialPolygon, depth + 1, opers, bestAnswer);
                opers.remove(opers.size() - 1);
                graph.undoDCJ(dcj);
            }
        }
    }

    public Polygon maxSizePolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size > specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    public Polygon minSizePolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size < specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    public Polygon maxSizeEvenPolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size % 2 == 0 && polygon.size > specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    public Polygon maxSizeOddPolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size % 2 == 1 && polygon.size > specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    public Polygon minSizeEvenPolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size % 2 == 0 && polygon.size < specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    public Polygon minSizeOddPolygon() {
        Graph patternGraph = startGraph.copy();
        Polygon specialPolygon = patternGraph.polygons.get(0);
        int specialPolygonSize = patternGraph.polygons.get(0).size;
        for (Polygon polygon : patternGraph.polygons) {
            if (polygon.size % 2 == 1 && polygon.size < specialPolygonSize) {
                specialPolygonSize = polygon.size;
                specialPolygon = polygon;
            }
        }
        return specialPolygon;
    }

    private List<DCJ> potentialDCJs(Graph graph, Polygon specialSizePolygon) {
        List<DCJ> answer = new ArrayList<>();
        int add = (specialSizePolygon.size % 2 == 0) ? 2 : 1;

        Pair firstToCut, secondToCut;
        for (int i = 0; i < specialSizePolygon.size - 1; i++) {
            // find its pair
            firstToCut = differentPolygonsPair(graph.state.edges, specialSizePolygon, i);
            if (firstToCut == null) {
                continue;
            }
            for (int j = i + 1; j < specialSizePolygon.size; j += add) {
//                System.out.println(i + " " + j);
                secondToCut = differentPolygonsPair(graph.state.edges, specialSizePolygon, j);
                if (secondToCut != null) {
                    List<Pair> toCut = new ArrayList<>();
                    toCut.add(firstToCut);
                    toCut.add(secondToCut);

                    List<Pair> toGlue = new ArrayList<>();
                    if (firstToCut.first.polygonId == specialSizePolygon.id && secondToCut.first.polygonId == specialSizePolygon.id ||
                            firstToCut.second.polygonId == specialSizePolygon.id && secondToCut.second.polygonId == specialSizePolygon.id) {
                        toGlue.add(new Pair(firstToCut.first, secondToCut.first));
                        toGlue.add(new Pair(firstToCut.second, secondToCut.second));
                    }
                    if (firstToCut.first.polygonId == specialSizePolygon.id && secondToCut.second.polygonId == specialSizePolygon.id ||
                            firstToCut.second.polygonId == specialSizePolygon.id && secondToCut.first.polygonId == specialSizePolygon.id) {
                        toGlue.add(new Pair(firstToCut.first, secondToCut.second));
                        toGlue.add(new Pair(firstToCut.second, secondToCut.first));
                    }

                    answer.add(new DCJ(toCut, toGlue));
                }
            }
        }
        return answer;
    }

    private Pair differentPolygonsPair(List<Pair> state, Polygon polygon, int index) {
        for (Pair pair : state) {
            if (pair.first.edgeId != index && pair.second.edgeId != index) {
                continue;
            }
            if (pair.first.polygonId != pair.second.polygonId) {
                if (pair.first.edgeId == index && pair.first.polygonId == polygon.id) {
                    return pair;
                }
                if (pair.second.edgeId == index && pair.second.polygonId == polygon.id) {
                    return pair;
//                    return new Pair(pair.second, pair.first);
                }
            }
        }
        return null;
    }
}
