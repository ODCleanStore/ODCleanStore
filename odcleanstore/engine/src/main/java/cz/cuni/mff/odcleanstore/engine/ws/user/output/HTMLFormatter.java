package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * Returns a representation of a query result in a user-friendly HTML document.
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
			if (queryResult.getExecutionTime() != null) {
				writer.write(" <p>Query executed in ");
				writer.write(formatExecutionTime(queryResult.getExecutionTime()));
				writer.write(".</p>\n");
			}
		}

		/**
		 * Write table with result quads.
		 * @param writer output writer
		 * @throws IOException if an I/O error occurs
		 */
		private void writeResultQuads(Writer writer) throws IOException {
			writer.write(" <table border=\"1\">\n");
			writer.write("  <tr><th>Subject</th><th>Predicate</th><th>Object</th><th>Quality</th><th>Source named graphs</th></tr>\n");
			for (CRQuad crQuad : queryResult.getResultQuads()) {
				writer.write("  <tr><td>");
				writer.write(crQuad.getQuad().getSubject().toString());
				writer.write("</td><td>");
				writer.write(crQuad.getQuad().getPredicate().toString());
				writer.write("</td><td>");
				writer.write(crQuad.getQuad().getObject().toString());
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
			writer.write(" Source graphs:\n <table border=\"1\">\n");
			writer.write("  <tr><th>Named graph</th><th>Data source</th><th>Inserted at</th><th>Graph score</th></tr>");
			for (NamedGraphMetadata metadata : queryResult.getMetadata().listMetadata()) {
				writer.write("  <tr><td>");
				writer.write(metadata.getNamedGraphURI());
				writer.write("</td><td>");
				if (metadata.getDataSource() != null) {
					writer.write(metadata.getDataSource());
				}
				writer.write("</td><td>");
				if (metadata.getStored() != null) {
					writer.write(metadata.getStored().toString());
				}
				writer.write("</td><td>");
				if (metadata.getScore() != null) {
					writer.write(metadata.getScore().toString());
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
		
	}
	
	@Override
	public Representation format(QueryResult result) {
		WriterRepresentation representation = new HTMLRepresentation(result); 
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}
}
