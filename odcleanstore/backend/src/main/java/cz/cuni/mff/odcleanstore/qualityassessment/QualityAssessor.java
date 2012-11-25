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
 * It provides a debugging interface that does not affect
 * metadata graphs but instead returns a structured output in
 * form of a GraphScoreWithTrace instance.
 *
 * @author Jakub Daniel
 */
public interface QualityAssessor extends Transformer {
	public static interface GraphScoreWithTrace extends Serializable {

		public String getGraphName();
		public void setGraphName(String graphName);
		public Double getScore();
		public List<QualityAssessmentRule> getTrace();
	}

	public List<GraphScoreWithTrace> debugRules(HashMap<String, String> graphs, TransformationContext context, TableVersion tableVersion)
			throws TransformerException;
}
