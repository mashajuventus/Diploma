package graph;

public class HalfEdge {
    public int id;
//    HalfEdge nextHalfEdge;
    public Vertex vertex; // ?????

    public HalfEdge(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return vertex.polygonId + "*" + id;
    }

//    public HalfEdge moveTo(int id) {
//        HalfEdge start = this;
//        while (start.id != id) {
//            start = start.nextHalfEdge;
//        }
//        return start;
//    }
}
