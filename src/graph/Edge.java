package graph;

import java.util.Comparator;
import java.util.Objects;

public class Edge {
    public int polygonId;
    public int edgeId;

    public Edge(int polygonId, int edgeId) {
        this.polygonId = polygonId;
        this.edgeId = edgeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return polygonId == edge.polygonId &&
                edgeId == edge.edgeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(polygonId, edgeId);
    }

    public String toString() {
        return "p" + this.polygonId + "e" + this.edgeId;
    }

    public static Comparator<Edge> comparator = (Edge edge1, Edge edge2) -> {
        if (edge1.polygonId < edge2.polygonId) return -1;
        if (edge1.polygonId > edge2.polygonId) return 1;

        return Integer.compare(edge1.edgeId, edge2.edgeId);
    };

}
