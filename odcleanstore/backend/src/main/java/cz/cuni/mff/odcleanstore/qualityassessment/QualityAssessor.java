package cz.cuni.mff.odcleanstore.qualityassessment;

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
}
