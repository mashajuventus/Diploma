package graph;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    public int id;
    public int size;
    public Vertex mainVertex;

    public Polygon(int id, int size) {
        this.id = id;
        this.size = size;
        mainVertex = new Vertex(0, id);
        genPolygon();
    }

    public Edge nextEdge(Edge e) {
        assert e.polygonId == id;
        return new Edge(id, mainVertex.moveToInPolygon(e.edgeId).id);
    }

    private void genPolygon() {
        Vertex prevVertex = mainVertex;
        for (int v = 1; v < size; v++) {
            Vertex newVertex = new Vertex(v, id);
            newVertex.halfEdgeIn = new HalfEdge(v * 2 - 1);
            newVertex.halfEdgeIn.vertex = newVertex;
            prevVertex.nextInPolygon = newVertex;
            prevVertex = newVertex;
        }
        prevVertex.nextInPolygon = mainVertex;
        mainVertex.halfEdgeIn = new HalfEdge(size * 2 - 1);
        mainVertex.halfEdgeIn.vertex = mainVertex;
    }

    public String toString(int shift) {
        StringBuilder builder = new StringBuilder();
        StringBuilder blankShift = new StringBuilder();
        for (int s = 0; s < shift; s++) {
            blankShift.append(" ");
        }
        builder.append(blankShift)
                .append("Polygon #")
                .append(id)
                .append(", size = ")
                .append(size)
                .append(":\n");
        Vertex start = mainVertex;
        do {
            builder.append(start.toShow(shift + 4));
            start = start.nextInPolygon;
        } while (start != mainVertex);
        return builder.toString();
    }

    public Vertex moveTo(int id) {
        return this.mainVertex.moveToInPolygon(id);
    }

    public Vertex edgeTail(int id) {
        return this.moveTo(id);
    }

    public Vertex edgeHead(int id) {
        return this.edgeTail(id).nextInPolygon;
    }

    public List<Vertex> getVertices() {
        List<Vertex> answer = new ArrayList<>();
        Vertex start = mainVertex;
        do {
            answer.add(start);
            start = start.nextInPolygon;
        } while (start != mainVertex);
        return answer;
    }
}
