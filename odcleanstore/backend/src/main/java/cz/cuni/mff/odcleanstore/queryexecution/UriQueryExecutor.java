package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Executes the URI search query.
 * Triples that contain the given URI as their subject or object are returned.
 *
 * TODO: sameAs links between objects and between properties
 * @author Jan Michelfeit
 */
/*package*/class UriQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(UriQueryExecutor.class);

    // TODO: use time
    // UNION je nutny kvuli spravnemu fungovani owl:sameAs inference pro hledane URI ve Virtuosu
    // poddotaz je nutny kvuli prekladu subjectu/objectu na jejich jediny owl:sameAs ekvivalent
    // pri pouziti poddotazu tento preklad provede Virtuoso samo - diky tomu neni nutne ziskavat linky z databaze
    // explicitne a predavat je do ConflictResolverSpec
    private static final String URI_OCCURENCES_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?graph ?s ?p ?o"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?s ?p ?o"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?s = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)" // TODO: asi opravdu vyfiltrovat?
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?meta_score }" // TODO: non-optional if given?
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?p ?o."
            + "\n           FILTER (?o = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n       }"
            + "\n     }"
            + "\n   }"
            + "\n }"
            + "\n LIMIT %3$d";

    // TODO: limit by time & score
    // TODO: omit metadata for additional labels?
    // TODO: pridat do dotazu vnitrni limity?
    // metadata i pro labels
    // source se povazuje za povinny
    private static final String METADATA_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?graph ?source ?score ?insertedAt ?publishedBy ?publisherScore"
            + "\n WHERE {"
            + "\n   {"
            + "\n     {"
            + "\n       SELECT DISTINCT ?graph" // TODO: remove DISTINCT??
            + "\n       WHERE {"
            + "\n         {"
            + "\n           GRAPH ?graph {"
            + "\n              ?s ?p ?o."
            + "\n              FILTER (?s = <%1$s>)"
            + "\n              FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n           }"
            + "\n         }"
            + "\n         UNION"
            + "\n         {"
            + "\n           GRAPH ?graph {"
            + "\n              ?s ?p ?o."
            + "\n              FILTER (?o = <%1$s>)"
            + "\n              FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n           }"
            + "\n         }"
            + "\n       }"
            + "\n     }"
            + "\n     UNION"
            + "\n     {"
            + "\n       SELECT DISTINCT ?graph"
            + "\n       WHERE {"
            + "\n         {"
            + "\n           SELECT DISTINCT ?g"
            + "\n           WHERE {"
            + "\n             {"
            + "\n               GRAPH ?g {" // TODO: remove with regex
            + "\n                 ?s ?p ?r."
            + "\n                 FILTER (?s = <%1$s>)"
            //+ "\n                 FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
            + "\n               }"
            + "\n               OPTIONAL { ?g <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n               OPTIONAL { ?g <" + ODCS.score + ">  ?meta_score }" // TODO: non-optional if given?
            + "\n               FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n               FILTER regex(?g, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n               FILTER (!isLITERAL(?r))"
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?g {"
            + "\n                 ?s ?r ?o."
            + "\n                 FILTER (?s = <%1$s>)"
            //+ "\n                 FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
            + "\n               }"
            + "\n               OPTIONAL { ?g <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n               OPTIONAL { ?g <" + ODCS.score + ">  ?meta_score }" // TODO: non-optional if given?
            + "\n               FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n               FILTER regex(?g, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?g {"
            + "\n                 ?r ?p ?o."
            + "\n                 FILTER (?o = <%1$s>)"
            //+ "\n                 FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n               }"
            + "\n               OPTIONAL { ?g <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n               OPTIONAL { ?g <" + ODCS.score + ">  ?score }"
            + "\n               FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n               FILTER regex(?g, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n             }"
            + "\n             UNION"
            + "\n             {"
            + "\n               GRAPH ?g {"
            + "\n                 ?s ?r ?o."
            + "\n                 FILTER (?o = <%1$s>)"
            //+ "\n                 FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n               }"
            + "\n               OPTIONAL { ?g <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n               OPTIONAL { ?g <" + ODCS.score + ">  ?score }"
            + "\n               FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n               FILTER regex(?g, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n             }"
            + "\n           }"
            + "\n         }"
            + "\n         GRAPH ?graph {"
            + "\n           ?r ?labelProp ?label"
            + "\n         }"
            + "\n         FILTER (?labelProp IN (%4$s))"
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")"
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
            + "\n   FILTER (bound(?source))"
            + "\n }"
            + "\n LIMIT %3$d";

    private static final String LABELS_QUERY = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT ?labelGraph ?r ?labelProp ?label WHERE {{"
            + "\n SELECT DISTINCT ?labelGraph ?r ?labelProp ?label"
            + "\n WHERE {"
            + "\n   {"
            + "\n     SELECT DISTINCT ?graph ?r"
            + "\n     WHERE {"
            + "\n       {"
            + "\n         GRAPH ?graph {" // TODO: remove with regex
            + "\n           ?s ?p ?r."
            + "\n           FILTER (?s = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?meta_score }" // TODO: non-optional if given?
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n         FILTER (!isLITERAL(?r))"
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           FILTER (?s = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?meta_score }" // TODO: non-optional if given?
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?r ?p ?o."
            + "\n           FILTER (?o = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n       }"
            + "\n       UNION"
            + "\n       {"
            + "\n         GRAPH ?graph {"
            + "\n           ?s ?r ?o."
            + "\n           FILTER (?o = <%1$s>)"
            //+ "\n           FILTER (?p != <" + OWL.sameAs + ">)"
            + "\n         }"
            + "\n         OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n         OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n         FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n         FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n       }"
            + "\n     }"
            + "\n   }"
            + "\n   GRAPH ?labelGraph {"
            + "\n     ?r ?labelProp ?label"
            + "\n   }"
            + "\n   FILTER (?labelProp IN (%4$s))"
            + "\n   FILTER regex(?labelGraph, \"^" + NG_PREFIX_FILTER + "\")"
            + "\n }"
            + "\n }}"
            + "\n LIMIT %3$d";

    // TODO: limit by time and quality too?
    // dela to same co LABELS_QUERY, ale je approx. 30 times slower
    private static final String LABELS_QUERY2 = "SPARQL"
            + "\n DEFINE input:same-as \"yes\""
            + "\n SELECT DISTINCT ?labelGraph ?r ?labelProp ?label"
            + "\n WHERE {"
            + "\n   {"
            + "\n     ?s ?p ?r"
            + "\n     FILTER (?s = <%1$s>)"
            + "\n     GRAPH ?labelGraph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?labelGraph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     ?s ?r ?o"
            + "\n     FILTER (?s = <%1$s>)"
            + "\n     GRAPH ?labelGraph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?labelGraph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     ?r ?p ?o"
            + "\n     FILTER (?o = <%1$s>)"
            + "\n     GRAPH ?labelGraph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?labelGraph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n   UNION"
            + "\n   {"
            + "\n     ?s ?r ?o"
            + "\n     FILTER (?o = <%1$s>)"
            + "\n     GRAPH ?labelGraph {"
            + "\n       ?r ?labelProp ?label"
            + "\n       FILTER (?labelProp IN (%4$s))"
            + "\n     }"
            + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
            + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
            + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
            + "\n     FILTER regex(?labelGraph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
            + "\n   }"
            + "\n }"
            + "\n LIMIT %3$d";


    /**
     * Creates a new instance of UriQueryExecutor.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    public UriQueryExecutor(SparqlEndpoint sparqlEndpoint) {
        super(sparqlEndpoint);
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

        long startTime = System.currentTimeMillis(); // TODO: only if LOG.isDebugEnabled()
        // Check that the URI is valid (must not be empty or null, should match '<' ([^<>"{}|^`\]-[#x00-#x20])* '>' )
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            throw e; // rethrow
        }

        // Get the quads relevant for the query
        Collection<Quad> quads = getURIOccurrences(uri, constraints);
        quads.addAll(getLabels(uri, constraints));

        // Gather all settings for Conflict Resolution
        ConflictResolverSpec crSpec = new ConflictResolverSpec(RESULT_GRAPH_PREFIX, aggregationSpec);
        crSpec.setPreferredURIs(Collections.singleton(uri));
        // ... no need for sameAs links - see URI_OCCURENCES_QUERY
        crSpec.setSameAsLinks(Collections.<Triple>emptySet().iterator());
        NamedGraphMetadataMap metadata = getMetadata(uri, constraints);
        crSpec.setNamedGraphMetadata(metadata);

        // Apply conflict resolution
        ConflictResolver conflictResolver = ConflictResolverFactory.createResolver(crSpec);
        Collection<CRQuad> resolvedQuads = conflictResolver.resolveConflicts(quads);

        LOG.debug("Query Execution: findURI() in {} ms", System.currentTimeMillis() - startTime);
        // Format and return result
        return convertToNGSet(resolvedQuads, metadata);
    }

    private Collection<Quad> getURIOccurrences(String uri, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        // Prepare the query
        String query = String.format(Locale.ROOT, URI_OCCURENCES_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT);
        WrappedResultSet resultSet = executeQuery(query);
        LOG.debug("Query Execution: getURIOccurences() query took {} ms", System.currentTimeMillis() - startTime);

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
            resultSet.close();
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        LOG.debug("Query Execution: getURIOccurrences() in {} ms", System.currentTimeMillis() - startTime);
        return quads;
    }

    private Collection<Quad> getLabels(String uri, QueryConstraintSpec constraints) throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        // Prepare the query
        String query = String.format(Locale.ROOT, LABELS_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT,
                LABEL_PROPERTIES_LIST);
        WrappedResultSet resultSet = executeQuery(query);
        LOG.debug("Query Execution: getLabels() query took {} ms", System.currentTimeMillis() - startTime);

        QuadCollection quads = new QuadCollection();
        try {
            while (resultSet.next()) {
                Quad quad = new Quad(
                        resultSet.getNode("labelGraph"),
                        resultSet.getNode("r"), // TODO: number indeces?
                        resultSet.getNode("labelProp"),
                        resultSet.getNode("label"));
                quads.add(quad);
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        LOG.debug("Query Execution: getLabels() in {} ms", System.currentTimeMillis() - startTime);
        return quads;
    }


    private NamedGraphMetadataMap getMetadata(String uri, QueryConstraintSpec constraints)
            throws ODCleanStoreException {

        long startTime = System.currentTimeMillis();
        // Execute the query
        String query = String.format(Locale.ROOT, METADATA_QUERY, uri, constraints.getMinScore(), DEFAULT_LIMIT,
                LABEL_PROPERTIES_LIST);
        WrappedResultSet resultSet = executeQuery(query);
        LOG.debug("Query Execution: getMetadata() query took {} ms", System.currentTimeMillis() - startTime);

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
            resultSet.close();
        } catch (SQLException e) {
            throw new QueryException(e);
        }

        LOG.debug("Query Execution: getMetadata() in {} ms", System.currentTimeMillis() - startTime);
        return metadata;
    }
}
