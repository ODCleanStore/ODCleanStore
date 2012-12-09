/**
 * 
 */
package cz.cuni.mff.odcleanstore.comlib.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Reader with set eof method.
 * 
 * @author Petr Jerman
 */
public class EndableReader extends Reader {

	private Reader reader;
	private boolean isEnded;

	public EndableReader(Reader reader) {
		this.reader = reader;
	}

	/**
	 * Set EOF.
	 */
	public void setForceEOF() {
		isEnded = true;
	}

	/**

	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		// do nothing
	}

	/**
	 * @see java.io.Reader#read()
	 */
	@Override
	public int read() throws IOException {
		if (isEnded) {
			return -1;
		}
		return super.read();
	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (isEnded) {
			return -1;
		}
		return reader.read(cbuf, off, len);
	}

	/**
	 * @see java.io.Reader#ready()
	 */
	@Override
	public boolean ready() throws IOException {
		if (isEnded) {
			return true;
		}
		return super.ready();
	}
}
