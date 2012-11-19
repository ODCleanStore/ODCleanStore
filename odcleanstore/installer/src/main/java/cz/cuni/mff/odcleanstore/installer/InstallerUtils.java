package cz.cuni.mff.odcleanstore.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class InstallerUtils {

	private static final String WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES = "WEB-INF/classes/config/application.properties";
	private static final String ODCS_CONFIG_PATH_PROPERTY_NAME = "odcs.config.path";

	private static final byte[] BUFFER = new byte[4096 * 1024];

	public static void runIsql() throws IOException {
		Runtime r = Runtime.getRuntime();
		Process p;
		BufferedReader is;
		String line;

		// p = r.exec("isql --help", null, new File("C:\\odcs"));
		p = r.exec("isql --help");

		is = new BufferedReader(new InputStreamReader(p.getInputStream()));

		while ((line = is.readLine()) != null)
			System.out.println(line);
		System.out.flush();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			System.err.println(e);
			return;
		}
		System.err.println("Isql done with exit status " + p.exitValue());
		return;
	}

	public static void copyFolder(File src, File dst) throws IOException {

		if (src.isDirectory()) {
			if (!dst.exists()) {
				dst.mkdir();
			}

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dst, file);
				copyFolder(srcFile, destFile);
			}

		} else {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(src);
				out = new FileOutputStream(dst);

				int length;
				while ((length = in.read(BUFFER)) > 0) {
					out.write(BUFFER, 0, length);
				}
			} finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			}
		}
	}

	public static void installFE(String srcWarFileName, String dstWarFileName, String odcsIniFileName) throws IOException {
		JarFile war = new JarFile(srcWarFileName);
		JarOutputStream append = new JarOutputStream(new FileOutputStream(dstWarFileName));

		try {
			war = new JarFile(srcWarFileName);
			append = new JarOutputStream(new FileOutputStream(dstWarFileName));

			Enumeration<? extends JarEntry> entries = war.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (!e.isDirectory()) {
					if (e.getName().equalsIgnoreCase(WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES)) {
						append.putNextEntry(new JarEntry(e.getName()));
						String appString = ODCS_CONFIG_PATH_PROPERTY_NAME + " = " + odcsIniFileName;
						copy(appString, append);
					} else {
						append.putNextEntry(e);
						copy(war.getInputStream(e), append);
					}
				}
				append.closeEntry();
			}
		} finally {
			war.close();
			append.close();
		}
	}

	private static void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, bytesRead);
		}
	}

	private static void copy(String input, OutputStream output) throws IOException {
		output.write(input.getBytes());
	}
}
