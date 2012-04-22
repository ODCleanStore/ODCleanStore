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
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Executes the URI search query.
 * Triples that contain the given URI as their subject or object are returned.
 *
 * @author Jan Michelfeit
 */
/*package*/class UriQueryExecutor extends QueryExecutorBase {

    // TODO: count(*) AS ?cardinality
    // TODO: use time
    // TODO UNION je nutny kvuly sameAs ve Virtuosu
    private static final String URI_OCCURENCES_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?graph ?s ?p ?o ?insertedAt"
            + "\n WHERE {"
            + "\n   {"
            + "\n     GRAPH ?graph {"
            + "\n       ?s ?p ?o."
            + "\n       FILTER (?s = <%1$s>)"
            + "\n       FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
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
            + "\n       FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }";
    // + "\n LIMIT %3$d";

    private static final String METADATA_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?graph ?source ?score ?insertedAt ?publishedBy ?publisherScore"
            + "\n WHERE {"
            + "\n   {"
            + "\n     GRAPH ?graph {"
            + "\n       ?s ?p ?o."
            + "\n       FILTER (?s = <%1$s>)"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.source + "> ?source }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + "> ?score }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy }"
            + "\n     OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy. "
            + "\n       ?publishedBy <" + ODCS.publisherScore + "> ?publisherScore }"
            + "\n     FILTER(!bound(?meta_quality) || ?meta_quality > %2$f) ."
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION" // TODO: je nutny UNION?
            + "\n   {"
            + "\n     GRAPH ?graph {"
            + "\n       ?s ?p ?o."
            + "\n       FILTER (?o = <%1$s>)"
            + "\n       FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.source + "> ?source }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + "> ?score }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }" // TODO: really optional?
            + "\n     OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy }"
            + "\n     OPTIONAL { ?graph <" + W3P.publishedBy + "> ?publishedBy. "
            + "\n       ?publishedBy <" + ODCS.publisherScore + "> ?publisherScore }"
            + "\n     FILTER(!bound(?meta_quality) || ?meta_quality > %2$f) ."
            + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }"
            + "\n LIMIT %3$d";

    /**
     * Executes the URI search query.
     *
     * @param uri searched URI
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     */
    public NamedGraphSet findURI(String uri, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) throws ODCleanStoreException {

        // TODO: uri must not be empty or null, must match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>'

        QuadCollection quads = getURIOccurrences(uri, constraints);

        // Gather all settings for Conflict Resolution
        ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
        crSpec.setPreferredURIs(Collections.singleton(uri));
        // crSpec.setSameAsLinks(sameAsLinks);
        crSpec.setNamedGraphMetadata(getMetadata(uri, constraints));

        // Apply conflict resolution
        ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
        Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

        return convertToNGSet(resolvedQuads);
    }

    private QuadCollection getURIOccurrences(String uri, QueryConstraintSpec constraints) throws ODCleanStoreException {
        // Prepare the query
        String query =
                String.format(Locale.ROOT, URI_OCCURENCES_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT);
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

    private QuadCollection getLabels() {
        return null;
    }

    private NamedGraphMetadataMap getMetadata(String uri, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        // Execute the query
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT);
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

    /**
     * @todo return a different type
     */
    private Collection<Triple> getSameAsLinks() {
        return null;
    }

    private NamedGraphSet convertToNGSet(Collection<CRQuad> crQuads) {
        NamedGraphSet result = new NamedGraphSetImpl();

        // TODO: add metadata
        for (CRQuad crQuad : crQuads) {
            result.addQuad(crQuad.getQuad());
        }

        return result;
    }

}
