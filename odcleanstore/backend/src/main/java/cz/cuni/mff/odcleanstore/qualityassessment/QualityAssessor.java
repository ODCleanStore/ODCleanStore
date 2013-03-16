package cz.cuni.mff.odcleanstore.qualityassessment;

import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

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
    /**
     * Container of QA score with explanation for a named graph.
     */
    public interface GraphScoreWithTrace extends Serializable {

        String getGraphName();

        void setGraphName(String graphName);

        Double getScore();

        List<QualityAssessmentRule> getTrace();
    }

    public List<GraphScoreWithTrace> debugRules(HashMap<String, String> graphs, TransformationContext context,
            TableVersion tableVersion)
            throws TransformerException;
}
