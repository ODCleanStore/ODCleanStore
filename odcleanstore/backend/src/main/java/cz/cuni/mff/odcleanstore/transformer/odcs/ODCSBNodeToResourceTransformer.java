package cz.cuni.mff.odcleanstore.transformer.odcs;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.shared.Utils;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

/**
 * Transformer for replacing blank nodes with unique URI resources.
 * The generated URIs have format <prefix><randomUUID>-<Virtuoso-nodeID>.
 * Transformer guarantees that occurrences of the same blank node within the transformed graph
 * will be assigned the same URI, however, occurrences of the blank node in other graphs will
 * be assigned a different URI when they are processed by the transformer.
 *
 * @author Jan Michelfeit
 */
public class ODCSBNodeToResourceTransformer implements Transformer {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSBNodeToResourceTransformer.class);

    private static final String URI_PREFIX_KEY = "uriPrefix";

    private static final String DEFAULT_URI_PREFIX = ODCS.getURI() + "genResource/";

    private static final String INSERT_QUERY_OBJECTS = "SPARQL INSERT INTO <%1$s> "
            + "\n { ?s ?p `IRI(fn:concat('%2$s', fn:substring-after(str(?o), 'nodeID://')))` }"
            + "\n FROM <%1$s> WHERE { ?s ?p ?o FILTER isBlank(?o) }";

    private static final String INSERT_QUERY_SUBJECTS = "SPARQL INSERT INTO <%1$s> "
            + "\n { `IRI(fn:concat('%2$s', fn:substring-after(str(?s), 'nodeID://')))` ?p ?o }"
            + "\n FROM <%1$s> WHERE { ?s ?p ?o FILTER isBlank(?s) }";

    private static final String DELETE_QUERY = "SPARQL DELETE FROM <%1$s> "
            + "\n { ?s ?p ?o }"
            + "\n FROM <%1$s> WHERE { ?s ?p ?o FILTER (isBlank(?s) || isBlank(?o)) }";

    @Override
    public void transformNewGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
        LOG.info("Converting blank nodes to resources in graph <{}>.", inputGraph.getGraphName());

        Properties configuration = parseProperties(context.getTransformerConfiguration());
        String prefix = configuration.getProperty(URI_PREFIX_KEY);
        if (prefix == null) {
            prefix = DEFAULT_URI_PREFIX;
        } else if (!Utils.isValidIRI(prefix)) {
            LOG.warn("Invalid URI <{}> passed as {} to ODCSBNodeToResourceTransformer - must be a valid URI",
                    prefix, URI_PREFIX_KEY);
            prefix = DEFAULT_URI_PREFIX;
        }

        // Make the generated resource URI really unique
        prefix += UUID.randomUUID().toString() + "-";

        VirtuosoConnectionWrapper connection = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());

            String query = String.format(Locale.ROOT, INSERT_QUERY_OBJECTS, inputGraph.getGraphName(), prefix);
            connection.execute(query);

            query = String.format(Locale.ROOT, INSERT_QUERY_SUBJECTS, inputGraph.getGraphName(), prefix);
            connection.execute(query);

            query = String.format(Locale.ROOT, DELETE_QUERY, inputGraph.getGraphName());
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
        transformNewGraph(inputGraph, context);
    }

    @Override
    public void shutdown() throws TransformerException {
        return; // Do nothing
    }

    /**
     * Parse transformer configuration to a Properties object.
     * @param configuration value of {@link TransformationContext#getTransformerConfiguration()}
     * @return properties parsed from configuration
     */
    private Properties parseProperties(String configuration) {
        Properties properties = new Properties();
        if (configuration == null) {
            return properties;
        }
        try {
            properties.load(new StringReader(configuration));
        } catch (IOException e) {
            LOG.warn("Failed to parse properties from {} transformer configuration.", getClass().getSimpleName());
        }
        return properties;
    }
}
