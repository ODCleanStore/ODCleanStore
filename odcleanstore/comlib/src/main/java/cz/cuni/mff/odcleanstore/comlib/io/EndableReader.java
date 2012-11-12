/**
 * 
 */
package cz.cuni.mff.odcleanstore.comlib.io;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Administrator
 * 
 */
public class EndableReader extends Reader {

	private Reader reader;
	private boolean isEnded;

	public EndableReader(Reader reader) {
		this.reader = reader;
	}

	public void setForceEOF() {
		isEnded = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public int read() throws IOException {
		if (isEnded) {
			return -1;
		}
		return super.read();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (isEnded) {
			return -1;
		}
		return reader.read(cbuf, off, len);
	}

	@Override
	public boolean ready() throws IOException {
		if (isEnded) {
			return true;
		}
		return super.ready();
	}
}
