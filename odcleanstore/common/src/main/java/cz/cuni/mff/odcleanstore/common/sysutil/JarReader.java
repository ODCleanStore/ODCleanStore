/**
 * 
 */
package cz.cuni.mff.odcleanstore.common.sysutil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cz.cuni.mff.odcleanstorage.common.Empty;

/**
 * JarFile reader.
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class JarReader {
	private JarFile _jarFile;

	public JarReader(String jarFileName) {
		try {
			_jarFile = new JarFile(jarFileName);
		} catch (IOException e) {
			_jarFile = null;
		}
	}

	public Iterable<String> getEntryNames() {
		return _jarFile == null ? Empty.ITERABLE_STRING : new Iterable<String>() {

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

	public JarEntry getEntry(String name) {
		return _jarFile == null ? null : _jarFile.getJarEntry(name);
	}

	public JarEntry getClassEntry(String classFullName) {
		return _jarFile == null || classFullName == null ? null : _jarFile.getJarEntry(classFullName.replace(".", "/")
				+ ".class");
	}

	public byte[] getClassBytes(String classFullName) {
		try {
			BufferedInputStream jarBuf = new BufferedInputStream(_jarFile.getInputStream(getClassEntry(classFullName)));
			ByteArrayOutputStream jarOut = new ByteArrayOutputStream();

			int b;
			while ((b = jarBuf.read()) != -1)
				jarOut.write(b);

			return jarOut.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	public String getMainClassName() {
		try {
			return _jarFile.getManifest().getMainAttributes().getValue("Main-Class");

		} catch (IOException e) {
			return null;
		}
	}
}
