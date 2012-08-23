package cz.cuni.mff.odcleanstore.engine.outputws.output;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Locale;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
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
		
		/** Representation of the requested URI */
		private Reference requestReference;
		
		/** 
		 * Initialize.
		 * @param queryResult query result
		 * @param requestReference representation of the requested URI
		 */
		public HTMLRepresentation(QueryResult queryResult, Reference requestReference) {
			super(MediaType.TEXT_HTML);
			this.queryResult = queryResult;
			this.requestReference = requestReference;
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
				writer.write(String.format(Locale.ROOT, "%.5f", crQuad.getQuality()));
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
				writer.append("<a href=\"/")
					.append(getRequestForURI(node.getURI()))
					.append("\">")
					.append(node.toString())
					.append("</a>");
			} else if (node.isLiteral()) {
				writer.append("<a href=\"/")
				.append(getRequestForKeyword(node.getLiteralLexicalForm()))
				.append("\">")
				.append(node.toString())
				.append("</a>");
			} else {
				writer.write(node.toString());
			}
		}
		
		/**
		 * Returns a URI of a URI query request with other settings same as for the current request 
		 * @param uri the requested URI
		 * @return URI of the query request
		 * @throws UnsupportedEncodingException exception
		 */
		private CharSequence getRequestForURI(String uri) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			result.append(Engine.OUTPUTWS_URI_PATH);
			result.append("?uri=");
			result.append(URLEncoder.encode(uri, "UTF-8"));
			result.append("&");
			result.append(requestReference.getQuery());
			return result.toString();
		}
		
		/**
		 * Returns a URI of a keyword query request with other settings same as for the current request 
		 * @param keyword the searched keyword
		 * @return URI of the keyword request
		 * @throws UnsupportedEncodingException exception
		 */
		private CharSequence getRequestForKeyword(String keyword) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			result.append(Engine.OUTPUTWS_KEYWORD_PATH);
			result.append("?kw=");
			result.append(URLEncoder.encode(keyword, "UTF-8"));
			result.append("&");
			result.append(requestReference.getQuery());
			return result.toString();
		}
	}
	
	@Override
	public Representation format(QueryResult result, Reference requestReference) {
		WriterRepresentation representation = new HTMLRepresentation(result, requestReference); 
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}
}
