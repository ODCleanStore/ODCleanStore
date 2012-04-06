/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

/**
 * JarFile reader.
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class JarReader {

	private final JarFile _jarFile;

	/**
	 * Construct JarReader object from file name.
	 * 
	 * @param jarFileName
	 *            File name of jar archive file.
	 * @throws IOException
	 * @throws SecurityException
	 */
	public JarReader(String jarFileName) throws IOException {
		_jarFile = new JarFile(jarFileName);
	}

	/**
	 * Gets all entry names in jar archive.
	 * 
	 * @return Iterable object of names.
	 */
	public Iterable<String> getEntryNames() {
		return new Iterable<String>() {

			public Iterator<String> iterator() {
				return new Iterator<String>() {

					private Enumeration<JarEntry> jarEntries = _jarFile.entries();

					public boolean hasNext() {
						return jarEntries.hasMoreElements();
					}

					public String next() {
						return jarEntries.nextElement().getName();
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

	/**
	 * Gets Name of main runnable class in jar archive if exists or null.
	 * 
	 * @return Name of MainClass if exists or null.
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String getMainClassName() throws IOException {
		return _jarFile.getManifest().getMainAttributes().getValue("Main-Class");
	}

	/**
	 * Get bytecode of class from full class name if exists or null.
	 * 
	 * @param classFullName
	 *            Full name of class.
	 * @return Bytecode of class if exists or null.
	 * @throws ZipException
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws SecurityException
	 */
	public byte[] getClassBytes(String classFullName) throws ZipException, IOException {
		JarEntry jarEntry = getClassEntry(classFullName);
		if (jarEntry == null)
			return null;

		BufferedInputStream jarBuf = new BufferedInputStream(_jarFile.getInputStream(jarEntry));
		ByteArrayOutputStream jarOut = new ByteArrayOutputStream();

		int b;
		while ((b = jarBuf.read()) != -1)
			jarOut.write(b);

		return jarOut.toByteArray();
	}

	/**
	 * Gets JarEntry from full class name if exists or null.
	 * 
	 * @param classFullName
	 *            Full class name.
	 * @return JarEntry for full class name if exist or null.
	 * @throws IllegalStateException
	 */
	private JarEntry getClassEntry(String classFullName) {
		return classFullName == null ? null : _jarFile.getJarEntry(classFullName.replace(".", "/") + ".class");
	}
}
