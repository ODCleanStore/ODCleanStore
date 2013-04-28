package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.engine.outputws.QueryExecutorResourceBase;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResultBase;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMapping;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Returns a representation of a query result in a user-friendly HTML document.
 * 
 * TODO: process labels?
 * 
 * @author Jan Michelfeit
 */
public class HTMLFormatter extends ResultFormatterBase {
    
    private static final String ENCODING = ODCSUtils.DEFAULT_ENCODING;

    private static final String[] PROPAGATED_QUERY_PARAMS = {
            QueryExecutorResourceBase.DEFAULT_AGGREGATION_PARAM,
            QueryExecutorResourceBase.DEFAULT_MULTIVALUE_PARAM,
            QueryExecutorResourceBase.ERROR_STRATEGY_PARAM,
            QueryExecutorResourceBase.FORMAT_PARAM,
            QueryExecutorResourceBase.PROPERTY_AGGREGATION_PARAM,
            QueryExecutorResourceBase.PROPERTY_MULTIVALUE_PARAM
    };
    
    /** Configuration of the output webservice from the global configuration file. */
    private final OutputWSConfig outputWSConfig;
    
    /** Namespace prefix mappings. */
    private final PrefixMapping prefixMapping;

    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     * @param prefixMapping namespace prefix mappings
     */
    public HTMLFormatter(OutputWSConfig outputWSConfig, PrefixMapping prefixMapping) {
        this.outputWSConfig = outputWSConfig;
        this.prefixMapping = prefixMapping;
    }

    @Override
    public Representation format(BasicQueryResult result, Reference requestReference) {
        WriterRepresentation representation = new BasicQueryHTMLRepresentation(result, requestReference);
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    @Override
    public Representation format(MetadataQueryResult metadataResult, GraphScoreWithTrace qaResult,
            long totalTime, Reference requestReference) {

        WriterRepresentation representation = new MetadataQueryHTMLRepresentation(
                metadataResult, qaResult, totalTime, requestReference);
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    /** The actual representation of the result HTML document. */
    private abstract class HTMLRepresentationBase extends WriterRepresentation {
        /** Representation of the requested URI. */
        //private Reference requestReference;
        
        /** Query result. */
        private final QueryResultBase queryResult;
        
        /** Query string with aggregation properties to append to links. */
        private final String propagatedQueryString;

        /**
         * Initialize.
         * @param queryResult query result
         * @param requestReference representation of the requested URI
         */
        public HTMLRepresentationBase(QueryResultBase queryResult, Reference requestReference) {
            super(MediaType.TEXT_HTML);
            //this.requestReference = requestReference;
            this.queryResult = queryResult;
            
            this.propagatedQueryString = buildPropagatedQueryString(requestReference);
        }

        /**
         * Builds query string with aggregation properties.
         * @param requestReference representation of the requested URI
         * @return query string part
         */
        private String buildPropagatedQueryString(Reference requestReference) {
            Iterator<Parameter> it = requestReference.getQueryAsForm().iterator();
            Map<String, String> queryParams = new HashMap<String, String>();
            StringBuilder result = new StringBuilder();

            while (it.hasNext()) {
                Parameter param = it.next();
                String name = param.getName();
                for (int i = 0; i < PROPAGATED_QUERY_PARAMS.length; i++) {
                    if (name.startsWith(PROPAGATED_QUERY_PARAMS[i])) {
                        queryParams.put(name, param.getValue());
                    }
                }
            }

            try {
                for (Entry<String, String> entry : queryParams.entrySet()) {
                    result.append(URLEncoder.encode(entry.getKey(), ENCODING));
                    result.append('=');
                    result.append(URLEncoder.encode(entry.getValue(), ENCODING));
                    result.append('&');
                }
            } catch (UnsupportedEncodingException e) {
                // do nothing
            }
            
            return result.toString();
        }

        @Override
        public abstract void write(Writer writer) throws IOException;

        /**
         * Write start of the HTML document.
         * @param writer output writer
         * @param queryResult query result
         * @param executionTime execution time of the query
         * @throws IOException if an I/O error occurs
         */
        protected void writeHeader(Writer writer, QueryResultBase queryResult, Long executionTime) throws IOException {
            writer.write("<!DOCTYPE html>"
                    + "\n<html lang=\"en\">" 
                    + "\n<head>"
                    + "\n <meta charset=\"" + ENCODING + "\" />"
                    + "\n <style type=\"text/css\">" 
                    + "\n   body {font-family: Verdana,Sans-Serif,Arial; font-size:13px;}"
                    + "\n   th, td, table {border: 1px solid lightgray;}"
                    + "\n   td, th {border-left-width: 0px; border-top-width: 0px}"
                    + "\n   th {background-color: #49B7E0; color:white; padding: 5px 2px;}"
                    + "\n   td {background-color: #F2F4F5; padding: 5px 2px; }"
                    + "\n   tr.odd td {background-color: #FFFFFF; }"
                    + "\n   a { text-decoration:none; }"
                    + "\n   a:hover {text-decoration:underline; }"
                    + "\n </style>");
            if (queryResult.getQuery() != null) {
                switch (queryResult.getQueryType()) {
                case KEYWORD:
                    writer.write("\n<title>Keyword query</title>");
                    writer.write("\n</head>\n<body>\n <p>");
                    writer.write("Keyword query for <code>");
                    writer.write(ODCSUtils.toStringNullProof(queryResult.getQuery()));
                    writer.write("</code>.");
                    break;
                case URI:
                    writer.write("\n<title>URI query</title>");
                    writer.write("\n</head>\n<body>\n <p>");
                    writer.write("URI query for &lt;");
                    writer.write(ODCSUtils.toStringNullProof(queryResult.getQuery()));
                    writer.write("&gt;.");
                    break;
                case METADATA:
                    writer.write("\n<title>Metadata query</title>");
                    writer.write("\n</head>\n<body>\n <p>");
                    writer.write("Metadata query for named graph &lt;");
                    writer.write(ODCSUtils.toStringNullProof(queryResult.getQuery()));
                    writer.write("&gt;.");
                    break;
                case NAMED_GRAPH:
                    writer.write("\n<title>Named graph query</title>");
                    writer.write("\n</head>\n<body>\n <p>");
                    writer.write("Named graph query for &lt;");
                    writer.write(ODCSUtils.toStringNullProof(queryResult.getQuery()));
                    writer.write("&gt;.");
                    break;
                default:
                    writer.write("\n<title>Query</title>");
                    writer.write("\n</head>\n<body>\n <p>");
                    writer.write("Query <code>");
                    writer.write(ODCSUtils.toStringNullProof(queryResult.getQuery()));
                    writer.write("</code>.");
                }
            }
            if (executionTime != null) {
                writer.write(" Query executed in ");
                writer.write(formatExecutionTime(executionTime));
                writer.write('.');
            }
            writer.write("</p>\n");
        }

        /**
         * Write table with metadata.
         * @param writer output writer
         * @param metadataMap metadata for graphs in the result
         * @throws IOException if an I/O error occurs
         */
        protected void writeMetadata(Writer writer, Model metadata) throws IOException {
            writer.write(" <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
            writer.write("  <tr><th>Named graph</th><th>Data source</th><th>Inserted at</th>"
                    + "<th>Graph score</th><th>License</th><th>Update tag</th></tr>");
            int row = 0;
            for (Resource namedGraph : metadata.subjects()) {
                writeOpeningTr(writer, ++row);
                writer.write("<td>");
                writeRelativeLink(
                        writer, 
                        getRequestForMetadata(namedGraph.stringValue()),
                        getPrefixedURI(namedGraph.stringValue()),
                        "Metadata query");
                writer.write("</td><td>");
                Model sources = metadata.filter(namedGraph, METADATA_SOURCE_PROPERTY, null);
                if (!sources.isEmpty()) {
                    boolean isFirst = true;
                    for (Statement statement : sources) {
                        if (!isFirst) {
                            writer.write(", ");
                        }
                        String source = statement.getObject().stringValue();
                        writeAbsoluteLink(writer, source, source);
                        isFirst = false;
                    }
                }
                writer.write("</td><td>");
                Model insertedAt = metadata.filter(namedGraph, METADATA_INSERTED_AT_PROPERTY, null);
                if (!insertedAt.isEmpty()) {
                    writer.write(formatDate(insertedAt.iterator().next().getObject()));
                }
                writer.write("</td><td>");
                Model score = metadata.filter(namedGraph, METADATA_SCORE_PROPERTY, null);
                if (!score.isEmpty()) {
                    writer.write(formatScore(score.iterator().next().getObject()));
                }
                writer.write("</td><td>");
                Model licences = metadata.filter(namedGraph, METADATA_LICENCES_PROPERTY, null);
                if (!licences.isEmpty()) {
                    boolean isFirst = true;
                    for (Statement statement : licences) {
                        if (!isFirst) {
                            writer.write(", ");
                        }
                        writer.write(statement.getObject().stringValue());
                        isFirst = false;
                    }
                }
                writer.write("</td><td>");
                Model updateTag = metadata.filter(namedGraph, METADATA_UPDATE_TAG_PROPERTY, null);
                if (!updateTag.isEmpty()) {
                    writer.write(updateTag.iterator().next().getObject().stringValue());
                }
                writer.write("</td></tr>\n");
            }
            writer.write(" </table>\n");
        }

        /**
         * Write end of the HTML document.
         * @param writer output writer
         * @throws IOException if an I/O error occurs
         */
        protected void writerFooter(Writer writer) throws IOException {
            writer.write("\n</body>\n</html>");
        }

        /**
         * Write a single node.
         * @param writer output writer
         * @param value RDF node
         * @throws IOException if an I/O error occurs
         */
        protected void writeNode(Writer writer, Value value) throws IOException {
            if (value instanceof URI) {
                String text = getPrefixedURI(value.stringValue());
                assert queryResult.getQuery() != null;
                if (queryResult.getQuery().equals(text) || queryResult.getQuery().equals(value.stringValue())) {
                    writer.write("<em>");
                    writeRelativeLink(writer, getRequestForURI(value.stringValue()), text, "URI query");
                    writer.write("</em>");
                } else {
                    writeRelativeLink(writer, getRequestForURI(value.stringValue()), text, "URI query");
                }
            } else if (value instanceof Literal) {
                String text = formatLiteral((Literal) value);
                assert queryResult.getQuery() != null;
                if (queryResult.getQuery().equals(value.stringValue())) {
                    writer.write("<em>");
                    writeRelativeLink(writer, getRequestForKeyword(value.stringValue()), text, "Keyword query");
                    writer.write("</em>");                    
                } else {
                    writeRelativeLink(writer, getRequestForKeyword(value.stringValue()), text, "Keyword query");
                }
            } else if (value instanceof BNode) {
                String uri = ODCSUtils.getVirtuosoURIForBlankNode((BNode) value);
                assert queryResult.getQuery() != null;
                if (queryResult.getQuery().equals(uri)) {
                    writer.write("<em>");
                    writeRelativeLink(writer, getRequestForURI(uri), "_:" + value.stringValue(), "URI query");
                    writer.write("</em>");
                } else {
                    writeRelativeLink(writer, getRequestForURI(uri), "_:" + value.stringValue(), "URI query");
                }
                
            } else {
                writer.write(ODCSUtils.toStringNullProof(value));
            }
        }
        
        /**
         * Return uri with namespace shortened to prefix if possible.
         * @param uri uri to format
         * @return uri with namespace shortened to prefix if possible
         */
        protected String getPrefixedURI(String uri) {
            if (ODCSUtils.isNullOrEmpty(uri)) {
                return uri;
            }
            int namespacePartLength = Math.max(uri.lastIndexOf('/'), uri.lastIndexOf('#')) + 1; // use a simple heuristic
            String prefix = 0 < namespacePartLength && namespacePartLength < uri.length()
                    ? prefixMapping.getPrefix(uri.substring(0, namespacePartLength))
                    : null;
            return (prefix == null)
                    ? uri
                    : prefix + ":" + uri.substring(namespacePartLength);
        }
        
        /**
         * Format a literal value for output.
         * @param literalValue a literal node (literalNode.isLiteral() must return true!)
         * @return literal value formatted for output
         */
        protected String formatLiteral(Literal literalValue) {
            StringBuilder result = new StringBuilder();
            String lang = literalValue.getLanguage();
            URI dtype = literalValue.getDatatype();
            
            result.append('"');
            result.append(literalValue.getLabel());
            result.append('"');
            if (!ODCSUtils.isNullOrEmpty(lang)) {
                result.append("@").append(lang);
            }
            if (dtype != null) {
                result.append(" ^^").append(getPrefixedURI(dtype.stringValue()));
            }
            return result.toString();
        }
        /**
         * Write a relative hyperlink.
         * @param writer output writer
         * @param uri URI of the hyperlink
         * @param text text of the hyperlink
         * @param title title of the hyperlink
         * @throws IOException if an I/O error occurs
         */
        protected void writeRelativeLink(Writer writer, CharSequence uri, String text, String title) throws IOException {
            writer.append("<a title=\"")
                    .append(title)
                    .append("\" href=\"/")
                    .append(escapeHTML(uri))
                    .append("\">")
                    .append(text)
                    .append("</a>");
        }

        /**
         * Write an absolute hyperlink.
         * @param writer output writer
         * @param uri URI of the hyperlink
         * @param text text of the hyperlink
         * @throws IOException if an I/O error occurs
         */
        protected void writeAbsoluteLink(Writer writer, CharSequence uri, String text) throws IOException {
            writer.append("<a href=\"")
                    .append(escapeHTML(uri))
                    .append("\">")
                    .append(text)
                    .append("</a>");
        }

        /**
         * Returns a URI of a URI query request with other settings same as for the current request.
         * @param uri the requested URI
         * @return URI of the query request
         * @throws UnsupportedEncodingException exception
         */
        protected CharSequence getRequestForURI(String uri) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            result.append(outputWSConfig.getUriPath());
            result.append("?uri=");
            result.append(URLEncoder.encode(uri, ENCODING));
            result.append("&");
            result.append(propagatedQueryString);
            return result.toString();
        }

        /**
         * Returns a URI of a keyword query request with other settings same as for the current request.
         * @param keyword the searched keyword
         * @return URI of the keyword request
         * @throws UnsupportedEncodingException exception
         */
        protected CharSequence getRequestForKeyword(String keyword) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            result.append(outputWSConfig.getKeywordPath());
            result.append("?kw=");
            result.append(URLEncoder.encode(keyword, ENCODING));
            result.append("&");
            result.append(propagatedQueryString);
            return result.toString();
        }

        /**
         * Returns a URI of a metadata query request.
         * @param namedGraphURI the requested named graph
         * @return URI of the metadata request
         * @throws UnsupportedEncodingException exception
         */
        protected CharSequence getRequestForMetadata(String namedGraphURI) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            result.append(outputWSConfig.getMetadataPath());
            result.append("?uri=");
            result.append(URLEncoder.encode(namedGraphURI, ENCODING));
            result.append("&format=HTML");
            return result.toString();
        }
        
        /**
         * Returns a URI of a named graph query request.
         * @param namedGraphURI the requested named graph
         * @return URI of the named graph request
         * @throws UnsupportedEncodingException exception
         */
        protected CharSequence getRequestForNamedGraph(String namedGraphURI) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            result.append(outputWSConfig.getNamedGraphPath());
            result.append("?uri=");
            result.append(URLEncoder.encode(namedGraphURI, ENCODING));
            result.append("&format=HTML");
            return result.toString();
        }

        /**
         * Return a text escaped for use in HTML.
         * @param text text to escape
         * @return escaped text
         */
        protected String escapeHTML(CharSequence text) {
            return text.toString()
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;")
                    .replace("/", "&#x2F;");
        }
        
        /**
         * Write an opening &lt;tr&gt; tag with the correct background color.
         * @param writer writer
         * @param rowIndex row index
         * @throws IOException exception
         */
        protected void writeOpeningTr(Writer writer, int rowIndex) throws IOException {
            writer.write("  <tr");
            if (rowIndex % 2 != 0) {
                writer.write(" class=\"odd\"");
            }
            writer.write(">");
        }
    }

    /**
     * Response representation for basic (URI/KW) query.
     */
    private class BasicQueryHTMLRepresentation extends HTMLRepresentationBase {
        /** Query result. */
        private final BasicQueryResult queryResult;

        /**
         * Initialize.
         * @param queryResult query result
         * @param requestReference representation of the requested URI
         */
        public BasicQueryHTMLRepresentation(BasicQueryResult queryResult, Reference requestReference) {
            super(queryResult, requestReference);
            this.queryResult = queryResult;
        }

        @Override
        public void write(Writer writer) throws IOException {
            writeHeader(writer, queryResult, queryResult.getExecutionTime());
            writeResultQuads(writer);
            writer.write("\n <br />Source graphs:\n");
            writeMetadata(writer, queryResult.getMetadata());
            writerFooter(writer);
        }

        /**
         * Write table with result quads.
         * @param writer output writer
         * @throws IOException if an I/O error occurs
         */
        private void writeResultQuads(Writer writer) throws IOException {
            writer.write(" <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
            writer.write("  <tr><th>Subject</th><th>Predicate</th><th>Object</th>"
                    + "<th>Quality</th><th>Source named graphs</th></tr>\n");
            int row = 0;
            for (ResolvedStatement resolvedStatement : queryResult.getResultQuads()) {
                writeOpeningTr(writer, ++row);
                writer.write("<td>");
                writeNode(writer, resolvedStatement.getStatement().getSubject());
                writer.write("</td><td>");
                writer.write(getPrefixedURI(resolvedStatement.getStatement().getPredicate().toString()));
                writer.write("</td><td>");
                writeNode(writer, resolvedStatement.getStatement().getObject());
                writer.write("</td><td>");
                writer.write(String.format(Locale.ROOT, "%.5f", resolvedStatement.getConfidence()));
                writer.write("</td><td>");
                boolean first = true;
                for (Resource sourceURI : resolvedStatement.getSourceGraphNames()) {
                    if (!first) {
                        writer.write(", ");
                    }
                    first = false;
                    writeRelativeLink(writer, getRequestForNamedGraph(sourceURI.stringValue()), 
                            getPrefixedURI(sourceURI.stringValue()), "Named graph query");
                }
                writer.write("</td></tr>\n");
            }
            writer.write(" </table>\n");
        }
    }

    /**
     * Response representation for metadata query.
     */
    private class MetadataQueryHTMLRepresentation extends HTMLRepresentationBase {
        /** Result of metadata query about the requested named graph. */
        private final MetadataQueryResult metadataResult;

        /** Result of quality assessment over the given named graph. Can be null. */
        private final GraphScoreWithTrace qaResult;

        /** Execution time of the query. */
        private final long totalTime;

        /**
         * Initialize.
         * @param metadataResult result of metadata query about the requested named graph
         * @param qaResult result of quality assessment over the given named graph; can be null
         * @param totalTime execution time of the query
         * @param requestReference representation of the requested URI
         */
        public MetadataQueryHTMLRepresentation(MetadataQueryResult metadataResult,
                GraphScoreWithTrace qaResult, long totalTime, Reference requestReference) {

            super(metadataResult, requestReference);
            this.metadataResult = metadataResult;
            this.qaResult = qaResult;
            this.totalTime = totalTime;
        }

        @Override
        public void write(Writer writer) throws IOException {
            // Header
            writeHeader(writer, metadataResult, totalTime);

            // Basic metadata
            writer.write("\n Basic metadata:\n");
            writeMetadata(writer, metadataResult.getMetadata());

            // QA results
            if (qaResult != null) {
                writer.write("\n <br />Total Quality Assessment score: ");
                writer.write(String.format(Locale.ROOT, "%.5f", qaResult.getScore()));
                writer.write("\n <br />Quality Assessment rule violations:\n");
                writeQARules(writer, qaResult.getTrace());
            }

            // Additional provenance metadata
            if (!metadataResult.getProvenanceMetadata().isEmpty()) {
                writer.write("\n <br />Additional provenance metadata:\n");
                writeTriples(writer, metadataResult.getProvenanceMetadata());
            }

            // Footer
            writerFooter(writer);
        }

        /**
         * Write table with quads converted to triples.
         * @param writer output writer
         * @param quads quads to write
         * @throws IOException if an I/O error occurs
         */
        private void writeTriples(Writer writer, Collection<Statement> quads) throws IOException {
            writer.write(" <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
            writer.write("  <tr><th>Subject</th><th>Predicate</th><th>Object</th></tr>\n");
            int row = 0;
            for (Statement quad : quads) {
                writeOpeningTr(writer, ++row);
                writer.write("<td>");
                writeNode(writer, quad.getSubject());
                writer.write("</td><td>");
                writer.write(getPrefixedURI(quad.getPredicate().toString()));
                writer.write("</td><td>");
                writeNode(writer, quad.getObject());
                writer.write("</td></tr>\n");
            }
            writer.write(" </table>\n");
        }

        /**
         * Write table with violated QA rules.
         * @param writer output writer
         * @param qaRules violated QA rules
         * @throws IOException if an I/O error occurs
         */
        private void writeQARules(Writer writer, Collection<QualityAssessmentRule> qaRules) throws IOException {
            writer.write(" <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
            writer.write("  <tr><th>Rule description</th><th>Score decreased by</th></tr>\n");
            int row = 0;
            for (QualityAssessmentRule rule : qaRules) {
                writeOpeningTr(writer, ++row);
                writer.write("<td>");
                writer.write(ODCSUtils.toStringNullProof(rule.getLabel()));
                writer.write("</td><td>");
                if (rule.getCoefficient() != null) {
                    writer.write(String.format(Locale.ROOT, "%.5f", rule.getCoefficient()));
                }
                writer.write("</td></tr>\n");
            }
            writer.write(" </table>\n");
        }
    }
}
