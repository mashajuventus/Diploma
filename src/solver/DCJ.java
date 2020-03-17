package solver;

import graph.Edge;
import utils.Pair;

import java.util.List;

public class DCJ {
    public List<Pair> edgesToCut;
    public List<Pair> edgesToGlue;

    public DCJ(List<Pair> edgesToCut, List<Pair> edgesToGlue) {
        this.edgesToCut = edgesToCut;
        this.edgesToGlue = edgesToGlue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        Pair cut0 = edgesToCut.get(0);
        Pair cut1 = edgesToCut.get(1);
        Pair glue0 = edgesToGlue.get(0);
        Pair glue1 = edgesToGlue.get(1);
        builder.append("  edges to cut:\n")
                .append("    ")
                .append(cut0.first.polygonId).append("-").append(cut0.first.edgeId)
                .append(" and ")
                .append(cut0.second.polygonId).append("-").append(cut0.second.edgeId)
                .append("\n")
                .append("    ")
                .append(cut1.first.polygonId).append("-").append(cut1.first.edgeId)
                .append(" and ")
                .append(cut1.second.polygonId).append("-").append(cut1.second.edgeId)
                .append("\n")
                .append("  edges to glue:\n")
                .append("    ")
                .append(glue0.first.polygonId).append("-").append(glue0.first.edgeId)
                .append(" and ")
                .append(glue0.second.polygonId).append("-").append(glue0.second.edgeId)
                .append("\n")
                .append("    ")
                .append(glue1.first.polygonId).append("-").append(glue1.first.edgeId)
                .append(" and ")
                .append(glue1.second.polygonId).append("-").append(glue1.second.edgeId)
                .append("\n");
        return builder.toString();
    }
}
