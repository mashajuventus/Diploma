package graph;

import solver.DCJ;
import solver.State;
import utils.Pair;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    public List<Polygon> polygons;
    public State state;

    public Graph(List<Integer> sizes) {
        polygons = new ArrayList<>();
        for (int i = 0; i < sizes.size(); i++) {
            Polygon polygon = new Polygon(i, sizes.get(i));
            polygons.add(polygon);
        }
        state = new State();
    }

    public Graph(List<Polygon> polygons, State state) {
        this.polygons = new ArrayList<>(polygons);
        this.state = new State(state.edges);
    }

    public Graph copy() {
        return new Graph(this.polygons, this.state);
    }

//    public void test(DCJ dcj) {
//        List<Pair> state = new ArrayList<>(this.state.edges);
//        System.out.println(state);
//        this.doDCJ(dcj);
//        List<Pair> state1 = new ArrayList<>(this.state.edges);
//        this.undoDCJ(dcj);
//        List<Pair> state2 = new ArrayList<>(this.state.edges);
//        System.out.println(state2);
//        System.out.println(State.equalsTo(state, state));
//        System.out.println(State.equalsTo(state, state1));
//        System.out.println(State.equalsTo(state, state2));
//    }

    public String toString(int shift) {
        StringBuilder builder = new StringBuilder();
        StringBuilder blankShift = new StringBuilder();
        for (int s = 0; s < shift; s++) {
            blankShift.append(" ");
        }
        for (Polygon polygon : polygons) {
            builder.append(blankShift).append(polygon.toString(shift));
        }
        return builder.toString();
    }

    public void glueEdges(Pair gluePair) {
        Edge e0 = gluePair.first;
        Edge e1 = gluePair.second;

        Polygon polygon0 = polygons.get(e0.polygonId);
        Polygon polygon1 = polygons.get(e1.polygonId);

        Vertex v0t = polygon0.edgeTail(e0.edgeId);
        Vertex v0h = polygon0.edgeHead(e0.edgeId);

        Vertex v1t = polygon1.edgeTail(e1.edgeId);
        Vertex v1h = polygon1.edgeHead(e1.edgeId);

        v0h.insertInClass(v1t);
        v1h.insertInClass(v0t);

//        e0.gluedTo = e1;
//        e1.gluedTo = e0;

        this.state.addToState(gluePair);
    }

    public void doDCJ(DCJ dcj) {
        List<Pair> edgesToCut = dcj.edgesToCut;
        List<Pair> edgesToGlue = dcj.edgesToGlue;

        Pair firstPairToCut = edgesToCut.get(0);
        Pair secondPairToCut = edgesToCut.get(1);

        Pair firstPairToGlue = edgesToGlue.get(0);
        Pair secondPairToGlue = edgesToGlue.get(1);

        this.state.deleteFromState(firstPairToCut);
        this.state.deleteFromState(secondPairToCut);
//        Edge e0_0 = edgesToGlue[0][0];
//        Edge e0_1 = edgesToGlue[0][1];
//
//        Edge e1_0 = edgesToGlue[1][0];
//        Edge e1_1 = edgesToGlue[1][1];

        glueEdges(firstPairToGlue);
        glueEdges(secondPairToGlue);

    }

    public void undoDCJ(DCJ dcj) {
        List<Pair> edgesToCut = dcj.edgesToCut;
        List<Pair> edgesToGlue = dcj.edgesToGlue;

        DCJ reverseDcj = new DCJ(edgesToGlue, edgesToCut);
        doDCJ(reverseDcj);
    }

    public int bestAnswer() {
        int answer = 0;
        int lastOdd = -1;
        for (Polygon polygon : polygons) {
            if (polygon.size % 2 == 0) {
                answer += polygon.size / 2 + 1;
            } else if (lastOdd > 0) {
//                answer += (lastOdd + polygon.size - 2) / 2 + 1;
                answer += (lastOdd + polygon.size) / 2;
                lastOdd = -1;
            } else {
                lastOdd = polygon.size;
            }
        }
        return answer;
    }

    public int calculate3dVertices() {
        int answer = 0;
        for (Polygon polygon : polygons) {
            List<Vertex> vertices = polygon.getVertices();
            for (Vertex vertex : vertices) {
                vertex.id3d = -1;
            }
        }
        run: while (true) {
            for (Polygon polygon : polygons) {
                List<Vertex> vertices = polygon.getVertices();
                for (Vertex vertex : vertices) {
                    if (vertex.id3d == -1) {
                        Vertex start = vertex;
                        do {
                            start.id3d = answer;
                            start = start.nextInClass;
                        } while (start != vertex);
                        answer++;
                        continue run;
                    }
                }
            }
            break;
        }
        return answer;
    }

    public List<State> genBestStates() {
        List<State> answer = new ArrayList<>();
        List<List<Edge>> oddPolygons = new ArrayList<>();
        List<List<Edge>> evenPolygons = new ArrayList<>();

        for (int i = 0; i < polygons.size(); i++) {
            List<Edge> edges = new ArrayList<>();
            for (int j = 0; j < polygons.get(i).size; j++) {
                edges.add(new Edge(i, j));
            }
            if (polygons.get(i).size % 2 == 0) {
                evenPolygons.add(edges);
            } else {
                oddPolygons.add(edges);
            }
        }


        // for every polygon we have all themselves gluings
        List<List<List<Pair>>> gluingForEvenPolygons = new ArrayList<>();
        for (List<Edge> evenPolygon : evenPolygons) {
            gluingForEvenPolygons.add(genEvenPolygons(evenPolygon));
        }

        List<List<List<Pair>>> gluingsForOddPolygonPairs = new ArrayList<>();
        List<List<Integer>> indicesOfOddPairs = genAllOddPairs(oddPolygons.size());
        for (int i = 0; i < oddPolygons.size() / 2; i++) {
            gluingsForOddPolygonPairs.add(new ArrayList<>());
        }

        for (List<Integer> indices : indicesOfOddPairs) {
            for (int fi = 0; fi < indices.size(); fi += 2) {
                int si = fi + 1;
                int pol1ind = indices.get(fi);
                int pol2ind = indices.get(si);
                gluingsForOddPolygonPairs.get(fi / 2).addAll(genOddPolygons(oddPolygons.get(pol1ind), oddPolygons.get(pol2ind)));
            }
        }

        List<List<List<Pair>>> gluingsForOddAndEven = new ArrayList<>(gluingForEvenPolygons);
        gluingsForOddAndEven.addAll(gluingsForOddPolygonPairs);

        List<Integer> polSizes = new ArrayList<>();
        for (List<List<Pair>> onePolygon : gluingsForOddAndEven) {
            polSizes.add(onePolygon.size());
        }
        List<List<Integer>> whichGluingForEveryPolygons = genAllChoiceOfGluings(polSizes);

        for (List<Integer> indices : whichGluingForEveryPolygons) {
            List<Pair> oneGluing = new ArrayList<>();
            for (int i = 0; i < indices.size(); i++) {
                int indOfPol = indices.get(i);
                oneGluing.addAll(gluingsForOddAndEven.get(i).get(indOfPol));
            }
//            System.out.println();
//            System.out.println(oneGluing);
//            System.out.println();
            answer.add(new State(oneGluing));
        }
//        System.err.println(answer.size() + " -- all best gluings");
        return answer;
    }

    public List<List<Integer>> genAllChoiceOfGluings(List<Integer> sizes) {

        if (sizes.size() == 1) {
            List<List<Integer>> last = new ArrayList<>();
            for (int i = 0; i < sizes.get(0); i++) {
                List<Integer> a = new ArrayList<>();
                a.add(i);
                last.add(a);

            }
            return last;
        }

        List<List<Integer>> answer = new ArrayList<>();
        List<List<Integer>> forFirst = genAllChoiceOfGluings(sizes.subList(0, sizes.size() - 1));
        for (int i = 0; i < sizes.get(sizes.size() - 1); i++) {
            for (List<Integer> l : forFirst){
                answer.add(new ArrayList<>(l));
            }
        }
        for (int i = 0; i < sizes.get(sizes.size() - 1); i++) {
            for (int j = 0; j < forFirst.size(); j++) {
                answer.get(i * forFirst.size() + j).add(i);
            }
        }

//        System.out.println(sizes.get(sizes.size() - 1));
//        for (int i = 0; i < sizes.get(sizes.size() - 1); i++) {
//            List<List<Integer>> copyForFirst = new ArrayList<>(forFirst);
//            System.err.println("copyForFirst before = ");
//            System.err.println(copyForFirst);
//            for (List<Integer> partIndices : copyForFirst) {
//                partIndices.add(i);
////                System.err.println(partIndices);
//            }
//            System.err.println("copyForFirst after = ");
//            System.err.println(copyForFirst);
//            answer.addAll(copyForFirst);
//        }
        return answer;
    }

    private List<List<Integer>> genAllOddPairs(int size) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        return genAllOddPairsHelp(indices);
    }

    public List<List<Integer>> genAllOddPairsHelp(List<Integer> indices) {

        List<List<Integer>> answer = new ArrayList<>();
//        for (int fi = 0; fi < indices.size() - 1; fi++) {
        int fi = 0;
        {
            for (int si = fi + 1; si < indices.size(); si++) {
                List<Integer> other = new ArrayList<>();
                for (int ind = 0; ind  < indices.size(); ind++) {
                    if (ind != si && ind != fi) {
                        other.add(indices.get(ind));
                    }
                }
//                System.err.println(other.size());
                if (other.size() == 0) {
                    answer.add(indices);
                } else {
                    for (List<Integer> otherSol : genAllOddPairsHelp(other)) {
                        List<Integer> partAnswer = new ArrayList<>();
                        partAnswer.add(indices.get(fi));
                        partAnswer.add(indices.get(si));
                        partAnswer.addAll(otherSol);
                        answer.add(partAnswer);
                    }
                }
            }
        }
        return answer;
    }

//    private List<Pair> genEvenPolygonsHelper(List<Edge> edges) {
//        if (edges.isEmpty()) {
//            return new ArrayList<>();
//        }
////        if (edges.size() == 2) {
////            List<Pair> answer = new ArrayList<>();
////            answer.add(new Pair(edges.get(0), edges.get(1)));
////            return answer;
////        }
//
//        for (int i = 1; i < edges.size(); i += 2) {
//            Pair commonGlue = new Pair(edges.get(0), edges.get(i));
//            List<List<Pair>> firstPart = genEvenPolygons(edges.subList(1, i));
//            List<List<Pair>> secondPart = genEvenPolygons(edges.subList(i + 1, edges.size()));
//
//            if (firstPart.isEmpty()) {
//                if (secondPart.isEmpty()) {
//                    return new ArrayList<>();
//                } else {
//                    return secondPart;
//                }
//            }
//        }
//
//    }

    public List<List<Pair>> genEvenPolygons(List<Edge> edges) {
        if (edges.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<Pair>> allAnswers = new ArrayList<>();
        for (int i = 1; i < edges.size(); i += 2) {
            Pair commonGlue = new Pair(edges.get(0), edges.get(i));
            List<Pair> common = new ArrayList<>();
            common.add(commonGlue);
            List<List<Pair>> firstPart = genEvenPolygons(edges.subList(1, i));
            List<List<Pair>> secondPart = genEvenPolygons(edges.subList(i + 1, edges.size()));
            allAnswers.addAll(Utils.joinTwoGlues(firstPart, secondPart, common));
        }
        return allAnswers;
    }

    public List<List<Pair>> genOddPolygons(List<Edge> edges1, List<Edge> edges2) {
        List<List<Pair>> answer = new ArrayList<>();
        for (int l = 1; l <= Math.min(edges1.size(), edges2.size()); l += 2) {
//            System.err.println("l = " + l);
            for (int st1 = 0; st1 < edges1.size(); st1++) {

                if (l == Math.min(edges1.size(), edges2.size()) && edges1.size() == edges2.size()) {
                    List<Pair> commonGlue = new ArrayList<>();
                    for (int i = 0; i < l; i++) {
                        commonGlue.add(new Pair(edges1.get((i + st1) % l), edges2.get(i)));
                    }
                    answer.add(commonGlue);
//                    System.err.println(commonGlue);
                    // fix st2
                } else {
                    List<Edge> from1 = Utils.sublist(edges1, st1, l);
                    for (int st2 = 0; st2 < edges2.size(); st2++) {
                        List<Edge> from2 = Utils.sublist(edges2, st2, l);

                        List<Pair> commonGlue = new ArrayList<>();
                        for (int i = 0; i < from1.size(); i++) {
                            commonGlue.add(new Pair(from1.get(i), from2.get(i)));
                        }

                        List<Edge> other1 = Utils.sublist(edges1, (st1 + l) % edges1.size(), (edges1.size() - l));
                        List<Edge> other2 = Utils.sublist(edges2, (st2 + l) % edges2.size(), (edges2.size() - l));

                        List<List<Pair>> newGl = Utils.joinTwoGlues(genEvenPolygons(other1), genEvenPolygons(other2), commonGlue);
//                        for (List<Pair> oneGlue : newGl) {
//                            System.out.println(oneGlue);
//                        }
                        answer.addAll(newGl);
                    }
                }
            }
        }
        return answer;

//        for (int i = 0; i < edges1.size(); i++) {
//            List<Edge> from1pol = new ArrayList<>(edges1.subList(i + 1, edges1.size()));
//            from1pol.addAll(edges1.subList(0, i));
//            for (int j = 0; j < edges2.size(); j++) {
//                List<Edge> from2pol = new ArrayList<>(edges2.subList(j + 1, edges1.size()));
//                from2pol.addAll(edges2.subList(0, j));
//                from2pol.addAll(from1pol);
//                List<List<Pair>> evenResult = genEvenPolygons(from2pol);
//                for (List<Pair> perimeterPol : evenResult) {
//                    perimeterPol.add(new Pair(edges1.get(i), edges2.get(j)));
//                }
//                answer.addAll(evenResult);
//            }
//        }
//        return answer;
    }

}
