package cz.cuni.mff.odcleanstore.qualityassessment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.transformer.*;

/**
 * Quality Assessment component.
 *
 * This is a special implementation of transformer. It
 * assigns a quality indicator (score) to the named graph
 * defined by inputGraph.
 *
 * The resulting score is stored in the metadataGraph
 * defined by inputGraph (rdf property odcs:score). A textual
 * trace of the whole process is stored there too
 * (odcs:scoreTrace).
 *
 * @author Jakub Daniel
 */
public interface QualityAssessor extends Transformer {
	public static class GraphScoreWithTrace implements Serializable {
		private static final long serialVersionUID = 1L;

		private String graphName;
		private Double score;
		private List<QualityAssessmentRule> trace;

		public GraphScoreWithTrace(Double score, List<QualityAssessmentRule> trace) {
			this.score = score;
			this.trace = trace;
		}

		public String getGraphName() {
			return graphName;
		}

		public void setGraphName(String graphName) {
			this.graphName = graphName;
		}

		public Double getScore() {
			return score;
		}

		public List<QualityAssessmentRule> getTrace() {
			return trace;
		}
	}

	public List<GraphScoreWithTrace> debugRules(HashMap<String, String> graphs, TransformationContext context, TableVersion tableVersion)
			throws TransformerException;
}
