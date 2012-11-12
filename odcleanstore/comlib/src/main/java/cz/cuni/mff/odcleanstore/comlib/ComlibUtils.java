package cz.cuni.mff.odcleanstore.comlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class ComlibUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";

	public static String loadResourceToString(@SuppressWarnings("rawtypes") Class clazz, String resourceName, String charset)
			throws IOException {
		BufferedReader reader = null;
		try {
			InputStream is = clazz.getResourceAsStream(resourceName);
			reader = new BufferedReader(new InputStreamReader(is, charset != null ? charset : DEFAULT_CHARSET));
			StringBuilder sb = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			return sb.toString();
		} finally {
			reader.close();
		}
	}

	public static void closeQuietly(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (Exception e) {
			// do nothing
		}
	}
}
