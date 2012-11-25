/**
 * 
 */
package cz.cuni.mff.odcleanstore.installer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility methods for working with files.
 * @author jermanp
 */
@SuppressWarnings("serial")
public final class FileUtils {
	
	private static final byte[] BUFFER = new byte[4096 * 1024];
	
    /** Disable constructor for utility class. */
    private FileUtils() {
    }

    /** Error when working with a directory. */
    public static class DirectoryException extends Exception {
        DirectoryException(String message) {
            super(message);
        }

        DirectoryException(Throwable cause) {
            super(cause);
        }
    }

	public static void copyFolder(File src, File dst, Runnable afterStep) throws IOException {

		if (src.isDirectory()) {
			if (!dst.exists()) {
				dst.mkdir();
			}

			String files[] = src.list();

			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dst, file);
				copyFolder(srcFile, destFile, afterStep);
			}
		} else {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = new FileInputStream(src);
				out = new FileOutputStream(dst);
				System.out.println(String.format("copy file %s", src.getName()));
				afterStep.run();
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
    
    public static String satisfyDirectory(String dirName) throws DirectoryException {
        return satisfyDirectory(dirName, ".");
    }

    public static String satisfyDirectory(String dirName, String baseForRelativePath) throws DirectoryException {
        try {
            File file = createFileObject(dirName, baseForRelativePath);

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

    private static File createFileObject(String fileName, String baseForRelativePath) {
        File file = new File(fileName);
        if (!file.isAbsolute()) {
            File curdir = new File(baseForRelativePath);
            file = new File(curdir, file.getPath());
        }
        return file;
    }

    private static void satisfyParentDirectoryExist(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            satisfyParentDirectoryExist(parent);
        }
        if (!file.exists()) {
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
}
