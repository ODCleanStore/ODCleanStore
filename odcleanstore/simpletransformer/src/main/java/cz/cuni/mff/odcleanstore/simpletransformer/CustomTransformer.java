package cz.cuni.mff.odcleanstore.simpletransformer;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Simple Testing custom Transformer.
 * 
 * @author Petr Jerman
 */
public class CustomTransformer implements Transformer {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTransformer.class);

    private static final String[] FILTERED_PROPERTIES = new String[] {
            ODCS.SCORE.stringValue(),
            ODCS.PUBLISHER_SCORE.stringValue(),
            ODCS.SCORE_TRACE.stringValue(),
            ODCS.METADATA_GRAPH.stringValue(),
            ODCS.PROVENANCE_METADATA_GRAPH.stringValue(),
            ODCS.SOURCE_GRAPH.stringValue(),
            ODCS.INSERTED_AT.stringValue(),
            ODCS.INSERTED_BY.stringValue(),
            ODCS.SOURCE.stringValue(),
            ODCS.PUBLISHED_BY.stringValue(),
            ODCS.LICENSE.stringValue(),
            ODCS.UPDATE_TAG.stringValue()
    };

    private static final String FILTERED_PROPERTIES_LIST;

    static {
        StringBuilder listBuilder = new StringBuilder();
        if (FILTERED_PROPERTIES.length > 1) {
            listBuilder.append('<').append(FILTERED_PROPERTIES[0]).append('>');
            for (int i = 1; i < FILTERED_PROPERTIES.length; i++) {
                listBuilder.append(", <").append(FILTERED_PROPERTIES[i]).append('>');
            }
        }
        FILTERED_PROPERTIES_LIST = listBuilder.toString();
    }

    private static final String DELETE_QUERY = "SPARQL DELETE FROM <%1$s> { ?s ?p ?o }"
            + "\n WHERE { ?s ?p ?o FILTER (?p IN (%2$s)) }";

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.Transformer#transformGraph(cz.cuni.mff.odcleanstore.transformer.TransformedGraph, cz.cuni.mff.odcleanstore.transformer.TransformationContext)
     */
    @Override
    public void transformGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
        if (context.getTransformationType() == EnumTransformationType.EXISTING) {
            LOG.info("Filtering reserved predicates skipped for graph from clen database {}", inputGraph.getGraphName());
            return;
        }

        LOG.info("Filtering reserved predicates from {} and its metadata graph.", inputGraph.getGraphName());
        if (FILTERED_PROPERTIES.length == 0) {
            return;
        }

        VirtuosoConnectionWrapper connection = null;
        try {
            connection = VirtuosoConnectionFactory.createJDBCConnection(context.getDirtyDatabaseCredentials());
            String query = String.format(Locale.ROOT, DELETE_QUERY,
                    inputGraph.getGraphName(), FILTERED_PROPERTIES_LIST);
            connection.execute(query);

            query = String.format(Locale.ROOT, DELETE_QUERY,
                    inputGraph.getProvenanceMetadataGraphName(), FILTERED_PROPERTIES_LIST);
            connection.execute(query);
        } catch (DatabaseException e) {
            throw new TransformerException(e);
        } finally {
            if (connection != null) {
                connection.closeQuietly();
            }
        }

    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.Transformer#shutdown()
     */
    @Override
    public void shutdown() throws TransformerException {
        return; // Do nothing
    }
}
