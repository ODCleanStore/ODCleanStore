package cz.cuni.mff.odcleanstore.simpletransformer;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
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
 * @author Bramburek
 */
public class CustomTransformer implements Transformer {
    private static final Logger LOG = LoggerFactory.getLogger(CustomTransformer.class);

    private static final String[] FILTERED_PROPERTIES = new String[] { ODCS.score, ODCS.publisherScore, ODCS.scoreTrace,
            ODCS.metadataGraph, ODCS.provenanceMetadataGraph, ODCS.sourceGraph, ODCS.insertedAt, ODCS.insertedBy, 
            ODCS.source, ODCS.publishedBy, ODCS.license };

    private static final String DELETE_QUERY = "SPARQL DELETE FROM <%1$s> { ?s <%2$s> ?o } WHERE { ?s <%2$s> ?o }";

    @Override
    public void transformNewGraph(TransformedGraph inputGraph, TransformationContext context)
            throws TransformerException {

        LOG.info("Running CustomTransformer on graph {}", inputGraph.getGraphName());
        VirtuosoConnectionWrapper connection = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());
            for (String property : FILTERED_PROPERTIES) {
                String query = String.format(Locale.ROOT, DELETE_QUERY, inputGraph.getGraphName(), property);
                connection.execute(query);

                //query = String.format(Locale.ROOT, DELETE_QUERY, inputGraph.getMetadataGraphName(), property);
                //connection.execute(query);
            }

        } catch (DatabaseException e) {
            throw new TransformerException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (ConnectionException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context)
            throws TransformerException {
    }

    @Override
    public void shutdown() throws TransformerException {
    }
}
