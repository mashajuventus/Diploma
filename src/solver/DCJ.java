package solver;

import graph.Edge;
import utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class DCJ {
    public List<Pair> edgesToCut;
    public List<Pair> edgesToGlue;

    public DCJ(List<Pair> edgesToCut, List<Pair> edgesToGlue) {
        this.edgesToCut = new ArrayList<>(edgesToCut);
        this.edgesToGlue = new ArrayList<>(edgesToGlue);
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
                .append(cut0.first)
                .append(" and ")
                .append(cut0.second)
                .append("\n")
                .append("    ")
                .append(cut1.first)
                .append(" and ")
                .append(cut1.second)
                .append("\n")
                .append("  edges to glue:\n")
                .append("    ")
                .append(glue0.first)
                .append(" and ")
                .append(glue0.second)
                .append("\n")
                .append("    ")
                .append(glue1.first)
                .append(" and ")
                .append(glue1.second)
                .append("\n");
        return builder.toString();
    }
}
