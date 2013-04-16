package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * @author Jan Michelfeit
 * @todo test various aggregations
 */
public class ConflictResolverImplTest {
    private static final double EPSILON = 0.0;

    private static Statement oldVersionQuad;
    private static Statement newVersionQuad;
    private static Statement otherQuad;
    private static String updatedQuadDataSource;
    private ConflictResolverSpec spec;
    private NamedGraphMetadataMap metadata;

    @BeforeClass
    public static void beforeClass() {
        String subject = CRTestUtils.getUniqueURI();
        String predicate = CRTestUtils.getUniqueURI();
        String updatedQuadObject = CRTestUtils.getUniqueURI();
        oldVersionQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURI());
        newVersionQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURI());
        otherQuad = CRTestUtils.createStatement(
                subject,
                predicate,
                CRTestUtils.getUniqueURI(),
                CRTestUtils.getUniqueURI());
    }

    @Before
    public void beforeTest() {
        metadata = new NamedGraphMetadataMap();
        updatedQuadDataSource = CRTestUtils.getUniqueURI();
        String insertedBy = CRTestUtils.getUniqueURI();


        Calendar date = Calendar.getInstance();
        NamedGraphMetadata otherQuadMetadata =
                new NamedGraphMetadata(otherQuad.getContext().stringValue());
        // otherQuadMetadata.setScore();
        otherQuadMetadata.setSources(Collections.singleton(CRTestUtils.getUniqueURI()));
        otherQuadMetadata.setInsertedAt(date.getTime());
        otherQuadMetadata.setInsertedBy(insertedBy);
        metadata.addMetadata(otherQuadMetadata);

        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata oldVersionMetadata =
                new NamedGraphMetadata(oldVersionQuad.getContext().stringValue());
        // oldVersionMetadata.setScore();
        oldVersionMetadata.setSources(Collections.singleton(updatedQuadDataSource));
        oldVersionMetadata.setInsertedAt(date.getTime());
        oldVersionMetadata.setInsertedBy(insertedBy);
        metadata.addMetadata(oldVersionMetadata);

        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata newVersionMetadata =
                new NamedGraphMetadata(newVersionQuad.getContext().stringValue());
        // newVersionMetadata.setScore();
        newVersionMetadata.setSources(Collections.singleton(updatedQuadDataSource));
        newVersionMetadata.setInsertedAt(date.getTime());
        newVersionMetadata.setInsertedBy(insertedBy);
        metadata.addMetadata(newVersionMetadata);

        spec = new ConflictResolverSpec(CRTestUtils.getUniqueURI());
        spec.getAggregationSpec().setDefaultAggregation(EnumAggregationType.NONE);
        spec.setNamedGraphMetadata(metadata);
    }

    @Test
    public void testFilterOldVersionsPositive() throws ODCleanStoreException {
        // Prepare test data
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        conflictingQuads.add(oldVersionQuad);
        conflictingQuads.add(otherQuad);
        conflictingQuads.add(newVersionQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Statement>((conflictingQuads)));
        // Only newVersionQuad and otherQuad, oldVersionQuad is filtered out
        Assert.assertEquals(2, aggregationResult.size());
        double newVersionQuality = Double.NaN;
        double oldVersionQuality = Double.NaN;
        double otherQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(oldVersionQuad.getContext().stringValue())) {
                oldVersionQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getContext().stringValue())) {
                newVersionQuality = crQuad.getQuality();
                // TODO sources
            }
            if (crQuad.getSourceNamedGraphURIs().contains(otherQuad.getContext().stringValue())) {
                otherQuadQuality = crQuad.getQuality();
            }
        }
        Assert.assertEquals(Double.NaN, oldVersionQuality, EPSILON);
        Assert.assertTrue(newVersionQuality != Double.NaN);
        Assert.assertEquals(newVersionQuality, otherQuadQuality, EPSILON);
    }

    @Test
    public void testFilterOldVersionsDifferentSources() throws ODCleanStoreException {
        // A triple identical to newVersionQuad, but using
        // otherQuad's named graph gives it a different source
        Statement similarQuad = CRTestUtils.createStatement(
                newVersionQuad.getSubject().stringValue(),
                newVersionQuad.getPredicate().stringValue(),
                newVersionQuad.getObject().stringValue(),
                otherQuad.getContext().stringValue());
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(similarQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Statement>(conflictingQuads));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size());
    }

    @Test
    public void testFilterOldVersionsDifferentObjects() throws ODCleanStoreException {
        // A triple that would be filtered out if it had the same object
        // as newVersionQuad, but a different object makes it stay
        Statement oldVersionDiferentObjectQuad = CRTestUtils.createStatement(
                newVersionQuad.getSubject().stringValue(),
                newVersionQuad.getPredicate().stringValue(),
                CRTestUtils.getUniqueURI(),
                oldVersionQuad.getContext().stringValue());
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(oldVersionDiferentObjectQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Statement>((conflictingQuads)));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size());
    }

    @Test
    public void testFilterOldVersionsSameNamedGraphs() throws ODCleanStoreException {
        // Prepare test data
        Statement sameNamedGraphQuad = CRTestUtils.createStatement(
                newVersionQuad.getSubject().stringValue(),
                newVersionQuad.getPredicate().stringValue(),
                CRTestUtils.getUniqueURI(),
                newVersionQuad.getContext().stringValue());
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(otherQuad);
        conflictingQuads.add(sameNamedGraphQuad);

        // Create class instance
        Collection<Statement> sameAsLinks = Collections.singleton(CRTestUtils.createStatement(
                newVersionQuad.getObject().stringValue(),
                OWL.sameAs,
                sameNamedGraphQuad.getObject().stringValue()));
        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());
        spec.setURIMapping(uriMapping);
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Statement>((conflictingQuads)));
        // Nothing filtered out because of versions, but CR removes duplicates
        final long expectedQuadCount = 2;
        Assert.assertEquals(expectedQuadCount, aggregationResult.size());
        double newVersionQuality = Double.NaN;
        double sameNamedGraphQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(sameNamedGraphQuad.getContext().stringValue())) {
                sameNamedGraphQuadQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getContext().stringValue())) {
                newVersionQuality = crQuad.getQuality();
            }
        }
        Assert.assertEquals(newVersionQuality, sameNamedGraphQuadQuality, EPSILON);
    }
}
