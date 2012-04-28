package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Executes the keyword search query.
 * Triples that contain the given keywords (separated by whitespace) in the object of the triple
 * of type literal are returned.
 *
 * @author Jan Michelfeit
 */
/*package*/class KeywordQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(KeywordQueryExecutor.class);

    private static final String KW_OCCURENCES_QUERY = "";
    private static final String METADATA_QUERY = "";
    private static final String SAME_AS_QUERY = "";
    private static final String LABELS_QUERY = "";

    /**
     * Creates a new instance of KeywordQueryExecutor.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    public KeywordQueryExecutor(SparqlEndpoint sparqlEndpoint) {
        super(sparqlEndpoint);
    }

    /**
     * Executes the keyword search query.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     */
    public NamedGraphSet findKeyword(String keywords, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        // TODO: escaping

        // Get the quads relevant for the query
        Collection<Quad> quads = getKeywordOccurrences(keywords, constraints);
        quads = new QuadCollection();
        quads.addAll(getLabels(keywords, constraints));

        // Gather all settings for Conflict Resolution
        ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
        // crSpec.setSameAsLinks(getSameAsLinks(keywords, constraints)); // TODO
        NamedGraphMetadataMap metadata = getMetadata(keywords, constraints);
        crSpec.setNamedGraphMetadata(metadata);

        // Apply conflict resolution
        ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
        Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

        LOG.debug("Query Execution: findKeyword() in {} ms", System.currentTimeMillis() - startTime);
        // Format and return result
        return convertToNGSet(resolvedQuads, metadata);
    }

    private Collection<Quad> getKeywordOccurrences(String keywords, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        QuadCollection quads = new QuadCollection();

        LOG.debug("Query Execution: getKeywordOccurrences() in {} ms", System.currentTimeMillis() - startTime);
        return quads;
    }

    private Collection<Quad> getLabels(String keywords, QueryConstraintSpec constraints) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();
        QuadCollection quads = new QuadCollection();
        LOG.debug("Query Execution: getLabels() in {} ms", System.currentTimeMillis() - startTime);
        return quads;
    }

    private NamedGraphMetadataMap getMetadata(String keywords, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();

        LOG.debug("Query Execution: getMetadata() in {} ms", System.currentTimeMillis() - startTime);
        return metadata;
    }

    private Iterator<Triple> getSameAsLinks(String keywords, QueryConstraintSpec constraints) throws ODCleanStoreException {
        long startTime = System.currentTimeMillis();

        // Build the result
        ArrayList<Triple> sameAsLinks = new ArrayList<Triple>();

        LOG.debug("Query Execution: getSameAsLinks() in %f ms", System.currentTimeMillis() - startTime);
        return sameAsLinks.iterator();
    }
}


