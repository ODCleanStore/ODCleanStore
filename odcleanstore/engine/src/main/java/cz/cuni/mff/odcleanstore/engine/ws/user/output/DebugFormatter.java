package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.shared.NodeComparator;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Returns a representation of a query result serialized to the TriG format.
 * (See http://www4.wiwiss.fu-berlin.de/bizer/TriG/ .)
 * @author Jan Michelfeit
 */
public class DebugFormatter implements QueryResultFormatter {
	// Compares quads by graph, subject, property, object
	private static class QuadComparator implements Comparator<Quad> {
		 @Override
         public int compare(Quad quad1, Quad quad2) {
             int comparison = NodeComparator.compare(quad1.getGraphName(), quad2.getGraphName());
             if (comparison != 0) {
                 return comparison;
             }
             comparison = NodeComparator.compare(quad1.getSubject(), quad2.getSubject());
             if (comparison != 0) {
                 return comparison;
             }
             comparison = NodeComparator.compare(quad1.getPredicate(), quad2.getPredicate());
             if (comparison != 0) {
                 return comparison;
             }
             return NodeComparator.compare(quad1.getObject(), quad2.getObject());
         }
	}
	
	private final static Comparator<Quad> QUAD_COMPARATOR = new QuadComparator();
	
	@Override
	public Representation format(final NamedGraphSet result) {
		WriterRepresentation representation = new WriterRepresentation(MediaType.TEXT_PLAIN) {
			@SuppressWarnings("unchecked")
			@Override
			public void write(Writer writer) throws IOException {
		        ArrayList<Quad> resultQuads = new ArrayList<Quad>();
		        Iterator<Quad> quadIt = (Iterator<Quad>) result.findQuads(new Quad(Node.ANY, Node.ANY, Node.ANY, Node.ANY));
		        while (quadIt.hasNext()) {
		            resultQuads.add(quadIt.next());
		        }
		        // Sort quads by graph, subject, property, object
		        Collections.sort(resultQuads, QUAD_COMPARATOR);

		        // Print result
		        for (Quad quad : resultQuads) {
		        	writer.write(quad.toString());
		        	writer.write('\n');
		        }
		    }
		};
		representation.setCharacterSet(CharacterSet.UTF_8);
		return representation;
	}

}
