/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author jermanp
 */
@SuppressWarnings("serial")
public final class Utils {
	
	public static class DirectoryException extends Exception {
		DirectoryException(String message) {
			super(message);
		}
		
		DirectoryException(Throwable cause) {
			super(cause);
		}
	}
	
	public static String checkDirectory(String dirName) throws DirectoryException {
		try {
			return checkDirectoryAndReturnCanonicalPath(createFileObject(dirName));
		} catch (DirectoryException e) {
			throw e;
		} catch (Exception e) {
			throw new DirectoryException(e);
		}
	}
	
	public static String satisfyDirectory(String dirName) throws DirectoryException {
		try {
			File file = createFileObject(dirName);
				
	 		if (!file.exists()) {
	 			satisfyParentDirectoryExist(file);
				file.mkdir();
			}
	 		
			return checkDirectoryAndReturnCanonicalPath(file);
		} catch (DirectoryException e) {
			throw e;
		} catch (Exception e) {
			throw new DirectoryException(e);
		}
	}
	
	private static File createFileObject(String fileName) {
		File file = new File(fileName);
		if (!file.isAbsolute()) {
			File curdir = new File("");
			file = new File(curdir.getAbsolutePath() + File.separator + file.getPath());
		}
		return file;
	}

	private static void satisfyParentDirectoryExist(File file) {
		File parent = file.getParentFile();
		if(parent != null) satisfyParentDirectoryExist(parent);
		if(!file.exists()) {
			file.mkdir();
		}
	}
	
	private static String checkDirectoryAndReturnCanonicalPath(File file) throws DirectoryException, IOException {
		String canonicalPath = file.getCanonicalPath();
		
		if (!file.isDirectory()) {
			throw new DirectoryException(String.format(" Directory %s not exists", canonicalPath));
		}

		if (!file.canRead()) {
			throw new DirectoryException(String.format(" Cannot read from directory %s", canonicalPath));
		}

		if (!file.canWrite()) {
			throw new DirectoryException(String.format(" Cannot write to directory %s", canonicalPath));
		}
		return canonicalPath;
	}
	
	

	public static Collection<String> selectColumn(Collection<String[]> source, int columnNum) {
		LinkedList<String> retVal = new LinkedList<String>();

		if (source == null | columnNum < 0) {
			return retVal;
		}

		for (String[] row : source) {
			if (columnNum >= row.length) {
				return new LinkedList<String>();
			}
			retVal.add(row[columnNum]);
		}

		return retVal;
	}

	public static String selectScalar(Collection<String[]> source) {
		if (source != null) {
			Iterator<String[]> iterator = source.iterator();
			if (iterator.hasNext()) {
				return iterator.next()[0];
			}
		}

		return null;
	}
}
