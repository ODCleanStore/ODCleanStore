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
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.util.Collection;
import java.util.Collections;
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
    private static final String URI_OCCURENCES_QUERY =
            "SELECT DISTINCT ?graph ?s ?p ?o ?insertedAt"
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
                    + "\n   UNION" // TODO: je nutny UNION?
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

    private static final String METADATA_QUERY =
            "SELECT DISTINCT ?graph ?source ?s ?p ?o ?insertedAt"
                    + "\n WHERE {"
                    + "\n   {"
                    + "\n     GRAPH ?graph {"
                    + "\n       ?s ?p ?o."
                    + "\n       FILTER (?s = <%1$s>)"
                    + "\n       FILTER (?p != <" + OWL.sameAs + ">)" // TODO: ?
                    + "\n     }"
                    + "\n     OPTIONAL { ?graph <" + W3P.source + "> ?source }"
                    + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
                    + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?meta_score }"
                    + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
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
                    + "\n     OPTIONAL { ?graph <" + W3P.insertedAt + "> ?insertedAt }"
                    + "\n     OPTIONAL { ?graph <" + ODCS.score + ">  ?score }"
                    + "\n     FILTER(!bound(?score) || ?score > %2$f) ."
                    + "\n     FILTER regex(?graph, \"^" + NG_PREFIX_FILTER + "\")" // TODO: remove
                    + "\n   }"
                    + "\n }";
            /*"SELECT DISTINCT ?graph ?source ?score ?insertedAt ?publishedBy ?publisherScore"
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
                    + "\n }";*/

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

        QuadCollection quads = null;
        try {
            quads = getURIOccurrences(uri, constraints);
        } catch (JenaException e) {
            throw new QueryException(e);
        }

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

    private QuadCollection getURIOccurrences(String uri, QueryConstraintSpec constraints) {
        // Prepare the query
        String queryString = String.format(Locale.ROOT, URI_OCCURENCES_QUERY, uri,
                constraints.getMinScore());
        com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
        query.setLimit(DEFAULT_LIMIT);

        // Execute the query
        VirtGraph virtGraph = new VirtGraph(CONNECTION_STRING, USER, PASSWORD); // TODO: keep
        virtGraph.setSameAs(true);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, virtGraph);
        com.hp.hpl.jena.query.ResultSet queryResults = vqe.execSelect();

        // Prepare result
        QuadCollection quads = new QuadCollection();
        while (queryResults.hasNext()) {
            QuerySolution solution = queryResults.next();
            Quad quad = new Quad(
                    solution.get("graph").asNode(),
                    solution.get("s").asNode(),
                    solution.get("p").asNode(),
                    solution.get("o").asNode());
            quads.add(quad);
        }

        return quads;
    }

    private QuadCollection getLabels() {
        return null;
    }

    private NamedGraphMetadataMap getMetadata(String uri, QueryConstraintSpec constraints)
            throws ODCleanStoreException {
        // Prepare the query
        String queryString = String.format(Locale.ROOT, METADATA_QUERY, uri, constraints.getMinScore());
        com.hp.hpl.jena.query.Query query = QueryFactory.create(queryString);
        query.setLimit(DEFAULT_LIMIT);

        // Execute the query
        VirtGraph virtGraph = new VirtGraph(CONNECTION_STRING, USER, PASSWORD); // TODO: keep
        virtGraph.setSameAs(true);
        VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create(query, virtGraph);
        com.hp.hpl.jena.query.ResultSet queryResults = vqe.execSelect();

        // Prepare result
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();
        while (queryResults.hasNext()) {
            QuerySolution solution = queryResults.next();
            NamedGraphMetadata graphMetadata = new NamedGraphMetadata(solution.get("graph").toString());

            RDFNode source = solution.get("source");
            if (source != null) {
                graphMetadata.setDataSource(source.toString());
            }
            RDFNode score = solution.get("score");
            if (score != null) {
                //graphMetadata.setScore(score)
            }
            RDFNode insertedAt = solution.get("insertedAt");
            if (insertedAt != null) {
                //graphMetadata.setScore(score)
            }
            RDFNode publishedBy = solution.get("publishedBy");
            if (publishedBy != null) {
                //graphMetadata.setScore(score)
            }
            RDFNode publisherScore = solution.get("publisherScore");
            if (publisherScore != null) {
                //graphMetadata.setScore(score)
            }

            metadata.addMetadata(graphMetadata);
        }

        /*// Prepare the query
        String queryString = String.format(Locale.ROOT, METADATA_QUERY, uri, constraints.getMinScore());
        queryString = "SPARQL\n DEFINE input:same-as \"yes\" \n" + queryString + "\n LIMIT " + DEFAULT_LIMIT;

        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();
        try {
            // Execute the query
            Class.forName("virtuoso.jdbc3.Driver");
            Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD); // TODO: keep
            Statement statement = connection.createStatement();
            statement.execute(queryString);

            // Prepare result
            boolean more = true;
            while (more) {
                java.sql.ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    NamedGraphMetadata graphMetadata = new NamedGraphMetadata(resultSet.getString("graph").toString());

                    String source = resultSet.getString("source");
                    if (source != null) {
                        graphMetadata.setDataSource(source.toString());
                    }
                    double score = resultSet.getDouble("score");
                    if (!resultSet.wasNull()) {
                        graphMetadata.setScore(score);
                    }
                    Object insertedAt = resultSet.getObject("insertedAt");
                    if (insertedAt != null && insertedAt instanceof Timestamp) {
                        graphMetadata.setStored(new Date(((Timestamp) insertedAt).getTime()));
                    }
                    String publishedBy = resultSet.getString("publishedBy");
                    if (publishedBy != null) {
                        graphMetadata.setPublisher(publishedBy);
                    }
                    double publisherScore = resultSet.getDouble("publisherScore");
                    if (!resultSet.wasNull()) {
                        graphMetadata.setPublisherScore(publisherScore);
                    }

                    metadata.addMetadata(graphMetadata);
                }
                more = statement.getMoreResults();
            }
        } catch (ClassNotFoundException e) {
            // TODO
            throw new ODCleanStoreException(e);
        } catch (SQLException e) {
            // TODO
            throw new ODCleanStoreException(e);
        }
        */

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
