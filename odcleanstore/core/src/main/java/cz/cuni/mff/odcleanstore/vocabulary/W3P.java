package cz.cuni.mff.odcleanstore.vocabulary;

import org.openrdf.model.Namespace;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.NamespaceImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Vocabulary definitions of W3p provenance model for the Web.
 *
 * @author Jan Michelfeit
 */
public final class W3P {
    /** The namespace of the vocabulary as a string. */
    public static final String NAMESPACE = "http://purl.org/provenance#";
    
    /**
     * Recommended prefix for the W3P namespace.
     */
    public static final String PREFIX = "w3p";

    /**
     * An immutable {@link Namespace} constant that represents the W3P namespace.
     */
    public static final Namespace NS = new NamespaceImpl(PREFIX, NAMESPACE);

    /** Disable constructor for a utility class. */
    private W3P() {
    }

    /* Vocabulary properties: */
    /**
     * Publisher of data.
     */
    public static final URI PUBLISHED_BY;

    /**
     * An Artifact has another Artifact as its source.
     */
    public static final URI SOURCE;

    /**
     * An Artifact was inserted to the data store by a User.
     */
    public static final URI INSERTED_BY;

    /**
     * The data was inserted at.
     */
    public static final URI INSERTED_AT;

    static {
        ValueFactory factory = ValueFactoryImpl.getInstance();

        PUBLISHED_BY = factory.createURI(W3P.NAMESPACE, "publishedBy");
        SOURCE = factory.createURI(W3P.NAMESPACE, "source");
        INSERTED_BY = factory.createURI(W3P.NAMESPACE, "insertedBy");
        INSERTED_AT = factory.createURI(W3P.NAMESPACE, "insertedAt");
    }
}
