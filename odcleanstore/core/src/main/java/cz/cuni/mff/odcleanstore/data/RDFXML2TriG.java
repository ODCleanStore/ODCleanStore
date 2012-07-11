package cz.cuni.mff.odcleanstore.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class RDFXML2TriG {
	public InputStream transform (String inputFileName, String defaultGraphName) throws IOException {
		NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();

		GraphReaderService reader = new GraphReaderService();
		
		reader.setSourceInputStream(new FileInputStream(inputFileName), defaultGraphName);
		reader.setLanguage("RDF/XML");
		reader.readInto(namedGraphSet);
		
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);
		
		namedGraphSet.write(out, "TRIG", defaultGraphName);
		
		out.flush();
		out.close();
		
		return in;
	}
}
