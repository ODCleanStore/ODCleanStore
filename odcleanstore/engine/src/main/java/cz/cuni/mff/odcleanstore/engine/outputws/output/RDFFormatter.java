package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResultBase;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.vocabulary.RDF;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
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
    /** {@link ODCS#totalResults} as a {@link URI}. */
    protected static final URI TOTAL_RESULTS_PROPERTY = VALUE_FACTORY.createURI(ODCS.totalResults);
    /** {@link RDF#type} as a {@link URI}. */
    protected static final URI TYPE_PROPERTY = VALUE_FACTORY.createURI(RDF.type);
    /** {@link DC#title} as a {@link URI}. */
    protected static final URI TITLE_PROPERTY = VALUE_FACTORY.createURI(DC.title);
    /** {@link DC#date} as a {@link URI}. */
    protected static final URI DATE_PROPERTY = VALUE_FACTORY.createURI(DC.date);
    /** {@link ODCS#query} as a {@link URI}. */
    protected static final URI QUERY_PROPERTY = VALUE_FACTORY.createURI(ODCS.query);
    /** {@link ODCS#score} as a {@link URI}. */
    protected static final URI SCORE_PROPERTY = VALUE_FACTORY.createURI(ODCS.score);
    /** {@link ODCS#publisherScore} as a {@link URI}. */
    protected static final URI PUBLISHER_SCORE_PROPERTY = VALUE_FACTORY.createURI(ODCS.publisherScore);
    /** {@link W3P#source} as a {@link URI}. */
    protected static final URI SOURCE_PROPERTY = VALUE_FACTORY.createURI(W3P.source);
    /** {@link W3P#insertedAt} as a {@link URI}. */
    protected static final URI INSERTED_AT_PROPERTY = VALUE_FACTORY.createURI(W3P.insertedAt);
    /** {@link W3P#publishedBy} as a {@link URI}. */
    protected static final URI PUBLISHED_BY_PROPERTY = VALUE_FACTORY.createURI(W3P.publishedBy);
    /** {@link ODCS#updateTag} as a {@link URI}. */
    protected static final URI UPDATE_TAG_PROPERTY = VALUE_FACTORY.createURI(ODCS.updateTag);
    /** {@link DC#license} as a {@link URI}. */
    protected static final URI LICENSE_PROPERTY = VALUE_FACTORY.createURI(DC.license);
    /** {@link DC#description} as a {@link URI}. */
    protected static final URI DESCRIPTION_PROPERTY = VALUE_FACTORY.createURI(DC.description);
    /** {@link ODCS#violatedQARule} as a {@link URI}. */
    protected static final URI VIOLATED_QA_RULE_PROPERTY = VALUE_FACTORY.createURI(ODCS.violatedQARule);
    /** {@link ODCS#coefficient} as a {@link URI}. */
    protected static final URI COEFFICIENT_PROPERTY = VALUE_FACTORY.createURI(ODCS.coefficient);
    /** {@link ODCS#QARule} as a {@link URI}. */
    protected static final URI QARULE_CLASS = VALUE_FACTORY.createURI(ODCS.QARule);
    /** {@link ODCS#queryResponse} as a {@link URI}. */
    protected static final URI QUERY_RESPONSE_CLASS = VALUE_FACTORY.createURI(ODCS.queryResponse);
    
    private static final Map<URI, URI> METADATA_PROPERTY_MAPPINGS;
    
    static {
        METADATA_PROPERTY_MAPPINGS = new HashMap<URI, URI>();
        METADATA_PROPERTY_MAPPINGS.put(METADATA_SOURCE_PROPERTY, SOURCE_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_SCORE_PROPERTY, SCORE_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_INSERTED_AT_PROPERTY, INSERTED_AT_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_PUBLISHED_BY_PROPERTY, PUBLISHED_BY_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_PUBLISHER_SCORE_PROPERTY, PUBLISHER_SCORE_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_LICENCES_PROPERTY, LICENSE_PROPERTY);
        METADATA_PROPERTY_MAPPINGS.put(METADATA_UPDATE_TAG_PROPERTY, UPDATE_TAG_PROPERTY);
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
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(requestURI, TYPE_PROPERTY, QUERY_RESPONSE_CLASS));

        String title = formatQueryTitle(queryResult.getQuery(), queryResult.getQueryType());
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(requestURI, TITLE_PROPERTY, VALUE_FACTORY.createLiteral(title)));
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                requestURI,
                DATE_PROPERTY,
                VALUE_FACTORY.createLiteral(new Date())));
        rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                requestURI,
                QUERY_PROPERTY,
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
                    SCORE_PROPERTY,
                    VALUE_FACTORY.createLiteral((double) qaResult.getScore())));
            for (QualityAssessmentRule qaRule : qaResult.getTrace()) {
                URI ruleNode = VALUE_FACTORY.createURI(outputWSConfig.getResultDataURIPrefix().toString()
                        + ODCSInternal.queryQARuleUriInfix + qaRule.getId().toString());
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(namedGraphURI, VIOLATED_QA_RULE_PROPERTY, ruleNode));

                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        TYPE_PROPERTY,
                        QARULE_CLASS));
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        DESCRIPTION_PROPERTY,
                        VALUE_FACTORY.createLiteral(qaRule.getDescription())));
                rdfWriter.handleStatement(VALUE_FACTORY.createStatement(
                        ruleNode,
                        COEFFICIENT_PROPERTY,
                        VALUE_FACTORY.createLiteral((double) qaRule.getCoefficient())));
            }
        }
    }
}
