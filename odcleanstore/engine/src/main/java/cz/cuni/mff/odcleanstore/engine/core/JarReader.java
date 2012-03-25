/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.core;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * @author jermanp
 * 
 */
public class JarReader {

	private JarFile jarFile;

	/**
	 * @param jarFileName
	 */
	public JarReader(String jarFileName) {
		try {
			jarFile = new JarFile(jarFileName);
		} catch (IOException e) {
			jarFile = null;
		}
	}

	public Iterable<String> getEntryNames() {
		return jarFile == null ? Empty.ITERABLE_STRING : new Iterable<String>() {

			public Iterator<String> iterator() {
				return new Iterator<String>() {

					private Enumeration<JarEntry> jarEntries = jarFile.entries();

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
		return jarFile == null ? null : jarFile.getJarEntry(name);
	}

	public JarEntry getClassEntry(String classFullName) {
		return jarFile == null || classFullName == null ? null : jarFile.getJarEntry(classFullName.replace(
				".", "/") + ".class");
	}
}
