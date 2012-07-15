package cz.cuni.mff.odcleanstore.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class EnforceTriG {
	public InputStream transform (InputStream input, String defaultGraphName) throws IOException {
		NamedGraphSetImpl namedGraphSet = new NamedGraphSetImpl();

		GraphReaderService reader = new GraphReaderService();
		
		/**
		 * Buffer the file so that it can be parsed multiple times
		 */
		int totalLength = 0;
		int totalAllocated = 1;
		int readLength;
		
		byte[] buffer = new byte[totalAllocated];
		
		while ((readLength = input.read(buffer, totalLength, totalAllocated - totalLength)) >= 0) {
			totalLength += readLength;
			
			if (readLength + totalLength == totalAllocated) {
				byte[] newBuffer = new byte[totalAllocated * 2];
			
				for (int i = 0; i < totalLength; ++i) {
					newBuffer[i] = buffer[i];
				}
				
				buffer = newBuffer;
				
				totalAllocated *= 2;
			}
		}
		
		ByteArrayInputStream buffered = new ByteArrayInputStream(buffer, 0, totalLength);
		
		reader.setSourceInputStream(buffered, defaultGraphName);
		
		try {
			reader.setLanguage("TRIG");
			reader.readInto(namedGraphSet);
		} catch (Exception e) {
			buffered.reset();

			reader.setLanguage("RDF/XML");
			reader.readInto(namedGraphSet);
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		namedGraphSet.write(out, "TRIG", defaultGraphName);
		
		return new ByteArrayInputStream(out.toByteArray());
	}
}
