/**
 * 
 */
package cz.cuni.mff.odcleanstore.shared;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Utility methods for working with files.
 * @author jermanp
 */
@SuppressWarnings("serial")
public final class FileUtils {
    private static final Pattern XML_PATTERN = Pattern.compile("^\\s*<(\\?xml|rdf:RDF)");

    /** Disable constructor for utility class. */
    private FileUtils() {
    }

    public static String removeInitialBOMXml(String src) {
        return src != null && src.startsWith("\ufeff") ? src.substring(1) : src;
    }

    /** Error when working with a directory. */
    public static class DirectoryException extends ODCleanStoreException {
        DirectoryException(String message) {
            super(message);
        }

        DirectoryException(Throwable cause) {
            super(cause);
        }
    }

    public static String satisfyDirectory(String dirName) throws DirectoryException {
        try {
            File file = createFileObject(dirName, "");

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

    /**
     * Translates native string into ASCII code.
     * 
     * @param src the native java unicode string
     * @return src with non-ascii characters replaced with ASCII code
     */
    public static String unicodeToAscii(String src) {
        if (src == null) {
            return null;
        }

        StringBuilder buffer = new StringBuilder(src.length());
        for (int i = 0; i < src.length(); i++) {
            // CHECKSTYLE:OFF
            char c = src.charAt(i);
            if (c <= 0x7E) {
                buffer.append(c);
            } else {
                buffer.append("\\u");
                String hex = Integer.toHexString(c);
                for (int j = hex.length(); j < 4; j++) {
                    buffer.append('0');
                }
                buffer.append(hex);
            }
            // CHECKSTYLE:ON
        }
        return buffer.toString();
    }
    
    /**
     * Guess serialization format of the given RDF data.
     * The detection is a heuristic.
     * @param src serialized RDF data
     * @return serialization type
     */
    public static SerializationLanguage guessLanguage(String src) {
        if (XML_PATTERN.matcher(src).find()) {
            return SerializationLanguage.RDFXML;
        } else {
            return SerializationLanguage.N3;
        }
    }
}