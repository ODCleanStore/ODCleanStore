package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

/**
 * Executes the URI search query.
 * Triples that contain the given URI as their subject or object are returned.
 *
 * TODO: sameAs links between objects and between properties
 * @author Jan Michelfeit
 */
/*package*/class UriQueryExecutor extends QueryExecutorBase {

    // TODO: count(*) AS ?cardinality
    // TODO: use time
    // TODO UNION je nutny kvuly sameAs ve Virtuosu
    private static final String URI_OCCURENCES_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n WHERE {"
            + "\n   {"
            + "\n     GRAPH ?graph {"
            + "\n       ?s ?p ?o."
            + "\n       FILTER (?s = <%1$s>)"
            //+ "\n       FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?meta_score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     GRAPH ?graph {"
            + "\n       ?s ?p ?o."
            + "\n       FILTER (?o = <%1$s>)"
            //+ "\n       FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }"
            + "\n LIMIT %3$d";

    // TODO: limit by time & score
    private static final String METADATA_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?graph ?source ?score ?insertedAt ?publishedBy ?publisherScore"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n            ?s ?p ?o."
            + "\n            FILTER (?s = <%1$s>)"
            + "\n            FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n            ?s ?p ?o."
            + "\n            FILTER (?o = <%1$s>)"
            + "\n            FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         ?s ?p ?o"
            + "\n         FILTER (?s = <%1$s>)"
            + "\n         GRAPH ?graph {"
            + "\n            ?o ?labelProp ?label"
            + "\n            FILTER (?labelProp IN (%4$s))"
            + "\n         }"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         ?s ?p ?o"
            + "\n         FILTER (?o = <%1$s>)"
            + "\n         GRAPH ?graph {"
            + "\n            ?s ?labelProp ?label"
            + "\n            FILTER (?labelProp IN (%4$s))"
            + "\n         }"
            + "\n       }"
            + "\n     }"
            + "\n   }"
            + "\n   OPTIONAL { ?graph <" + W3P.source + "> ?source }"
            + "\n   OPTIONAL { ?graph <" + ODCS.score + "> ?score }"
            + "\n   OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n   OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy }"
            + "\n   OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy. "
            + "\n     ?publishedBy <" + ODCS.publisherScore + "> ?publisherScore }"
            + "\n   FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n }"
            + "\n LIMIT %3$d";

    private static final String SAME_AS_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?r1 ?r2"
            + "\n WHERE {"
            + "\n   {"
            + "\n     <%1$s> ?p1 ?r1"
            + "\n     FILTER (isURI(?r1))"
            + "\n     FILTER (?p1 != <" + OWL.sameAs + ">)"
            + "\n     <%1$s> ?p2 ?r2"
            + "\n     FILTER (isURI(?r2))"
            + "\n     FILTER (?p2 != <" + OWL.sameAs + ">)"
            + "\n     ?r1 <" + OWL.sameAs + "> ?r2"
            + "\n     FILTER (?r1 < ?r2)"
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     ?r1 ?p1 <%1$s>"
            + "\n     FILTER (isURI(?r1))"
            + "\n     FILTER (?p1 != <" + OWL.sameAs + ">)"
            + "\n     ?r2 ?p2 <%1$s>"
            + "\n     FILTER (isURI(?r2))"
            + "\n     FILTER (?p2 != <" + OWL.sameAs + ">)"
            + "\n     ?r1 <" + OWL.sameAs + "> ?r2"
            + "\n     FILTER (?r1 < ?r2)"
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }"
            + "\n GROUP BY ?r1 ?r2"
            + "\n LIMIT %3$d";

    // TODO: limit by time and quality too?
    private static final String LABELS_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?graph ?r ?labelProp ?label"
            + "\n WHERE {"
            + "\n   {"
            + "\n     ?s ?p ?r"
            + "\n     FILTER (?s = <%1$s>)"
            + "\n     GRAPH ?graph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     ?r ?p ?o"
            + "\n     FILTER (?o = <%1$s>)"
            + "\n     GRAPH ?graph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }"
            + "\n LIMIT %3$d";

    private static final String LABEL_PROPERTIES_LIST;

    static {
        assert (LABEL_PROPERTIES.length > 0);
        StringBuilder sb = new StringBuilder();
        for (String property : LABEL_PROPERTIES) {
            sb.append('<');
            sb.append(property);
            sb.append(">, ");
        }
        LABEL_PROPERTIES_LIST = sb.substring(0, sb.length() - 2);
    }

    /**
     * Executes the URI search query.
     *
     * @param uri searched URI
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     */
    public NamedGraphSet findURI(String uri, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) throws ODCleanStoreException, URISyntaxException {

        // Check that the URI is valid (must not be empty or null, should match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>' )
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            throw e; // rethrow
        }

        // Get the quads relevant for the query
        QuadCollection quads = getURIOccurrences(uri, constraints);
        quads.addAll(getLabels(uri, constraints));

        // Gather all settings for Conflict Resolution
        ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
        crSpec.setPreferredURIs(Collections.singleton(uri));
        // crSpec.setSameAsLinks(getSameAsLinks(uri, constraints)); // TODO
        NamedGraphMetadataMap metadata = getMetadata(uri, constraints);
        crSpec.setNamedGraphMetadata(metadata);

        // Apply conflict resolution
        ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
        Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

        // Format and return result
        return convertToNGSet(resolvedQuads, metadata);
    }

    private QuadCollection getURIOccurrences(String uri, QueryConstraintSpec constraints) throws ODCleanStoreException {
        // Prepare the query
        String query = String.format(Locale.ROOT, URI_OCCURENCES_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT);
        WrappedResultSet resultSet = executeQuery(query);

        QuadCollection quads = new QuadCollection();
        try {
            while (resultSet.next()) {
                Quad quad = new Quad(
                        resultSet.getNode(1),
                        resultSet.getNode(2),
                        resultSet.getNode(3),
                        resultSet.getNode(4));
                quads.add(quad);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return quads;
    }

    private QuadCollection getLabels(String uri, QueryConstraintSpec constraints) throws ODCleanStoreException {
        // Prepare the query
        String query = String.format(Locale.ROOT, LABELS_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT,
                LABEL_PROPERTIES_LIST);
        WrappedResultSet resultSet = executeQuery(query);

        QuadCollection quads = new QuadCollection();
        try {
            while (resultSet.next()) {
                Quad quad = new Quad(
                        resultSet.getNode("graph"),
                        resultSet.getNode("r"), // TODO: number indeces?
                        resultSet.getNode("labelProp"),
                        resultSet.getNode("label"));
                quads.add(quad);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return quads;
    }

    private NamedGraphMetadataMap getMetadata(String uri, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        // Execute the query
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT,
                LABEL_PROPERTIES_LIST);
        WrappedResultSet resultSet = executeQuery(query);

        // Build the result
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();
        try {
            while (resultSet.next()) {
                NamedGraphMetadata graphMetadata = new NamedGraphMetadata(resultSet.getString("graph"));

                String source = resultSet.getString("source");
                graphMetadata.setDataSource(source);

                Double score = resultSet.getDouble("score");
                graphMetadata.setScore(score);

                Date insertedAt = resultSet.getJavaDate("insertedAt");
                graphMetadata.setStored(insertedAt);

                String publishedBy = resultSet.getString("publishedBy");
                graphMetadata.setPublisher(publishedBy);

                Double publisherScore = resultSet.getDouble("publisherScore");
                graphMetadata.setPublisherScore(publisherScore);

                metadata.addMetadata(graphMetadata);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return metadata;
    }

    private Iterator<Triple> getSameAsLinks(String uri, QueryConstraintSpec constraints) throws ODCleanStoreException {
        // Execute the query
        String query = String.format(Locale.ROOT, SAME_AS_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT);
        final WrappedResultSet resultSet = executeQuery(query);

        // Build the result
        ArrayList<Triple> sameAsLinks = new ArrayList<Triple>();
        try {
            while (resultSet.next()) {
                sameAsLinks.add(new Triple(resultSet.getNode(1), SAME_AS_PROPERTY, resultSet.getNode(2)));
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        return sameAsLinks.iterator();
    }
}
