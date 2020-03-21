package solver;

import graph.Edge;
import utils.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class State {
    public List<Pair> edges;

    public State() {
        edges = new ArrayList<>();
    }

    public State(List<Pair> edges) {
        this.edges = new ArrayList<>(edges);
    }

    public void setState(List<Pair> edges) {
        this.edges = new ArrayList<>(edges);
    }

    public void addToState(Pair newGlue) {
        this.edges.add(newGlue);
    }

    public void deleteFromState(Pair cutGlue) {
        for (Pair pair : this.edges) {
            if (pair.equals(cutGlue)) {
//                System.err.println("can delete");
                this.edges.remove(pair);
                break;
            }
        }
//        this.edges.remove(cutGlue);
    }

    public Map<Edge, Edge> stateToMap() {
        Map<Edge, Edge> stateEdges = new HashMap<>();
        for (Pair edgePair : this.edges) {
            if (stateEdges.put(edgePair.first, edgePair.second) != null) throw new AssertionError();
            if (stateEdges.put(edgePair.second, edgePair.first) != null) throw new AssertionError();
        }
        return stateEdges;
    }

    public int distanceTo(State state, boolean forDebug) {
        Map<Edge, Edge> thisEdges = this.stateToMap();
        Map<Edge, Edge> otherEdges = state.stateToMap();

        if (forDebug) {
            System.out.println("this edges");
            System.out.println(thisEdges);
            System.out.println("other edges");
            System.out.println(otherEdges);
        }

        Set<Map.Entry<Edge, Edge>> diff = thisEdges.entrySet();
        diff.removeAll(otherEdges.entrySet());
        return diff.size() / 4;
    }

//    public static boolean equalsTo(List<Pair> edges1, List<Pair> edges2) {
//        Comparator<Pair> comparator = (pair1, pair2) -> {
//            if (pair1.equals(pair2)) return 0;
//            if (Edge.comparator.compare(pair1.first, pair2.first) < 0) return -1;
//            if (Edge.comparator.compare(pair1.first, pair2.first) > 0) return 1;
//
//            return Integer.compare(Edge.comparator.compare(pair1.second, pair2.second), 0);
//        };
//        edges1.sort(comparator);
//        edges2.sort(comparator);
////        System.out.println("compare 2 states");
////        System.out.println(edges1);
////        System.out.println(edges2);
////        System.out.println("comoare 2 states");
//        for (int i = 0; i < edges1.size(); i++) {
//            if (!edges1.get(i).equals(edges2.get(i))) {
//                return false;
//            }
//        }
//        return true;
//    }

    public boolean equalsTo(State state) {
        Comparator<Pair> comparator = (pair1, pair2) -> {
            if (pair1.equals(pair2)) return 0;
            if (Edge.comparator.compare(pair1.first, pair2.first) < 0) return -1;
            if (Edge.comparator.compare(pair1.first, pair2.first) > 0) return 1;

            return Integer.compare(Edge.comparator.compare(pair1.second, pair2.second), 0);
        };

        List<Pair> edges1 = this.edges;
        List<Pair> edges2 = state.edges;

        edges1.sort(comparator);
        edges2.sort(comparator);
//        System.out.println("compare 2 states");
//        System.out.println(edges1);
//        System.out.println(edges2);
//        System.out.println("comoare 2 states");
        for (int i = 0; i < edges1.size(); i++) {
            if (!edges1.get(i).equals(edges2.get(i))) {
                return false;
            }
        }
        return true;
    }

    public List<State> genAllChildrenStates() {
        List<State> answer = new ArrayList<>();
        for (int i = 0; i < edges.size() - 1; i++) {
            Pair edgeI = edges.get(i);
            for (int j = i + 1; j < edges.size(); j++) {
                Pair edgeJ = edges.get(j);

                List<Pair> toShuffle = new ArrayList<>();
                toShuffle.add(edgeI);
                toShuffle.add(edgeJ);
                List<List<Pair>> newOrder = Pair.shuffle(toShuffle);

                if (newOrder.size() != 2) {
                    throw new RuntimeException("after shuffle must be only 2 ways of change state");
                }
                for (List<Pair> pairs : newOrder) {
                    State newState = new State(edges);
                    newState.deleteFromState(edgeI);
                    newState.deleteFromState(edgeJ);
                    if (pairs.size() != 2) {
                        throw new RuntimeException("after shuffle must be only 2 pairs of edges");
                    }
                    for (Pair newGlues : pairs) {
                        newState.addToState(newGlues);
                    }
                    answer.add(newState);
                }
            }
        }
        return answer;
    }

    public List<DCJ> genAllChildrenDcj() {

        List<DCJ> answer = new ArrayList<>();

        for (int i = 0; i < edges.size() - 1; i++) {

            Pair pairI = edges.get(i);

            for (int j = i + 1; j < edges.size(); j++) {

                Pair pairJ = edges.get(j);

                List<Pair> edgesToCut = new ArrayList<>();
                edgesToCut.add(pairI);
                edgesToCut.add(pairJ);

                List<List<Pair>> newOrder = Pair.shuffle(edgesToCut);

                if (newOrder.size() != 2) {
                    throw new RuntimeException("after shuffle must be only 2 ways of change state");
                }
                for (List<Pair> edgesToGlue : newOrder) {

                    if (edgesToGlue.size() != 2) {
                        throw new RuntimeException("after shuffle must be only 2 pairs of edges");
                    }

                    answer.add(new DCJ(edgesToCut, edgesToGlue));
                }
            }
        }

        return answer;
    }
}
