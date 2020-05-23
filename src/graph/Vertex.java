package graph;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    public int id;
    public HalfEdge halfEdgeIn;
    public HalfEdge halfEdgeOut;
    public Vertex nextInPolygon;
    public Vertex nextInClass;
    public List<Vertex> sameVertices;
    public int polygonId;
    public int id3d;

    public Vertex(int id, int polygonId) {
        this.id = id;
        sameVertices = new ArrayList<>();
        halfEdgeOut = new HalfEdge(2 * id);
        halfEdgeOut.vertex = this;
        nextInClass = this;
        this.polygonId = polygonId;
        id3d = -1;
    }

    public String toString() {
        return "p" + polygonId + "v" + id;
    }

    public String toShow(int shift) {
        StringBuilder builder = new StringBuilder();
        StringBuilder blankShift = new StringBuilder();
        for (int s = 0; s < shift; s++) {
            blankShift.append(" ");
        }
        builder.append(blankShift)
                .append("Vertex #")
                .append(id)
                .append(":\n");
        for (int s = 0; s < 4; s++) {
            blankShift.append(" ");
        }
        builder.append(blankShift)
                .append("next vertex ")
                .append(nextInPolygon.id)
                .append("\n")
                .append(blankShift)
                .append("half-edge out ")
                .append(halfEdgeOut.id)
                .append("\n")
                .append(blankShift)
                .append("half-edge in ")
                .append(halfEdgeIn.id)
                .append("\n")
                .append("\n");
        return builder.toString();
    }

    public Vertex moveToInPolygon(int id) {
        Vertex start = this;
        while (start.id != id) {
            start = start.nextInPolygon;
        }
        return start;
    }

    public Vertex moveToInClass(int id) {
        Vertex start = this;
        while (start.id != id) {
            start = start.nextInClass;
        }
        return start;
    }

    private boolean checkIsInClass(Vertex newInClass) {
        Vertex start = this;
        do {
            if (start.nextInClass == newInClass) {
                return true;
            }
            start = start.nextInClass;
        } while (start != this);
        return false;
    }

    private Vertex previousInClass() {
        Vertex start = this;
        while (start.nextInClass != this) {
            start = start.nextInClass;
        }
        return start;
    }

    public void insertInClass(Vertex newInClass) {
        newInClass.previousInClass().nextInClass = this.nextInClass;
        this.nextInClass = newInClass;
    }

    public Vertex deleteFromClass(Vertex notInClass) {
        if (this.nextInClass != notInClass) {
            throw new RuntimeException("incorrect order in class of vertices polygon "
                    + polygonId + " vertex " + this.id + " and polygon " +
                    notInClass.polygonId + " vertex " + notInClass.id);
        }
        Vertex free = this.nextInClass;
        this.nextInClass = null;
        return free;
    }

    public List<HalfEdge> cyclicOrder() {
        List<HalfEdge> answer = new ArrayList<>();
        Vertex start = this;
        do {
            answer.add(start.halfEdgeOut);
            answer.add(start.halfEdgeIn);
            start = start.nextInClass;
        } while (start != this && start != null);
        return answer;
    }
}
