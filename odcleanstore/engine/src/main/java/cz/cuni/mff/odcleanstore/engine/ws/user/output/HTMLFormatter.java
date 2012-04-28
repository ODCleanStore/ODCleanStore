package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.NamedGraphMetadataReader;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 *
 * @author Jan Michelfeit
 */
public class HTMLFormatter implements QueryResultFormatter {
	private static final String HTML_HEADER = 
			"<!DOCTYPE html>" 
			+ "\n<html lang=\"en\">"
			+ "\n<head>"
			+ "\n <meta charset=\"utf-8\" />"
			+ "\n <title>TODO</title>"
			+ "\n</head>"
			+ "\n<body>"
			+ "\n";
	
	private static final String HTML_FOOTER = "\n</body>\n</html>";
	
	@Override
	public Representation format(final NamedGraphSet result) {
		WriterRepresentation representation = new WriterRepresentation(MediaType.TEXT_HTML) {
			@SuppressWarnings("unchecked")
			@Override
			public void write(Writer writer) throws IOException {
				writer.write(HTML_HEADER);
				
				writer.write(" <table border=\"1\">\n");
				writer.write("  <tr><th>Subject</th><th>Predicate</th><th>Object</th><th>Quality</th><th>Source named graphs</th></tr>\n");
				Iterator<Quad> quadIt = (Iterator<Quad>) result.findQuads(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
				while (quadIt.hasNext()) {
					Quad quad = quadIt.next();
					if (quad.getGraphName().getURI().equals(QueryExecution.METADATA_GRAPH)) {
						continue;
					}
					writer.write("  <tr><td>");
					writer.write(quad.getSubject().toString());
					writer.write("</td><td>");
					writer.write(quad.getPredicate().toString());
					writer.write("</td><td>");
					writer.write(quad.getObject().toString());
					writer.write("</td><td>");
					Iterator<Quad> qualityIt = (Iterator<Quad>) result.findQuads(Node.ANY, quad.getGraphName(), Node.createURI(ODCS.quality), Node.ANY); // TODO
					if (qualityIt.hasNext()) {
						writer.write(qualityIt.next().getObject().getLiteralLexicalForm());
					}
					writer.write("</td><td>");
					Iterator<Quad> sourceIt = (Iterator<Quad>) result.findQuads(Node.ANY, quad.getGraphName(), Node.createURI(W3P.source), Node.ANY);
					boolean first = true;
					while (sourceIt.hasNext()) {
						if (!first) {
							writer.write(", ");
						}
						first = false;
						writer.write(sourceIt.next().getObject().toString());
					}
					writer.write("</td></tr>\n");
				}
				writer.write(" </table>\n");
				
				NamedGraphMetadataMap metadataMap = null;
				try {
					metadataMap = NamedGraphMetadataReader.readFromRDF((Iterator<Quad>) result.findQuads(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
				} catch (ODCleanStoreException e) {
					throw new IOException(e);
				}
				writer.write(" Source graphs:\n <table border=\"1\">\n");
				writer.write("  <tr><th>Named graph</th><th>Data source</th><th>Inserted at</th><th>Graph score</th></tr>");
				for (NamedGraphMetadata metadata : metadataMap.listMetadata()) {
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
				
				writer.write(HTML_FOOTER);
			};
		};
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}

}
