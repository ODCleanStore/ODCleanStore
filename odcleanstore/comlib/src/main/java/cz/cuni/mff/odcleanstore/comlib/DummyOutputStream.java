package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream for output size calculation only.
 *
 * @author Petr Jerman
 */
public class DummyOutputStream extends OutputStream {

	private long count;

	/**
	 * Increment counter of written bytes to the output.
	 * 
	 * @param arg byte to write 
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int arg) throws IOException {
		count++;
	}
	
	/**
	 * @return number of bytes written to the output.
	 */
	public long getCount() {
		return count;
	}
}
