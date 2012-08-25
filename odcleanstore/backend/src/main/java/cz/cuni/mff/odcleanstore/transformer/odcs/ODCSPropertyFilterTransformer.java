package cz.cuni.mff.odcleanstore.transformer.odcs;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Transformer for filtering of reserved properties from incoming payload data and provenance metadata.
 *
 * @author Jan Michelfeit
 */
public class ODCSPropertyFilterTransformer implements Transformer {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSPropertyFilterTransformer.class);

    private static final String[] FILTERED_PROPERTIES = new String[] { ODCS.score, ODCS.publisherScore, ODCS.scoreTrace,
            ODCS.metadataGraph, ODCS.provenanceMetadataGraph, W3P.insertedAt, W3P.insertedBy, W3P.source,
            W3P.publishedBy, DC.license };

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

    @Override
    public void transformNewGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
        if (FILTERED_PROPERTIES.length == 0) {
            return;
        }

        LOG.info("Filtering reserved predicates from {} and its metadata graph.", inputGraph.getGraphName());
        VirtuosoConnectionWrapper connection = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());
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

    @Override
    public void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context)
            throws TransformerException {
        return; // Do nothing
    }

    @Override
    public void shutdown() throws TransformerException {
        return; // Do nothing
    }
}