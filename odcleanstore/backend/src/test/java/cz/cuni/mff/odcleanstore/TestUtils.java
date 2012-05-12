package cz.cuni.mff.odcleanstore;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Utility methods for JUnit tests.
 * @author Jan Michelfeit
 */
public final class TestUtils {

    /** Hide constructor for a utility class. */
    private TestUtils() {
    }

    private static long uriCounter = 0;

    /** Returns a URI unique within a test run. @return URI */
    public static String getUniqueURI() {
        uriCounter++;
        return "http://example.com/" + Long.toString(uriCounter);
    }

    /** Resets the URI counter used by {@link #getUniqueURI()}. */
    public static void resetURICounter() {
        uriCounter = 0;
    }

    /** Create a new unique triple. @return triple */
    public static Triple createTriple() {
        return Triple.create(
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()));
    }

    /**
     * Create a new triple with the given subject, predicate and object.
     * @param subjectURI subject URI
     * @param predicateURI predicate URI
     * @param objectURI object URI
     * @return triple
     */
    public static Triple createTriple(String subjectURI, String predicateURI, String objectURI) {
        return Triple.create(
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    /** Create a new unique quad. @return quad */
    public static Quad createQuad() {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()));
    }

    /**
     * Create a new quad with the given subject, predicate and object with a unique named graph URI.
     * @param subjectURI subject URI
     * @param predicateURI predicate URI
     * @param objectURI object URI
     * @return quad
     */
    public static Quad createQuad(String subjectURI, String predicateURI, String objectURI) {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    /**
     * Create a new quad with the given subject, predicate, object and named graph URI.
     * @param subjectURI subject URI
     * @param predicateURI predicate URI
     * @param objectURI object URI
     * @param namedGraphURI named graph URI
     * @return quad
     */
    public static Quad createQuad(String subjectURI, String predicateURI, String objectURI, String namedGraphURI) {
        return new Quad(
                Node.createURI(namedGraphURI),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    /**
     * Compare two triples for equality; null-proof.
     * @param triple1 a triple
     * @param triple2 a triple
     * @return true iff the two triples are equal
     */
    public static boolean triplesEqual(Triple triple1, Triple triple2) {
        if (triple1 == null || triple2 == null) {
            return triple1 == triple2;
        }
        return triple1.equals(triple2);
    }

    /**
     * Compare two quads for equality; null-proof.
     * @param quad1 a quad
     * @param quad2 a quad
     * @return true iff the two quads are equal
     */
    public static boolean quadsEquals(Quad quad1, Quad quad2) {
        if (quad1 == null || quad2 == null) {
            return quad1 == quad2;
        }
        return triplesEqual(quad1.getTriple(), quad2.getTriple()) && quad1.getGraphName().equals(quad2.getGraphName());
    }

    /**
     * Returns true iff a quad equal to the given quad is contained in the given collection.
     * @param quad a quad
     * @param collection a collection of quads
     * @return true iff a quad equal to the given quad is contained in the given collection
     */
    public static boolean inCollection(Quad quad, Collection<Quad> collection) {
        for (Quad collectionQuad : collection) {
            if (quadsEquals(quad, collectionQuad)) {
                return true;
            }
        }
        return false;
    }
}
