package solver;

import graph.Edge;
import graph.Graph;
import graph.Polygon;
import utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvenPolygonsSolver {
    public Graph startGraph;
    public List<State> allBestStates;
    private AllWaysSolver allWaysSolver;

    public EvenPolygonsSolver(Graph graph) {
        startGraph = graph;
        allBestStates = startGraph.genBestStates();
        allWaysSolver = new AllWaysSolver(startGraph);
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

    public List<List<Pair>> findParityPaths() {
        List<List<Pair>> answer = new ArrayList<>();

        for (Polygon polygon : startGraph.polygons) {
            boolean inMiddle = true;
            List<Edge> thisPolygonEdges = new ArrayList<>();
            for (int i = 0; i < polygon.size; i++) {
                thisPolygonEdges.add(new Edge(polygon.id, i));
            }

            List<Pair> border = new ArrayList<>();
            List<Integer> gluedToIds = new ArrayList<>();
            for (Edge currentEdge : thisPolygonEdges) {
                Pair belongsTo = pairEdge(currentEdge);
                border.add(belongsTo);
                gluedToIds.add((belongsTo.first.equals(currentEdge) ? belongsTo.second.polygonId : belongsTo.first.polygonId));
            }

            boolean hasChange = true;
            while (hasChange) {
                hasChange = false;

                // shifting
                for (int i = 0; i < gluedToIds.size(); i++) {
                    if (gluedToIds.get(0).equals(gluedToIds.get(gluedToIds.size() - 1))) {

                        border.add(border.get(0));
                        border.remove(0);

                        gluedToIds.add(gluedToIds.get(0));
                        gluedToIds.remove(0);
                    } else {
                        break;
                    }
                }

                // one big path
                if (gluedToIds.get(0).equals(gluedToIds.get(gluedToIds.size() - 1))) {
                    if (gluedToIds.get(0) > polygon.id) {
                        answer.add(new ArrayList<>(border));
                    }
                    break;
                }

                int start = 0;
                for (int i = 1; i < gluedToIds.size() + 1; i++) {
                    int right = (i == gluedToIds.size()) ? 0 : i;
                    int startId = gluedToIds.get(start);
                    int rightId = gluedToIds.get(right);
                    if (rightId != startId) {
                        if (i - start > 1) {
                            int left = (start - 1 + gluedToIds.size()) % gluedToIds.size();
                            int leftId = gluedToIds.get(left);

                            if (!inMiddle || leftId == rightId && (i - start) % 2 == 0) {
                                if (startId > polygon.id) {
                                    List<Pair> newPath = border.subList(start, i);
                                    answer.add(newPath);
                                }

                                border = removeSublist(border, start, i);
                                gluedToIds = removeSublist(gluedToIds, start, i);

                                hasChange = true;
                                break;
                            }
                        }
                        start = i;
                    }
                }
                if (!hasChange && inMiddle) {
                    inMiddle = false;
                    hasChange = true;
                }

//                start = 0;
//                for (int i = 1; i < gluedToIds.size() + 1; i++) {
//                    int right = (i == gluedToIds.size()) ? 0 : i;
//                    if (!gluedToIds.get(right).equals(gluedToIds.get(start))) {
//                        if (right - start > 1) {
//                            int left = (start - 1 + gluedToIds.size()) % gluedToIds.size();
//
//                            int fin = ((right - start) % 2 == 0) ? right : right - 1;
//
//                            List<Pair> newPath = border.subList(start, fin);
//                            answer.add(newPath);
//
//                            border = removeSublist(border, start, fin);
//                            gluedToIds = removeSublist(gluedToIds, start, fin);
//
//                            System.out.println("  - " + gluedToIds);
//                        }
//
//                        start = right;
//                    }
//                }


//                for (int i = 1; i < gluedToIds.size(); i++) {
//                    if (!gluedToIds.get(i).equals(gluedToIds.get(start))) {
//                        System.out.println("           in then i = " + i);
//                        if (i - start > 1) {
//                            // already a path
//                            int before = (start - 1 + gluedToIds.size()) % gluedToIds.size();
//                            boolean isLocationNess;
//                            if (parity == 0) {
//                                isLocationNess = gluedToIds.get(before).equals(gluedToIds.get(i)) && (i - start) % 2 == parity;
//                            } else {
//                                isLocationNess = true;
//                            }
//                            //  && (i - start) % 2 == parity
//                            if (isLocationNess) {
//                                hasChange = true;
//                                int fin = ((i - start) % 2 == 0) ? i : i - 1;
//                                System.out.println("  before = " + before + ", fin = " + fin);
//                                System.out.println("  - " + gluedToIds);
//                                List<Pair> newPath = border.subList(start, fin);
////                                System.out.println(newPath);
//                                answer.add(newPath);
//
//                                border = removeSublist(border, start, fin);
//                                gluedToIds = removeSublist(gluedToIds, start, fin);
//                                System.out.println("  - " + gluedToIds);
//                                break;
//                            }
//                        }
//                        System.out.println("           move start");
//                        start = i;
//                    } else {
//                        System.out.println("           in else i = " + i);
//                        if (i == gluedToIds.size() - 1) {
//                            System.out.println("                 in elseif");
//                            hasChange = true;
//                            int fin = ((i + 1 - start) % 2 == 0) ? i + 1: i;
//                            List<Pair> newPath = border.subList(start, fin);
////                                System.out.println(newPath);
//                            answer.add(newPath);
//
//                            border = removeSublist(border, start, fin);
//                            gluedToIds = removeSublist(gluedToIds, start, fin);
//                            System.out.println("  " + gluedToIds);
//                            break;
//                        }
//                    }
//                }
//                if (parity == 1 && !hasChange) {
//                    System.out.println("    no changes");
//                }
//                if (parity == 0 && !hasChange) {
//                    parity = 1;
//                    hasChange = true;
//                    System.out.println("    swap parity");
//                }
            }
        }

        System.out.println(answer);
        return answer;
    }

    private <T> List<T> removeSublist(List<T> list, int start, int end) {
        if (start == end) {
            return new ArrayList<>();
        }
        List<T> answer = new ArrayList<>();
        answer.addAll(list.subList(0, start));
        answer.addAll(list.subList(end, list.size()));
        return answer;
    }

}
