package cz.cuni.mff.odcleanstore.comlib.io;

import java.io.IOException;
import java.io.OutputStream;

public class HttpUtils {
		
	private static final int CR = 13;
	private static final int LF = 10;

	private HttpUtils() {
	}

	/**
	 * Write a http header line of data to the outputstream
	 * 
	 */
	public static void writeHeaderLine(OutputStream os, String line, Object... params) throws IOException {
		String str = line != null && params != null ? String.format(line, params) : line;

		if (str != null) {
			for (int i = 0; i < str.length(); i++) {
				int ch = str.charAt(i);
				os.write(ch);
			}
		}
		os.write(CR);
		os.write(LF);
	}
}
