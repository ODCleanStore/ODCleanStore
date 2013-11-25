package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResultBase;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * An abstract base class for formatters having a serialized form of RDF as their output.
 * @author Jan Michelfeit
 */
public abstract class RDFFormatter extends ResultFormatterBase {
    private static final Map<URI, URI> METADATA_PROPERTY_MAPPINGS;
    
    static {
        METADATA_PROPERTY_MAPPINGS = new HashMap<URI, URI>();
        METADATA_PROPERTY_MAPPINGS.put(ODCS.SOURCE, W3P.SOURCE);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.SCORE, ODCS.SCORE);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.INSERTED_AT, W3P.INSERTED_AT);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.PUBLISHED_BY, W3P.PUBLISHED_BY);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.PUBLISHER_SCORE, ODCS.PUBLISHER_SCORE);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.LICENSE, DCTERMS.LICENSE);
        METADATA_PROPERTY_MAPPINGS.put(ODCS.UPDATE_TAG, ODCS.UPDATE_TAG);
    }
    

    /** Configuration of the output webservice from the global configuration file. */
    protected final OutputWSConfig outputWSConfig;

    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     */
    public RDFFormatter(OutputWSConfig outputWSConfig) {
        this.outputWSConfig = outputWSConfig;
    }

    /**
     * Adds basic metadata about the query response to the given graph.
     * @param rdfWriter RDF writer where the metadata are written
     * @param requestURI URI of the current request
     * @param queryResult query results
     * @param destinationGraphURI URI of named graph where result triples are placed
     * @throws RDFHandlerException writer error
     */
    protected void writeBasicQueryMetadata(RDFHandler rdfWriter, URI requestURI, QueryResultBase queryResult,
            URI destinationGraphURI) throws RDFHandlerException {
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(requestURI, RDF.TYPE, ODCS.QUERY_RESPONSE));

        String title = formatQueryTitle(queryResult.getQuery(), queryResult.getQueryType());
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(requestURI, DCTERMS.TITLE, VALUE_FACTORY.createLiteral(title)));
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                requestURI,
                DCTERMS.DATE,
                VALUE_FACTORY.createLiteral(new Date())));
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                requestURI,
                ODCS.QUERY,
                VALUE_FACTORY.createLiteral(queryResult.getQuery())));
    }

    /**
     * Adds metadata from the metadata argument as triples to the given graph.
     * @param rdfWriter RDF writer where the metadata are written
     * @param metadata metadata to be added
     * @param addScore indicates whether add a triple about named graph score or not
     * @param destinationGraphURI named graph to which metadata triples will be written
     * @throws RDFHandlerException writer error
     */
    protected void writeODCSNamedGraphMetadata(RDFWriter rdfWriter, Model metadata,
            boolean addScore, URI destinationGraphURI) throws RDFHandlerException {
        
        for (Statement statement : metadata) {
            URI predicate = METADATA_PROPERTY_MAPPINGS.get(statement.getPredicate());
            if (predicate == null) {
                continue;
            }
            
            rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                    statement.getSubject(),
                    predicate,
                    statement.getObject(),
                    destinationGraphURI));
        }
    }

    /**
     * Adds quality assessment results as triples to graph.
     * @param rdfWriter RDF writer where the metadata are written
     * @param namedGraphURI URI on which QA was executed
     * @param qaResult results of quality assessment
     * @param destinationGraphURI URI of named graph where result triples are placed
     * @throws RDFHandlerException writer error
     */
    protected void writeQualityAssessmentResults(RDFWriter rdfWriter, URI namedGraphURI, GraphScoreWithTrace qaResult,
            URI destinationGraphURI) throws RDFHandlerException {
        if (qaResult != null) {
            rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                    namedGraphURI,
                    ODCS.SCORE,
                    VALUE_FACTORY.createLiteral((double) qaResult.getScore())));
            for (QualityAssessmentRule qaRule : qaResult.getTrace()) {
                URI ruleNode = VALUE_FACTORY.createURI(outputWSConfig.getResultDataURIPrefix().toString()
                        + ODCSInternal.QUERY_QA_RULE_URI_INFIX + qaRule.getId().toString());
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(namedGraphURI, ODCS.VIOLATED_QA_RULE, ruleNode));

                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        RDF.TYPE,
                        ODCS.QA_RULE));
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        DCTERMS.DESCRIPTION,
                        VALUE_FACTORY.createLiteral(qaRule.getDescription())));
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        ODCS.COEFFICIENT,
                        VALUE_FACTORY.createLiteral((double) qaRule.getCoefficient())));
            }
        }
    }
}
