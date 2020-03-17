package graph;

import java.util.Comparator;

public class Edge {
    public int polygonId;
    public int edgeId;
//    public Edge gluedTo;

    public Edge(int polygonId, int edgeId) {
        this.polygonId = polygonId;
        this.edgeId = edgeId;
//        this.gluedTo = null;
    }

    public boolean equals(Edge edge) {
        return this.polygonId == edge.polygonId &&
                this.edgeId == edge.edgeId;
    }

    public String toString() {
        return this.polygonId + " " + this.edgeId;
    }

    public static Comparator<Edge> comparator = (Edge edge1, Edge edge2) -> {
        if (edge1.polygonId < edge2.polygonId) return -1;
        if (edge1.polygonId > edge2.polygonId) return 1;

        return Integer.compare(edge1.edgeId, edge2.edgeId);
    };
}
