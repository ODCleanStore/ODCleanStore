package cz.cuni.mff.odcleanstore.vocabulary;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;


/**
 * Vocabulary definitions for XPathFunctions.
 *
 * @author Jakub Daniel
 */
public final class XPathFunctions {
    /** The namespace of the vocabulary as a string. */
    private static final String NAMESPACE = "http://www.w3.org/2005/xpath-functions:";
    
    /** Disable constructor for a utility class. */
    private XPathFunctions() {
    }

    /* Vocabulary properties: */
    /** Boolean function. */
    public static final URI BOOLEAN_FUNCTION;
    
    /** Date function. */
    public static final URI DATE_FUNCTION;
    
    /** String function. */
    public static final URI STRING_FUNCTION;
    
    static {
        ValueFactory factory = ValueFactoryImpl.getInstance();

        BOOLEAN_FUNCTION = factory.createURI(NAMESPACE, "boolean");
        DATE_FUNCTION = factory.createURI(NAMESPACE, "date");
        STRING_FUNCTION = factory.createURI(NAMESPACE, "string");
    }
}
