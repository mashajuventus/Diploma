package solver;

import graph.Edge;
import graph.Graph;
import graph.Polygon;
import graph.Vertex;
import utils.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<List<Pair>> borders = new ArrayList<>();
        List<List<Integer>> gluedToIds = new ArrayList<>();

        for (Polygon polygon : startGraph.polygons) {
            List<Edge> thisPolygonEdges = new ArrayList<>();
            for (int i = 0; i < polygon.size; i++) {
                thisPolygonEdges.add(new Edge(polygon.id, i));
            }

            List<Pair> border = new ArrayList<>();
            List<Integer> gluedToIdsOne = new ArrayList<>();
            for (Edge currentEdge : thisPolygonEdges) {
                Pair belongsTo = pairEdge(currentEdge);
                border.add(belongsTo);
                gluedToIdsOne.add((belongsTo.first.equals(currentEdge) ? belongsTo.second.polygonId : belongsTo.first.polygonId));
            }

            // shifting
            for (int i = 0; i < gluedToIdsOne.size(); i++) {
                if (gluedToIdsOne.get(0).equals(gluedToIdsOne.get(gluedToIdsOne.size() - 1))) {

                    border.add(border.get(0));
                    border.remove(0);

                    gluedToIdsOne.add(gluedToIdsOne.get(0));
                    gluedToIdsOne.remove(0);
                } else {
                    break;
                }
            }

            borders.add(border);
            gluedToIds.add(gluedToIdsOne);
        }
//        System.out.println(borders);
//        System.out.println(gluedToIds);

        for (int id = 0; id < borders.size(); id++) {
            List<Pair> border = borders.get(id);
            List<Integer> gluedToIdsOne = gluedToIds.get(id);

//            System.out.println(id);
//            System.out.println(border);
//            System.out.println(gluedToIdsOne);

            boolean hasChange = true;
            while (hasChange) {
                hasChange = false;
                int start = 0;
                for (int i = 1; i < gluedToIdsOne.size() + 1; i++) {
                    int right = (i == gluedToIdsOne.size()) ? 0 : i;
                    int startId = gluedToIdsOne.get(start);
                    int rightId = gluedToIdsOne.get(right);
                    if (rightId != startId) {
//                        System.out.println("start = " + start);
//                        System.out.println("right = " + right);
                        if (i - start > 1) {
//                            System.out.println("  i - start = " + (i - start));
                            int left = (start - 1 + gluedToIdsOne.size()) % gluedToIdsOne.size();
                            int leftId = gluedToIdsOne.get(left);

//                            if (leftId == rightId && (i - start) % 2 == 0) {
                            if ((i - start) % 2 == 0) {
//                                System.out.println("  this is a path even length");
                                if (startId > id) {
                                    List<Pair> newPath = border.subList(start, i);
//                                    System.out.println("added " + newPath);
                                    answer.add(newPath);
                                }

                                border = removeSublist(border, start, i);
                                gluedToIdsOne = removeSublist(gluedToIdsOne, start, i);

                                hasChange = true;
                                break;
                            }
                        }
                        start = i;
                    }
                }
                if (gluedToIdsOne.size() > 0) {
                    if (gluedToIdsOne.get(0).equals(gluedToIdsOne.get(gluedToIdsOne.size() - 1))) {
//                    System.out.println("id = " + id);
                        if (gluedToIdsOne.get(0) > id) {
                            List<Pair> found = findShiftedPath(border, borders.get(gluedToIdsOne.get(0)), 0, gluedToIdsOne.size(), id);
                            if (found.size() > 0) {
//                            System.out.println("can added");
                                border = removeSublist(border, 0, gluedToIdsOne.size());
                                gluedToIdsOne = removeSublist(gluedToIdsOne, 0, gluedToIdsOne.size());
                                answer.add(found);
                            } else {
//                            System.out.println("cannot be here i suppose");
                            }
                        }
                    }
                }
            }
            borders.set(id, new ArrayList<>(border));
            gluedToIds.set(id, new ArrayList<>(gluedToIdsOne));

//            boolean hasChange = true;
//            while (hasChange) {
//                hasChange = false;
//
//                // shifting
//                for (int i = 0; i < gluedToIds.size(); i++) {
//                    if (gluedToIds.get(0).equals(gluedToIds.get(gluedToIds.size() - 1))) {
//
//                        border.add(border.get(0));
//                        border.remove(0);
//
//                        gluedToIds.add(gluedToIds.get(0));
//                        gluedToIds.remove(0);
//                    } else {
//                        break;
//                    }
//                }
//
//                System.out.println(polygon.id + " id, border = " + border);
//
//                if (!inMiddle) {
//                    for (List<Pair> shiftedAnswer : maybeAnswer) {
//                        System.out.println("try " + shiftedAnswer);
//                        System.out.println("we are " + polygon.id);
//                        List<Pair> shP = new ArrayList<>(shiftedAnswer);
//                        List<Pair> revShP = new ArrayList<>();
//                        for (Pair p : shP) {
//                            revShP.add(0, p);
//                        }
//
//                        int pId = shiftedAnswer.get(0).first.polygonId;
//                        System.out.println("glued to " + pId);
//                        for (int i = 0; i <= border.size() - shiftedAnswer.size(); i++) {
//                            System.out.println("from border = " + border.get(i).first.polygonId);
//                            if (border.get(i).first.polygonId == pId) {
//                                System.out.println("it might be " + border.subList(i, i + shiftedAnswer.size()));
//                                if (listEquals(border.subList(i, i + shiftedAnswer.size()), shP)) {
//                                    answer.add(shP);
//                                    maybeAnswer.remove(shiftedAnswer);
//                                    System.out.println("added " + shP);
//                                    System.out.println("removed " + shiftedAnswer);
//                                    break;
//                                } if (listEquals(border.subList(i, i + shiftedAnswer.size()), revShP)) {
//                                    answer.add(revShP);
//                                    maybeAnswer.remove(shiftedAnswer);
//                                    System.out.println("added " + revShP);
//                                    System.out.println("removed " + shiftedAnswer);
//                                    break;
//                                } else {
//                                    shP.add(shP.get(0));
//                                    shP.remove(0);
//                                    revShP.add(revShP.get(0));
//                                    revShP.remove(0);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // one big path
//                if (gluedToIds.get(0).equals(gluedToIds.get(gluedToIds.size() - 1))) {
//                    if (gluedToIds.get(0) > polygon.id) {
//                        // maybe shift is needed
//                        System.out.println("maybe " + border);
//                        maybeAnswer.add(new ArrayList<>(border));
//                    }
//                    break;
//                }
//
//                int start = 0;
//                for (int i = 1; i < gluedToIds.size() + 1; i++) {
//                    int right = (i == gluedToIds.size()) ? 0 : i;
//                    int startId = gluedToIds.get(start);
//                    int rightId = gluedToIds.get(right);
//                    if (rightId != startId) {
//                        if (i - start > 1) {
//                            int left = (start - 1 + gluedToIds.size()) % gluedToIds.size();
//                            int leftId = gluedToIds.get(left);
//
//                            if (!inMiddle || leftId == rightId && (i - start) % 2 == 0) {
//                                if (startId > polygon.id) {
//                                    List<Pair> newPath = border.subList(start, i);
//                                    answer.add(newPath);
//                                }
//
//                                border = removeSublist(border, start, i);
//                                gluedToIds = removeSublist(gluedToIds, start, i);
//
//                                hasChange = true;
//                                break;
//                            }
//                        }
//                        start = i;
//                    }
//                }
////                if (!hasChange && inMiddle) {
////                    inMiddle = false;
////                    hasChange = true;
////                }
//            }
        }

//        System.out.println("gluedToIds after " + gluedToIds);

        for (int id = 0; id < borders.size(); id++) {
            List<Pair> border = borders.get(id);
            List<Integer> gluedToIdsOne = gluedToIds.get(id);

            // shifting
            for (int i = 0; i < gluedToIdsOne.size(); i++) {
                if (gluedToIdsOne.get(0).equals(gluedToIdsOne.get(gluedToIdsOne.size() - 1))) {

                    border.add(border.get(0));
                    border.remove(0);

                    gluedToIdsOne.add(gluedToIdsOne.get(0));
                    gluedToIdsOne.remove(0);
                } else {
                    break;
                }
            }

            borders.set(id, new ArrayList<>(border));
            gluedToIds.set(id, new ArrayList<>(gluedToIdsOne));
        }

        for (int id = 0; id < borders.size(); id++) {
            List<Pair> border = borders.get(id);
            List<Integer> gluedToIdsOne = gluedToIds.get(id);

            int st = 0;
            while (st < gluedToIdsOne.size()) {
                int end = st + 1;
                while (end < gluedToIdsOne.size() &&
                        gluedToIdsOne.get(end - 1).equals(gluedToIdsOne.get(st))) {
                    end++;
                }
                if (end - st >= 3) {
                    // this is path, maybe it already has added
                    if (id < gluedToIdsOne.get(st)) {
                        List<Pair> found = findShiftedPath(border, borders.get(gluedToIdsOne.get(st)), st, end, id);
                        if (found.size() > 0) {
                            answer.add(found);
                        }

//                        System.out.println(id + " " + st + " " + end);
//                        System.out.println("here");
//                        List<Pair> shiftedPath = border.subList(st, end);
//                        List<Pair> reversedPath = new ArrayList<>();
//                        for (Pair p : shiftedPath) {
//                            reversedPath.add(0, p);
//                        }
//
//                        List<Pair> gluedToBorder = borders.get(gluedToIdsOne.get(st));
////                        System.out.println("gluedToBorder " + gluedToBorder);
//                        run: for (int i = 0; i <= gluedToBorder.size() - end + st; i++) {
//                            if (gluedToBorder.get(i).first.polygonId == id) {
////                                System.out.println("it might be " + gluedToBorder.subList(i, i + end - st));
////                                System.out.println("shifted " + shiftedPath);
////                                System.out.println("reversed " + reversedPath);
//                                List<Pair> cand = gluedToBorder.subList(i, i + end - st);
//                                for (int j = 0; j < shiftedPath.size(); j++) {
//                                    if (listEquals(cand, shiftedPath)) {
//                                        answer.add(shiftedPath);
////                                    maybeAnswer.remove(shiftedAnswer);
////                                    System.out.println("added " + shP);
////                                    System.out.println("removed " + shiftedAnswer);
//                                        break run;
//                                    }
//                                    if (listEquals(cand, reversedPath)) {
//                                        answer.add(reversedPath);
////                                    maybeAnswer.remove(shiftedAnswer);
////                                    System.out.println("added " + revShP);
////                                    System.out.println("removed " + shiftedAnswer);
//                                        break run;
//                                    } else {
//                                        shiftedPath.add(shiftedPath.get(0));
//                                        shiftedPath.remove(0);
//                                        reversedPath.add(reversedPath.get(0));
//                                        reversedPath.remove(0);
//                                    }
//                                }
//                            }
//                        }


                    }
                }
                st = end;
            }
        }
        return answer;
    }

    private List<Pair> findShiftedPath(List<Pair> border, List<Pair> gluedToBorder, int st, int end, int currentId) {
        List<Pair> shiftedPath = border.subList(st, end);
//        System.out.println("we are " + currentId);
//        System.out.println("border = " + border);
        List<Pair> reversedPath = new ArrayList<>();
        for (Pair p : shiftedPath) {
            reversedPath.add(0, p);
        }
//                        System.out.println("gluedToBorder " + gluedToBorder);
        for (int i = 0; i <= gluedToBorder.size() - end + st; i++) {
            if (gluedToBorder.get(i).first.polygonId == currentId) {
//                                System.out.println("it might be " + gluedToBorder.subList(i, i + end - st));
//                                System.out.println("shifted " + shiftedPath);
//                                System.out.println("reversed " + reversedPath);
                List<Pair> cand = gluedToBorder.subList(i, i + end - st);
                for (int j = 0; j < shiftedPath.size(); j++) {
                    if (listEquals(cand, shiftedPath)) {
                        return shiftedPath;
//                        answer.add(shiftedPath);
//                                    maybeAnswer.remove(shiftedAnswer);
//                                    System.out.println("added " + shP);
//                                    System.out.println("removed " + shiftedAnswer);
//                        break run;
                    }
                    if (listEquals(cand, reversedPath)) {
                        return reversedPath;
//                        answer.add(reversedPath);
//                                    maybeAnswer.remove(shiftedAnswer);
//                                    System.out.println("added " + revShP);
//                                    System.out.println("removed " + shiftedAnswer);
//                        break run;
                    } else {
                        shiftedPath.add(shiftedPath.get(0));
                        shiftedPath.remove(0);
                        reversedPath.add(reversedPath.get(0));
                        reversedPath.remove(0);
                    }
                }
            }
        }
        return new ArrayList<>();
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
            vertexClasses = vertexClasses.stream().sorted(Comparator.comparingInt(List::size)).collect(Collectors.toList());
//            System.out.println(vertexClasses);

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
//                        System.out.println(dcj);
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
