package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Calendar;

import org.junit.Before;
import org.junit.BeforeClass;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

public class ConflictResolverImplTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    //private static final double EPSILON = 0.0;

    // private static final URI SOURCE_SCORE_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.score);
    // private static final URI PUBLISHER_SCORE_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.publisherScore);
    // private static final URI PUBLISHER_PROPERTY = ValueFactoryImpl.getInstance().createURI(ODCS.publishedBy);

    private static Statement oldVersionQuad;
    private static Statement newVersionQuad;
    private static Statement otherQuad;
    private static URI updatedQuadDataSource;
    //private static ResolutionStrategy NONE_RESOLUTION_STRATEGY = new ResolutionStrategyImpl(NoneResolution.getName());
    private Model metadata;

    @BeforeClass
    public static void beforeClass() {
        String subject = CRTestUtils.getUniqueURIString();
        String predicate = CRTestUtils.getUniqueURIString();
        String updatedQuadObject = CRTestUtils.getUniqueURIString();
        oldVersionQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURIString());
        newVersionQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURIString());
        otherQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                CRTestUtils.getUniqueURIString(),
                CRTestUtils.getUniqueURIString());
    }

    @Before
    public void beforeTest() {
        metadata = new TreeModel();
        updatedQuadDataSource = CRTestUtils.getUniqueURI();
        URI insertedBy = CRTestUtils.getUniqueURI();

        Calendar date = Calendar.getInstance();
        metadata.add(otherQuad.getContext(), ODCS.SOURCE, CRTestUtils.getUniqueURI());
        metadata.add(otherQuad.getContext(), ODCS.INSERTED_AT, VALUE_FACTORY.createLiteral(date.getTime()));
        metadata.add(otherQuad.getContext(), ODCS.INSERTED_BY, insertedBy);

        date.add(Calendar.YEAR, 1);
        metadata.add(oldVersionQuad.getContext(), ODCS.SOURCE, updatedQuadDataSource);
        metadata.add(oldVersionQuad.getContext(), ODCS.INSERTED_AT, VALUE_FACTORY.createLiteral(date.getTime()));
        metadata.add(oldVersionQuad.getContext(), ODCS.INSERTED_BY, insertedBy);

        date.add(Calendar.YEAR, 1);
        metadata.add(newVersionQuad.getContext(), ODCS.SOURCE, updatedQuadDataSource);
        metadata.add(newVersionQuad.getContext(), ODCS.INSERTED_AT, VALUE_FACTORY.createLiteral(date.getTime()));
        metadata.add(newVersionQuad.getContext(), ODCS.INSERTED_BY, insertedBy);
    }

//    @Test
//    public void testFilterOldVersionsPositive() throws ODCleanStoreException {
//        // Create class instance
//        ConflictResolver conflictResolver = ConflictResolverFactory
//                .configureResolver()
//                .setMetadata(metadata)
//                .setDefaultResolutionStrategy(NONE_RESOLUTION_STRATEGY)
//                .create();
//
//        // Prepare test data
//        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
//        conflictingQuads.add(oldVersionQuad);
//        conflictingQuads.add(otherQuad);
//        conflictingQuads.add(newVersionQuad);
//
//        // Test results
//        Collection<ResolvedStatement> crResult = conflictResolver.resolveConflicts(conflictingQuads.iterator());
//
//        // Only newVersionQuad and otherQuad, oldVersionQuad is filtered out
//        Assert.assertEquals(2, crResult.size());
//        double newVersionQuality = Double.NaN;
//        double oldVersionQuality = Double.NaN;
//        double otherQuadQuality = Double.NaN;
//        for (ResolvedStatement crQuad : crResult) {
//            if (crQuad.getSourceGraphNames().contains(oldVersionQuad.getContext().stringValue())) {
//                oldVersionQuality = crQuad.getQuality();
//            }
//            if (crQuad.getSourceGraphNames().contains(newVersionQuad.getContext().stringValue())) {
//                newVersionQuality = crQuad.getQuality();
//                // TODO sources
//            }
//            if (crQuad.getSourceGraphNames().contains(otherQuad.getContext().stringValue())) {
//                otherQuadQuality = crQuad.getQuality();
//            }
//        }
//        Assert.assertEquals(Double.NaN, oldVersionQuality, EPSILON);
//        Assert.assertTrue(newVersionQuality != Double.NaN);
//        Assert.assertEquals(newVersionQuality, otherQuadQuality, EPSILON);
//    }
//
//    @Test
//    public void testFilterOldVersionsDifferentSources() throws ODCleanStoreException {
//        // Create class instance
//        ConflictResolver conflictResolver = ConflictResolverFactory
//                .configureResolver()
//                .setMetadata(metadata)
//                .setDefaultResolutionStrategy(NONE_RESOLUTION_STRATEGY)
//                .create();
//
//        // A triple identical to newVersionQuad, but using
//        // otherQuad's named graph gives it a different source
//        Statement similarQuad = CRTestUtils.createStatement(
//                newVersionQuad.getSubject().stringValue(),
//                newVersionQuad.getPredicate().stringValue(),
//                newVersionQuad.getObject().stringValue(),
//                otherQuad.getContext().stringValue());
//        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
//        conflictingQuads.add(newVersionQuad);
//        conflictingQuads.add(similarQuad);
//
//        // Test results
//        Collection<ResolvedStatement> crResult = conflictResolver.resolveConflicts(conflictingQuads.iterator());
//
//        // Neither quad was filtered out
//        Assert.assertEquals(2, crResult.size());
//    }
//
//    @Test
//    public void testFilterOldVersionsDifferentObjects() throws ODCleanStoreException {
//        // Create class instance
//        ConflictResolver conflictResolver = ConflictResolverFactory
//                .configureResolver()
//                .setMetadata(metadata)
//                .setDefaultResolutionStrategy(NONE_RESOLUTION_STRATEGY)
//                .create();
//
//        // A triple that would be filtered out if it had the same object
//        // as newVersionQuad, but a different object makes it stay
//        Statement oldVersionDiferentObjectQuad = CRTestUtils.createStatement(
//                newVersionQuad.getSubject().stringValue(),
//                newVersionQuad.getPredicate().stringValue(),
//                CRTestUtils.getUniqueURIString(),
//                oldVersionQuad.getContext().stringValue());
//        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
//        conflictingQuads.add(newVersionQuad);
//        conflictingQuads.add(oldVersionDiferentObjectQuad);
//
//        // Test results
//        Collection<ResolvedStatement> crResult = conflictResolver.resolveConflicts(conflictingQuads.iterator());
//        // Neither quad was filtered out
//        Assert.assertEquals(2, crResult.size());
//    }
//
//    @Test
//    public void testFilterOldVersionsSameNamedGraphs() throws ODCleanStoreException {
//        // Prepare test data
//        Statement sameNamedGraphQuad = CRTestUtils.createStatement(
//                newVersionQuad.getSubject().stringValue(),
//                newVersionQuad.getPredicate().stringValue(),
//                CRTestUtils.getUniqueURIString(),
//                newVersionQuad.getContext().stringValue());
//        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
//        conflictingQuads.add(newVersionQuad);
//        conflictingQuads.add(otherQuad);
//        conflictingQuads.add(sameNamedGraphQuad);
//
//        // Create class instance
//        ConflictResolver conflictResolver = ConflictResolverFactory
//                .configureResolver()
//                .setMetadata(metadata)
//                .setDefaultResolutionStrategy(NONE_RESOLUTION_STRATEGY)
//                .addLink((URI) newVersionQuad.getObject(), (URI) sameNamedGraphQuad.getObject())
//                .create();
//
//        // Test results
//        Collection<ResolvedStatement> crResult = conflictResolver.resolveConflicts(conflictingQuads.iterator());
//
//        // Nothing filtered out because of versions, but CR removes duplicates
//        final long expectedQuadCount = 2;
//        Assert.assertEquals(expectedQuadCount, crResult.size());
//        double newVersionQuality = Double.NaN;
//        double sameNamedGraphQuadQuality = Double.NaN;
//        for (ResolvedStatement crQuad : crResult) {
//            if (crQuad.getSourceGraphNames().contains(sameNamedGraphQuad.getContext().stringValue())) {
//                sameNamedGraphQuadQuality = crQuad.getQuality();
//            }
//            if (crQuad.getSourceGraphNames().contains(newVersionQuad.getContext().stringValue())) {
//                newVersionQuality = crQuad.getQuality();
//            }
//        }
//        Assert.assertEquals(newVersionQuality, sameNamedGraphQuadQuality, EPSILON);
//    }
}
