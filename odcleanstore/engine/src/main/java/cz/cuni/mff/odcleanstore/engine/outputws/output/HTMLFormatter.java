package cz.cuni.mff.odcleanstore.engine.outputws.output;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * Returns a representation of a query result in a user-friendly HTML document.
 * 
 * TODO: process labels?
 * 
 * @author Jan Michelfeit
 */
public class HTMLFormatter extends ResultFormatterBase {
	/** The actual representation of the result HTML document. */
	private class HTMLRepresentation extends WriterRepresentation {
		/** Query result. */
		private QueryResult queryResult;
		
		/** 
		 * Initialize.
		 * @param queryResult query result
		 */
		public HTMLRepresentation(QueryResult queryResult) {
			super(MediaType.TEXT_HTML);
			this.queryResult = queryResult;
		}
		
		@Override
		public void write(Writer writer) throws IOException {
			writeHeader(writer);
			writeResultQuads(writer);
			writeMetadata(writer);
			writerFooter(writer);
		}
		
		/**
		 * Write start of the HTML document.
		 * @param writer output writer
		 * @throws IOException if an I/O error occurs
		 */
		private void writeHeader(Writer writer) throws IOException {
			writer.write("<!DOCTYPE html>" 
					+ "\n<html lang=\"en\">"
					+ "\n<head>"
					+ "\n <meta charset=\"utf-8\" />"
					+ "\n <title>");
			writer.write(queryResult.getQueryType().toString());
			writer.write(" query</title>"
					+ "\n</head>"
					+ "\n<body>"
					+ "\n");
			writer.write(" <p>");
			if (queryResult.getQuery() != null) {
				switch (queryResult.getQueryType()) {
				case KEYWORD:
					writer.write("Keyword query for <code>");
					writer.write(queryResult.getQuery());
					writer.write("</code>.");
					break;
				case URI:
					writer.write("URI query for &lt;");
					writer.write(queryResult.getQuery());
					writer.write("&gt;.");
					break;
				default:
					writer.write("Query <code>");
					writer.write(queryResult.getQuery());
					writer.write("</code>.");
				}
			}
			if (queryResult.getExecutionTime() != null) {
				writer.write(" Query executed in ");
				writer.write(formatExecutionTime(queryResult.getExecutionTime()));
				writer.write('.');
			}
			writer.write("</p>\n");
		}

		/**
		 * Write table with result quads.
		 * @param writer output writer
		 * @throws IOException if an I/O error occurs
		 */
		private void writeResultQuads(Writer writer) throws IOException {
			writer.write(" <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
			writer.write("  <tr><th>Subject</th><th>Predicate</th><th>Object</th><th>Quality</th><th>Source named graphs</th></tr>\n");
			for (CRQuad crQuad : queryResult.getResultQuads()) {
				writer.write("  <tr><td>");
				writeNode(writer, crQuad.getQuad().getSubject());
				writer.write("</td><td>");
				writer.write(crQuad.getQuad().getPredicate().toString());
				writer.write("</td><td>");
				writeNode(writer, crQuad.getQuad().getObject());
				writer.write("</td><td>");
				writer.write(String.format("%.5f", crQuad.getQuality()));
				writer.write("</td><td>");
				boolean first = true;
				for (String sourceURI : crQuad.getSourceNamedGraphURIs()) {
					if (!first) {
						writer.write(", ");
					}
					first = false;
					writer.write(sourceURI);
				}
				writer.write("</td></tr>\n");
			}
			writer.write(" </table>\n");
		}

		/**
		 * Write table with metadata.
		 * @param writer output writer
		 * @throws IOException if an I/O error occurs
		 */
		private void writeMetadata(Writer writer) throws IOException {
			writer.write(" Source graphs:\n <table border=\"1\" cellspacing=\"0\" cellpadding=\"2\">\n");
			writer.write("  <tr><th>Named graph</th><th>Data source</th><th>Inserted at</th><th>Graph score</th><th>License</th></tr>");
			for (NamedGraphMetadata metadata : queryResult.getMetadata().listMetadata()) {
				writer.write("  <tr><td>");
				writer.write(metadata.getNamedGraphURI());
				writer.write("</td><td>");
				if (metadata.getSource() != null) {
					writer.write(metadata.getSource());
				}
				writer.write("</td><td>");
				if (metadata.getInsertedAt() != null) {
					writer.write(formatDate(metadata.getInsertedAt()));
				}
				writer.write("</td><td>");
				if (metadata.getScore() != null) {
					writer.write(metadata.getScore().toString());
				}
				writer.write("</td><td>");
				if (metadata.getLicence() != null) {
					writer.write(metadata.getLicence());
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
		private void writerFooter(Writer writer) throws IOException {
			writer.write("\n</body>\n</html>");
		}
		
		/**
		 * Write a single node.
		 * @param writer output writer
		 * @throws IOException if an I/O error occurs
		 */
		private void writeNode(Writer writer, Node node) throws IOException {
			if (node.isURI()) {
				writer.write("<a href=\"/");
				writer.write(Engine.OUTPUTWS_URI_PATH);
				writer.write("?find=");
				writer.write(URLEncoder.encode(node.getURI(), "UTF-8"));
				writer.write("&amp;aggregation=");
				writer.write(getAggregationType().name());
				writer.write("\">");
				writer.write(node.toString());
				writer.write("</a>");
			} else if (node.isLiteral()) {
				writer.write("<a href=\"/");
				writer.write(Engine.OUTPUTWS_KEYWORD_PATH);
				writer.write("?find=");
				writer.write(URLEncoder.encode(node.getLiteralLexicalForm(), "UTF-8"));
				writer.write("&amp;aggregation=");
				writer.write(getAggregationType().name());
				writer.write("\">");
				writer.write(node.toString());
				writer.write("</a>");
			} else {
				writer.write(node.toString());
			}
		}
		
		/**
		 * Returns the effective default aggregation type for the query.
		 * @return aggregation type
		 */
		private EnumAggregationType getAggregationType() {
			return queryResult.getAggregationSpec().getDefaultAggregation() == null
					? AggregationSpec.IMPLICIT_AGGREGATION
					: queryResult.getAggregationSpec().getDefaultAggregation();
		}
	}
	
	@Override
	public Representation format(QueryResult result, String requestURI) {
		WriterRepresentation representation = new HTMLRepresentation(result); 
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}
}
