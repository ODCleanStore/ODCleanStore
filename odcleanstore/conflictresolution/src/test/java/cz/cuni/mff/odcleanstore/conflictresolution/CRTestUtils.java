package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.mockito.Mockito;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Utility methods for JUnit tests.
 * @author Jan Michelfeit
 */
public final class CRTestUtils {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    
    
    /** Hide constructor for a utility class. */
    private CRTestUtils() {
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

    /**
     * Create a new quad with the given subject, predicate and object with a unique named graph URI.
     * @param subjectURI subject URI
     * @param predicateURI predicate URI
     * @param objectURI object URI
     * @return quad
     */
    public static Statement createStatement(String subjectURI, String predicateURI, String objectURI) {
        return VALUE_FACTORY.createStatement(
                VALUE_FACTORY.createURI(subjectURI),
                VALUE_FACTORY.createURI(predicateURI),
                VALUE_FACTORY.createURI(objectURI),
                VALUE_FACTORY.createURI(getUniqueURI()));
    }
    
    /** Create a new unique quad. @return quad */
    public static Statement createStatement() {
        return VALUE_FACTORY.createStatement(
                VALUE_FACTORY.createURI(getUniqueURI()),
                VALUE_FACTORY.createURI(getUniqueURI()),
                VALUE_FACTORY.createURI(getUniqueURI()),
                VALUE_FACTORY.createURI(getUniqueURI()));
    }
    
    /**
     * Create a new quad with the given subject, predicate, object and named graph URI.
     * @param subjectURI subject URI
     * @param predicateURI predicate URI
     * @param objectURI object URI
     * @param namedGraphURI named graph URI
     * @return quad
     */
    public static Statement createStatement(String subjectURI, String predicateURI, String objectURI, String namedGraphURI) {
        return VALUE_FACTORY.createStatement(
                VALUE_FACTORY.createURI(subjectURI),
                VALUE_FACTORY.createURI(predicateURI),
                VALUE_FACTORY.createURI(objectURI),
                VALUE_FACTORY.createURI(namedGraphURI));
    }

    /**
     * Compare two triples for equality; null-proof.
     * @param statement1 a triple
     * @param statement2 a triple
     * @return true iff the two triples are equal
     */
    public static boolean statementsEqual(Statement statement1, Statement statement2) {
        if (statement1 == null || statement2 == null) {
            return statement1 == statement2;
        }
        return statement1.equals(statement2) 
                && ODCSUtils.nullProofEquals(statement1.getContext(), statement2.getContext());
    }

    /**
     * Returns true iff a quad equal to the given quad is contained in the given collection.
     * @param quad a quad
     * @param collection a collection of quads
     * @return true iff a quad equal to the given quad is contained in the given collection
     */
    public static boolean inCollection(Statement quad, Collection<Statement> collection) {
        for (Statement collectionQuad : collection) {
            if (statementsEqual(quad, collectionQuad)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a mock of ConflictResolutionConfig
     * @return a ConflictResolutionConfig mock
     */
    public static ConflictResolutionConfig createConflictResolutionConfigMock() {
        ConflictResolutionConfig config = Mockito.mock(ConflictResolutionConfig.class);
        Mockito.when(config.getAgreeCoeficient()).thenReturn(4d);
        Mockito.when(config.getScoreIfUnknown()).thenReturn(1d);
        Mockito.when(config.getNamedGraphScoreWeight()).thenReturn(0.8);
        Mockito.when(config.getPublisherScoreWeight()).thenReturn(0.2);
        Mockito.when(config.getMaxDateDifference()).thenReturn(
                366 * ODCSUtils.DAY_HOURS * ODCSUtils.TIME_UNIT_60 * ODCSUtils.TIME_UNIT_60);
        return config;
    }
}
