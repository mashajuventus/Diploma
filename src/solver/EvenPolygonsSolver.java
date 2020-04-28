package solver;

import graph.Edge;
import graph.Graph;
import graph.Polygon;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class EvenPolygonsSolver {
    public Graph startGraph;
    public List<State> allBestStates;
    private AllWaysSolver allWaysSolver;

    public EvenPolygonsSolver(Graph graph) {
        startGraph = graph;
        allBestStates = startGraph.genBestStates();
        allWaysSolver = new AllWaysSolver(startGraph);
//        System.out.println(startGraph.state.edges);
    }

    public List<List<Pair>> findPath(int parity) {
        List<List<Pair>> answer = new ArrayList<>();
        for (Polygon polygon : startGraph.polygons) {
            List<Edge> thisPolygonEdge = new ArrayList<>();
            for (int i = 0; i < polygon.size; i++) {
                thisPolygonEdge.add(new Edge(polygon.id, i));
            }
//            thisPolygonEdge.addAll(thisPolygonEdge);

            List<Integer> gluedToPolygonIds = new ArrayList<>();
            for (int i = 0; i < thisPolygonEdge.size(); i++) {
                Edge currentEdge = thisPolygonEdge.get(i);
                Pair belongsTo = pairEdge(currentEdge);
                gluedToPolygonIds.add((belongsTo.first.equals(currentEdge) ? belongsTo.second.polygonId : belongsTo.first.polygonId));
            }
            for (int i = 0; 0 < gluedToPolygonIds.size(); i++) {
                if (gluedToPolygonIds.get(i).equals(gluedToPolygonIds.get(gluedToPolygonIds.size() - 1))) {
                    thisPolygonEdge.add(thisPolygonEdge.get(0));
                    thisPolygonEdge.remove(0);
                } else {
                    break;
                }
            }

            int currentPathMainPolygonId = thisPolygonEdge.get(0).polygonId;
            List<Pair> currentPath = new ArrayList<>();
//            currentPath.add(pairEdge(thisPolygonEdge.get(0)));
            for (int i = 0; i < thisPolygonEdge.size(); i++) {
                Edge currentEdge = thisPolygonEdge.get(i);
                Pair belongsTo = pairEdge(currentEdge);
//                System.out.println("edge " + currentEdge + ", pair ");

                int gluedToPolygonId = (belongsTo.first.equals(currentEdge) ? belongsTo.second.polygonId : belongsTo.first.polygonId);

                if (currentPathMainPolygonId != gluedToPolygonId) {
//                    System.out.println("stop path");
                    if (!currentPath.isEmpty() && currentPath.size() % 2 == parity) {
                        if (currentPathMainPolygonId > polygon.id) {
//                            System.out.println(currentPathMainPolygonId + " " + polygon.id);
                            answer.add(currentPath);
//                        System.out.println("added " + currentPath);
                        }
                        if (currentPathMainPolygonId == polygon.id) {
                            boolean hasFound = false;
                            for (List<Pair> path : answer) {
                                if (listEquals(currentPath, path)) {
                                    hasFound = true;
                                }
                            }
                            if (!hasFound) {
                                answer.add(currentPath);
                            }
                        }
                    }
                    currentPathMainPolygonId = gluedToPolygonId;
                    currentPath = new ArrayList<>();
                }
                currentPath.add(belongsTo);
//                System.out.println("current path = " + currentPath);
            }
            if (!currentPath.isEmpty() && currentPath.size() % 2 == parity) {
                if (currentPathMainPolygonId > polygon.id) {
//                    System.out.println(currentPathMainPolygonId + " " + polygon.id);
                    answer.add(currentPath);
//                        System.out.println("added " + currentPath);
                }
                if (currentPathMainPolygonId == polygon.id) {
                    boolean hasFound = false;
                    for (List<Pair> path : answer) {
                        if (listEquals(currentPath, path)) {
                            hasFound = true;
                        }
                    }
                    if (!hasFound) {
                        answer.add(currentPath);
                    }
                }
            }
//            System.out.println(answer);
//            System.out.println("stop polygon");
        }
        return answer;
    }

    private Pair pairEdge(Edge e) {
//        System.out.println(e);
        for (Pair pair : startGraph.state.edges) {
//            System.out.println(pair);
            if (pair.first.equals(e) || pair.second.equals(e)) {
//                System.out.println("-------------");
                return pair;
            }
        }
//        System.out.println("-------------");
        return null;
    }

    private <T> boolean listEquals(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) {
                return false;
            }
        }
        return true;
    }


}
