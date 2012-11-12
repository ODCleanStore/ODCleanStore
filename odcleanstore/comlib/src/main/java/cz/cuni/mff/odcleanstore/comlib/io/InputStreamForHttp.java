package cz.cuni.mff.odcleanstore.comlib.io;

import java.io.IOException;
import java.io.InputStream;

public final class InputStreamForHttp extends InputStream {
	
	private static final int MAXIMUM_HTTP_HEADER_LINE_SIZE = 4096;
	
	private static final int CR = 13;
	private static final int LF = 10;

	private InputStream is;
	private boolean isExtraHandling;
	private int extraChar;

	public InputStreamForHttp(InputStream is) {
		if (is == null) {
			throw new IllegalArgumentException("InputStream is null");
		}
		this.is = is;
	}

	public void setForceEOF() {
		extraChar = -1;
		isExtraHandling = true;
	}

	public String readAsciiLine() throws IOException {
		StringBuilder sb = new StringBuilder();
		int ch = read();
		int count = 0;
		while (ch != CR && ch != LF && ch != -1 && count <= MAXIMUM_HTTP_HEADER_LINE_SIZE) {
			sb.append((char) ch);
			ch = read();
			count++;
		}
		if (ch == CR) {
			ch = read();
			if (ch != LF) {
				extraChar = ch;
				isExtraHandling = true;
			}
		}
		if (count > MAXIMUM_HTTP_HEADER_LINE_SIZE) {
			throw new IOException("Maximum http header line size reached");
		}
		return sb.toString();
	}

	@Override
	public int available() throws IOException {
		if (!isExtraHandling)
			return is.available();
		return extraChar == -1 ? 0 : 1;
	}

	@Override
	public int read() throws IOException {
		if (!isExtraHandling)
			return is.read();
		if (extraChar != -1) {
			isExtraHandling = false;
		}
		return extraChar;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (!isExtraHandling)
			return is.read(b, off, len);
		if (extraChar == -1)
			return -1;
		if (len <= 0)
			return 0;
		b[off] = (byte) extraChar;
		isExtraHandling = false;
		return 1;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	public void closeQuietly() {
		try {
			is.close();
		} catch (Exception e) {
			// do nothing
		}
	}
}
