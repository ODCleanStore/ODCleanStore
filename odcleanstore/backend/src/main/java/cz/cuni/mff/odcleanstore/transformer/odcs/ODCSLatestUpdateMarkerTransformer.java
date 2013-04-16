package cz.cuni.mff.odcleanstore.transformer.odcs;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Transformer for marking the latest version of a named graph with {@link ODCS#isLatestUpdate} property.
 * A named graph A is considered an update of named graph B if:
 * <ul>
 * <li>Graphs A and B have the same update tag, or both have a null update tag.</li>
 * <li>Graphs A and B were inserted by the same user.</li>
 * <li>Graphs A and B have the same set of sources in metadata (see {@link #getSources()}).</li>
 * </ul>
 * The transformed graph will be labeled as the latest version and if it updates another graph in the clean database,
 * the other graph will be unmarked as being the latest version.
 *
 * @author Jan Michelfeit
 */
public class ODCSLatestUpdateMarkerTransformer implements Transformer {
    private static final Logger LOG = LoggerFactory.getLogger(ODCSLatestUpdateMarkerTransformer.class);

    private static final String INSERT_MARKER_QUERY = "SPARQL INSERT INTO <%1$s> "
            + "\n { <%2$s> <" + ODCS.isLatestUpdate + "> 1}";

    /**
     * SPARQL query for obtaining all sources of the given graph.
     * Format with arguments: (1) metadata graph, (2) data graph
     */
    private static final String GET_SOURCES_QUERY = "SPARQL SELECT ?source FROM <%1$s>"
            + "\n WHERE { <%2$s> <" + ODCS.source + "> ?source. FILTER isIRI(?source). }";

    /**
     * SPARQL query for obtaining update tag of the given graph.
     * Format with arguments: (1) metadata graph, (2) data graph
     */
    private static final String GET_UPDATE_TAG_USER_QUERY = "SPARQL SELECT ?updateTag ?insertedBy FROM <%1$s>"
            + "\n WHERE {"
            + "\n   <%2$s> <" + ODCS.insertedBy + "> ?insertedBy. "
            + "\n   OPTIONAL { <%2$s> <" + ODCS.updateTag + "> ?updateTag. } "
            + "\n }";

    /**
     * SPARQL query for obtaining graphs that are being updated and are labeled as latest update. /
     * Format with arguments: (1) comma-separated list of sources, (2) inserted by value,
     * (3) filter on ?updateTag, (4) number of sources
     */
    private static final String GET_MARKERS_QUERY = "SPARQL SELECT ?graph ?metadataGraph"
            + "\n WHERE {"
            + "\n   ?graph <" + ODCS.source + "> %1$s."
            + "\n   ?graph <" + ODCS.source + "> ?source."
            + "\n   ?graph <" + ODCS.insertedBy + "> '%2$s'."
            + "\n   ?graph <" + ODCS.metadataGraph + "> ?metadataGraph."
            + "\n   ?graph <" + ODCS.isLatestUpdate + "> 1."
            + "\n   OPTIONAL { ?graph <" + ODCS.updateTag + "> ?updateTag }."
            + "\n   FILTER (%3$s)."
            + "\n }"
            + "\n GROUP BY ?graph ?metadataGraph"
            + "\n HAVING COUNT(?source) = %4$d";

    /**
     * SPARQL query for removing the isLatestUpdate marker.
     * Format with arguments: (1) metadata graph, (2) data graph
     */
    private static final String DELETE_MARKER_QUERY = "SPARQL DELETE FROM <%1$s>  {<%2$s>  <" + ODCS.isLatestUpdate + "> 1}";

    @Override
    public void transformGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
        if (context.getTransformationType() == EnumTransformationType.EXISTING) {
            LOG.info("Marking graph as latest update skipped for graph from clean database {}", inputGraph.getGraphName());
            return;
        }

        VirtuosoConnectionWrapper dirtyConnection = null;
        VirtuosoConnectionWrapper cleanConnection = null;
        WrappedResultSet updatedLatestGraphs = null;
        WrappedResultSet updateTagAndUser = null;
        try {
            cleanConnection = VirtuosoConnectionFactory.createJDBCConnection(context.getCleanDatabaseCredentials());
            dirtyConnection = VirtuosoConnectionFactory.createJDBCConnection(context.getDirtyDatabaseCredentials());

            // Get sources
            List<String> sources = getSources(inputGraph, dirtyConnection);
            if (sources.isEmpty()) {
                LOG.info("No sources found, graph will not be marked as latest update");
                return;
            }

            // Get other metadata
            updateTagAndUser = getUpdateTagAndUser(inputGraph, dirtyConnection);
            if (!updateTagAndUser.next()) {
                LOG.info("No value of odcs:insertedBy found, graph will not be marked as latest update");
                return;
            }
            String updateTag = updateTagAndUser.getString("updateTag");
            String insertedBy = updateTagAndUser.getString("insertedBy");

            // Retrieve updated graph(s) labeled as latest update (should be only one, but handle case when there are more,
            // just in case)
            updatedLatestGraphs = getUpdatedLatestGraphs(updateTag, insertedBy, sources, inputGraph, cleanConnection);
            while (updatedLatestGraphs.next()) {
                String graph = updatedLatestGraphs.getString("graph");
                String metadataGraph = updatedLatestGraphs.getString("metadataGraph");

                deleteLatestUpdateLabel(graph, metadataGraph, cleanConnection);
            }

            // Mark current graph as latest update
            markLatestUpdate(inputGraph, dirtyConnection);

        } catch (DatabaseException e) {
            throw new TransformerException(e);
        } catch (SQLException e) {
            throw new TransformerException(e);
        } finally {
            if (updatedLatestGraphs != null) {
                updatedLatestGraphs.closeQuietly();
            }
            if (updateTagAndUser != null) {
                updateTagAndUser.closeQuietly();
            }
            if (dirtyConnection != null) {
                dirtyConnection.closeQuietly();
            }
            if (cleanConnection != null) {
                cleanConnection.closeQuietly();
            }
        }
    }

    /** Returns list of sources for the processed graph. */
    private List<String> getSources(TransformedGraph inputGraph, VirtuosoConnectionWrapper dirtyConnection)
            throws QueryException {

        String query = String.format(Locale.ROOT, GET_SOURCES_QUERY,
                inputGraph.getMetadataGraphName(), inputGraph.getGraphName());

        LinkedList<String> sources = new LinkedList<String>();
        WrappedResultSet queryResult = dirtyConnection.executeSelect(query);
        try {
            while (queryResult.next()) {
                String source = queryResult.getString(1);
                sources.add(source);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            if (queryResult != null) {
                queryResult.closeQuietly();
            }
        }
        return sources;
    }

    /** Return update tag of the processed graph, or null if there is none. */
    private WrappedResultSet getUpdateTagAndUser(TransformedGraph inputGraph, VirtuosoConnectionWrapper connection)
            throws QueryException {
        String query = String.format(Locale.ROOT, GET_UPDATE_TAG_USER_QUERY,
                inputGraph.getMetadataGraphName(), inputGraph.getGraphName());
        return connection.executeSelect(query);
    }

    /** Returns list of graphs that will be updated by the currently processed graph and are labeled as latest version. */
    private WrappedResultSet getUpdatedLatestGraphs(String updateTag, String insertedBy, List<String> sources,
            TransformedGraph inputGraph, VirtuosoConnectionWrapper cleanConnection) throws QueryException {

        assert !sources.isEmpty();

        StringBuilder sourcesQueryPart = new StringBuilder();
        for (String sourceURI : sources) {
            sourcesQueryPart
                    .append('<')
                    .append(sourceURI)
                    .append('>')
                    .append(',');
        }
        sourcesQueryPart.deleteCharAt(sourcesQueryPart.length() - 1); // remove last separator; sources cannot be empty
        String insertedByEscaped = ODCSUtils.escapeSPARQLLiteral(insertedBy);
        if (updateTag != null) {
            String updateTagFilter = "?updateTag = '" + ODCSUtils.escapeSPARQLLiteral(updateTag) + "'";
            String query = String.format(Locale.ROOT, GET_MARKERS_QUERY,
                    sourcesQueryPart, insertedByEscaped, updateTagFilter, sources.size());
            return cleanConnection.executeSelect(query);
        } else {
            String query = String.format(Locale.ROOT, GET_MARKERS_QUERY,
                    sourcesQueryPart, insertedByEscaped, "!bound(?updateTag)", sources.size());
            return cleanConnection.executeSelect(query);
        }
    }

    /** Removes outdated latest update marker for the given graph. */
    private void deleteLatestUpdateLabel(String graph, String metadataGraph, VirtuosoConnectionWrapper cleanConnection)
            throws QueryException {

        LOG.info("Unmarking graph as the latest update - {}", graph);
        String query = String.format(Locale.ROOT, DELETE_MARKER_QUERY, metadataGraph, graph);
        cleanConnection.execute(query);
    }

    /** Marks the processed graph as latest update. */
    private void markLatestUpdate(TransformedGraph inputGraph, VirtuosoConnectionWrapper connection) throws QueryException {
        LOG.info("Marking graph as the latest update - {}", inputGraph.getGraphName());
        String query = String.format(Locale.ROOT, INSERT_MARKER_QUERY,
                inputGraph.getMetadataGraphName(), inputGraph.getGraphName());
        connection.execute(query);
    }

    @Override
    public void shutdown() throws TransformerException {
        return; // Do nothing
    }
}
