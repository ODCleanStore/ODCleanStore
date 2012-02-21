package cz.cuni.mff.odcleanstore;

import java.util.Collection;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;

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
        return new Triple(
                new URITripleItem(getUniqueURI()),
                new URITripleItem(getUniqueURI()),
                new URITripleItem(getUniqueURI()));
    }
    
    public static Triple createTriple(String subjectURI, String predicateURI, String objectURI) {
        return new Triple(
                new URITripleItem(subjectURI),
                new URITripleItem(predicateURI),
                new URITripleItem(objectURI));
    }
    
    public static Quad createQuad() {
        return new Quad(
                new URITripleItem(getUniqueURI()),
                new URITripleItem(getUniqueURI()),
                new URITripleItem(getUniqueURI()),
                getUniqueURI());
    }
    
    public static Quad createQuad(String subjectURI, String predicateURI, String objectURI) {
        return new Quad(
                new URITripleItem(subjectURI),
                new URITripleItem(predicateURI),
                new URITripleItem(objectURI),
                getUniqueURI());
    }
    
    public static Quad createQuad(
            String subjectURI, String predicateURI, String objectURI, String namedGraphURI) {
        return new Quad(
                new URITripleItem(subjectURI),
                new URITripleItem(predicateURI),
                new URITripleItem(objectURI),
                namedGraphURI);
    }
    
    public static boolean triplesEqual(Triple triple1, Triple triple2) {
        if (triple1 == null || triple2 == null) {
            return triple1 == triple2;
        }
        return triple1.getSubject().equals(triple2.getSubject())
                && triple1.getPredicate().equals(triple2.getPredicate())
                && triple1.getObject().equals(triple2.getObject());
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
