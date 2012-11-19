package cz.cuni.mff.odcleanstore.engine.outputws.output;

import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;

/**
 * Formats a result of a query and returns it as an instance of {@link Representation}.
 * @author Jan Michelfeit
 */
public interface QueryResultFormatter {
    /**
     * Returns a formatted representation of a query result.
     * @param result query result
     * @param requestReference representation of the requested URI
     * @return representation of the formatted output
     */
    Representation format(BasicQueryResult result, Reference requestReference);

    /**
     * Returns a formatted representation of a named graph query result.
     * @param metadataResult result of metadata query about the requested named graph
     * @param qaResult result of quality assessment over the given named graph; can be null
     * @param totalTime execution time of the query
     * @param requestReference representation of the requested URI
     * @return representation of the formatted output
     */
    Representation format(MetadataQueryResult metadataResult, GraphScoreWithTrace qaResult, long totalTime,
            Reference requestReference);
}
