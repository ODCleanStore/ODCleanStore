package cz.cuni.mff.odcleanstore;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

public final class TestUtils {
    private TestUtils() {
    }

    private static long uriCounter = 0;

    public static String getUniqueURI() {
        uriCounter++;
        return "http://example.com/" + Long.toString(uriCounter);
    }

    public static void resetURICounter() {
        uriCounter = 0;
    }

    public static Triple createTriple() {
        return Triple.create(
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()));
    }

    public static Triple createTriple(String subjectURI, String predicateURI, String objectURI) {
        return Triple.create(
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    public static Quad createQuad() {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()));
    }

    public static Quad createQuad(String subjectURI, String predicateURI, String objectURI) {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    public static Quad createQuad(
            String subjectURI, String predicateURI, String objectURI, String namedGraphURI) {
        return new Quad(
                Node.createURI(namedGraphURI),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    public static boolean triplesEqual(Triple triple1, Triple triple2) {
        if (triple1 == null || triple2 == null) {
            return triple1 == triple2;
        }
        return triple1.equals(triple2);
    }

    public static boolean quadsEquals(Quad quad1, Quad quad2) {
        if (quad1 == null || quad2 == null) {
            return quad1 == quad2;
        }
        return triplesEqual(quad1.getTriple(), quad2.getTriple());
    }

    public static boolean inCollection(Quad quad, Collection<Quad> collection) {
        for (Quad collectionQuad : collection) {
            if (quadsEquals(quad, collectionQuad)) {
                return true;
            }
        }
        return false;
    }
}
