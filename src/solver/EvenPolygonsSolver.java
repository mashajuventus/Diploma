package solver;

import graph.Edge;
import graph.Graph;
import graph.Polygon;
import graph.Vertex;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;

import static utils.Utils.*;

public class EvenPolygonsSolver {
    public Graph startGraph;

    public EvenPolygonsSolver(Graph graph) {
        startGraph = graph;
    }

    private Pair pairEdge(Edge e) {
        for (Pair pair : startGraph.state.edges) {
            if (pair.first.equals(e) || pair.second.equals(e)) {
                return pair;
            }
        }
        return null;
    }

    public List<DCJ> solve() {
        List<DCJ> answer = new ArrayList<>();

        List<List<Pair>> paths = findParityPaths();
        for (List<Pair> path : paths) {
            for (int i = 0; i < path.size() - 1; i += 2) {
                List<Pair> toCut = new ArrayList<>();
                toCut.add(path.get(i));
                toCut.add(path.get(i + 1));

                List<Pair> toGlue = new ArrayList<>();
                toGlue.add(new Pair(path.get(i).first, path.get(i + 1).first));
                toGlue.add(new Pair(path.get(i).second, path.get(i + 1).second));

                DCJ dcj = new DCJ(toCut, toGlue);
                answer.add(dcj);

                startGraph.doDCJ(dcj);
            }
        }

        // DCJs is going in afterPaths()
        List<DCJ> afterPaths = afterPaths();
        answer.addAll(afterPaths);

        return answer;
    }

    public void checkIsBest() {
        if (startGraph.bestAnswer() != startGraph.calculate3dVertices()) {
            throw new RuntimeException("state is not the best yet");
        }
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
            }
        }

        return answer;
    }

    public GraphType graphType() {
        List<List<Vertex>> vertexClasses = startGraph.vertexClasses();

        boolean isEnd = true;
        run: for (List<Vertex> oneClass : vertexClasses) {
            int classPolygon = oneClass.get(0).polygonId;
            for (Vertex v : oneClass) {
                if (v.polygonId != classPolygon) {
                    isEnd = false;
                    break run;
                }
            }
        }
        if (isEnd) return GraphType.END;

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < vertexClasses.size(); i++) {
            List<Vertex> oneClass = vertexClasses.get(i);
            int classPolygon = oneClass.get(0).polygonId;
            for (Vertex v : oneClass) {
                if (v.polygonId != classPolygon) {
                    indices.add(i);
                    break;
                }
            }
        }

        if (indices.size() != 2) {
            return GraphType.OTHER;
        }

        if (vertexClasses.get(indices.get(0)).size() >
                vertexClasses.get(indices.get(1)).size()) {
            indices.add(indices.get(0));
            indices.remove(0);
        }
//        if (vertexClasses.get(indices.get(0)).size() * 2 !=
//                vertexClasses.get(indices.get(1)).size()) {
//            return GraphType.OTHER;
//        }

        int lastId = -1;
        List<Integer> polIdsSmall = new ArrayList<>();
        for (Vertex v : vertexClasses.get(indices.get(0))) {
            if (v.polygonId != lastId) {
                lastId = v.polygonId;
                polIdsSmall.add(v.polygonId);
            }
        }
        List<Integer> reversedSmall = new ArrayList<>();
        for (int id : polIdsSmall) {
            reversedSmall.add(0, id);
        }
        lastId = -1;
        List<Integer> polIdsBig = new ArrayList<>();
        for (Vertex v : vertexClasses.get(indices.get(1))) {
            if (v.polygonId != lastId) {
                lastId = v.polygonId;
                polIdsBig.add(v.polygonId);
            }
        }
        if (polIdsBig.get(0).equals(polIdsBig.get(polIdsBig.size() - 1))) {
            polIdsBig.remove(0);
        }
//        System.out.println("polIdsSmall = " + polIdsSmall);
//        System.out.println("reversedSmall = " + reversedSmall);
//        System.out.println("polIdsBig = " + polIdsBig);
        for (int i = 0; i < polIdsBig.size(); i++) {
            polIdsBig = sublist(polIdsBig, 1, polIdsBig.size());
            if (listEquals(polIdsSmall, polIdsBig) ||
                    listEquals(reversedSmall, polIdsBig)) return GraphType.ORANGE;
        }

        return GraphType.OTHER;
    }

    public enum GraphType {
        END, ORANGE, OTHER;
    }

    public void tryAfterPath() {
        List<List<Vertex>> vertexClasses = startGraph.vertexClasses();

        List<Pair> statePart = new ArrayList<>();
        for (Pair pair : startGraph.state.edges) {
            if (pair.first.polygonId != pair.second.polygonId) {
                statePart.add(pair);
            } else {
                break;
            }
        }

        List<List<Edge>> toCut = new ArrayList<>();
        for (List<Vertex> oneClass : vertexClasses) {
            List<Edge> oneClassCut = new ArrayList<>();

            for (int i = 0; i < oneClass.size(); i++) {
                Vertex cur = oneClass.get(i);
                int indNext = (i + 1) % oneClass.size();
                Vertex next = oneClass.get(indNext);
                if (cur.polygonId != next.polygonId) {
                    oneClassCut.add(inEdge(cur));
                    oneClassCut.add(outEdge(next));
                }
            }
            toCut.add(oneClassCut);
            System.out.println("one class cut " + oneClassCut);
        }

        List<Pair> toCutD = new ArrayList<>();
        toCutD.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(1)));
        toCutD.add(new Pair(toCut.get(0).get(2), toCut.get(0).get(3)));
        List<Pair> toGlue = new ArrayList<>();
        toGlue.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(3)));
        toGlue.add(new Pair(toCut.get(0).get(1), toCut.get(0).get(2)));
//        System.out.println("cut " + toCutD);
//        System.out.println("glue " + toGlue);
        DCJ dcj = new DCJ(toCutD, toGlue);
        startGraph.doDCJ(dcj);

        toCutD = new ArrayList<>();
        toCutD.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(3)));
        toCutD.add(new Pair(toCut.get(0).get(4), toCut.get(0).get(5)));
        toGlue = new ArrayList<>();
        toGlue.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(5)));
        toGlue.add(new Pair(toCut.get(0).get(3), toCut.get(0).get(4)));
//        System.out.println("cut " + toCutD);
//        System.out.println("glue " + toGlue);
        dcj = new DCJ(toCutD, toGlue);
        startGraph.doDCJ(dcj);

        toCutD = new ArrayList<>();
        toCutD.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(5)));
        toCutD.add(new Pair(toCut.get(0).get(6), toCut.get(0).get(7)));
        toGlue = new ArrayList<>();
        toGlue.add(new Pair(toCut.get(0).get(0), toCut.get(0).get(7)));
        toGlue.add(new Pair(toCut.get(0).get(5), toCut.get(0).get(6)));
//        System.out.println("cut " + toCutD);
//        System.out.println("glue " + toGlue);
        dcj = new DCJ(toCutD, toGlue);
        startGraph.doDCJ(dcj);
        System.out.println("after " + startGraph.vertexClasses());
    }

    public Edge inEdge(Vertex v) {
        if (v.id != 0) {
            return new Edge(v.polygonId, v.id - 1);
        } else {
            return new Edge(v.polygonId, startGraph.polygons.get(v.polygonId).size - 1);
        }
    }

    public Edge outEdge(Vertex v) {
        return new Edge(v.polygonId, v.id);
    }

    public List<DCJ> afterPaths() {
        int ops = 0;
        List<DCJ> answer = new ArrayList<>();
        run: while (true) {
            List<List<Vertex>> vertexClasses = startGraph.vertexClasses();

            for (List<Vertex> oneClass : vertexClasses) {
                int mainPolygon = oneClass.get(0).polygonId;
                boolean isOnePolygon = true;
                for (Vertex polygonVertex : oneClass) {
                    if (polygonVertex.polygonId != mainPolygon) {
                        isOnePolygon = false;
                        break;
                    }
                }
                if (!isOnePolygon) {
                    List<Edge> order = new ArrayList<>();
                    for (int i = 0; i < oneClass.size(); i++) {
                        Vertex cur = oneClass.get(i);
                        int indNext = (i + 1) % oneClass.size();
                        Vertex next = oneClass.get(indNext);
                        if (cur.polygonId != next.polygonId) {
                            order.add(inEdge(cur));
                            order.add(outEdge(next));
                        }
                    }

                    for (int i = 1; i < order.size() - 1; i += 2) {
                        List<Pair> toCut = new ArrayList<>();
                        toCut.add(new Pair(order.get(0), order.get(i)));
                        toCut.add(new Pair(order.get(i + 1), order.get(i + 2)));

                        List<Pair> toGlue = new ArrayList<>();
                        toGlue.add(new Pair(order.get(0), order.get(i + 2)));
                        toGlue.add(new Pair(order.get(i), order.get(i + 1)));

                        DCJ dcj = new DCJ(toCut, toGlue);
                        startGraph.doDCJ(dcj);
                        answer.add(dcj);
                        ops++;
                        continue run;
                    }
                }
            }
            break;
        }
        return answer;
    }

}
