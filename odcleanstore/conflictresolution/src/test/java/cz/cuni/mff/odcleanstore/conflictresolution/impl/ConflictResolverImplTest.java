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

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * @author Jan Michelfeit
 * @todo test various aggregations
 */
public class ConflictResolverImplTest {
    private static final double EPSILON = 0.0;

    private static Quad oldVersionQuad;
    private static Quad newVersionQuad;
    private static Quad otherQuad;
    private static String updatedQuadDataSource;
    private ConflictResolverSpec spec;
    private NamedGraphMetadataMap metadata;

    @BeforeClass
    public static void beforeClass() {
        String subject = CRTestUtils.getUniqueURI();
        String predicate = CRTestUtils.getUniqueURI();
        String updatedQuadObject = CRTestUtils.getUniqueURI();
        oldVersionQuad = CRTestUtils.createQuad(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURI());
        newVersionQuad = CRTestUtils.createQuad(
                subject,
                predicate,
                updatedQuadObject,
                CRTestUtils.getUniqueURI());
        otherQuad = CRTestUtils.createQuad(
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
                new NamedGraphMetadata(otherQuad.getGraphName().getURI());
        // otherQuadMetadata.setScore();
        otherQuadMetadata.setSources(Collections.singleton(CRTestUtils.getUniqueURI()));
        otherQuadMetadata.setInsertedAt(date.getTime());
        otherQuadMetadata.setInsertedBy(insertedBy);
        metadata.addMetadata(otherQuadMetadata);

        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata oldVersionMetadata =
                new NamedGraphMetadata(oldVersionQuad.getGraphName().getURI());
        // oldVersionMetadata.setScore();
        oldVersionMetadata.setSources(Collections.singleton(updatedQuadDataSource));
        oldVersionMetadata.setInsertedAt(date.getTime());
        oldVersionMetadata.setInsertedBy(insertedBy);
        metadata.addMetadata(oldVersionMetadata);

        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata newVersionMetadata =
                new NamedGraphMetadata(newVersionQuad.getGraphName().getURI());
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
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(oldVersionQuad);
        conflictingQuads.add(otherQuad);
        conflictingQuads.add(newVersionQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Quad>((conflictingQuads)));
        // Only newVersionQuad and otherQuad, oldVersionQuad is filtered out
        Assert.assertEquals(2, aggregationResult.size());
        double newVersionQuality = Double.NaN;
        double oldVersionQuality = Double.NaN;
        double otherQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(oldVersionQuad.getGraphName().getURI())) {
                oldVersionQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getGraphName().getURI())) {
                newVersionQuality = crQuad.getQuality();
                // TODO sources
            }
            if (crQuad.getSourceNamedGraphURIs().contains(otherQuad.getGraphName().getURI())) {
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
        Quad similarQuad = CRTestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                newVersionQuad.getObject().getURI(),
                otherQuad.getGraphName().getURI());
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(similarQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Quad>(conflictingQuads));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size());
    }

    @Test
    public void testFilterOldVersionsDifferentObjects() throws ODCleanStoreException {
        // A triple that would be filtered out if it had the same object
        // as newVersionQuad, but a different object makes it stay
        Quad oldVersionDiferentObjectQuad = CRTestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                CRTestUtils.getUniqueURI(),
                oldVersionQuad.getGraphName().getURI());
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(oldVersionDiferentObjectQuad);

        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Quad>((conflictingQuads)));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size());
    }

    @Test
    public void testFilterOldVersionsSameNamedGraphs() throws ODCleanStoreException {
        // Prepare test data
        Quad sameNamedGraphQuad = CRTestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                CRTestUtils.getUniqueURI(),
                newVersionQuad.getGraphName().getURI());
        Collection<Quad> conflictingQuads = new LinkedList<de.fuberlin.wiwiss.ng4j.Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(otherQuad);
        conflictingQuads.add(sameNamedGraphQuad);

        // Create class instance
        Collection<com.hp.hpl.jena.graph.Triple> sameAsLinks = Collections.singleton(CRTestUtils.createTriple(
                newVersionQuad.getObject().getURI(),
                OWL.sameAs,
                sameNamedGraphQuad.getObject().getURI()));
        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());
        spec.setURIMapping(uriMapping);
        ConflictResolverImpl instance = new ConflictResolverImpl(spec, CRTestUtils.createConflictResolutionConfigMock());

        // Test results
        Collection<CRQuad> aggregationResult =
                instance.resolveConflicts(new ArrayList<Quad>((conflictingQuads)));
        // Nothing filtered out because of versions, but CR removes duplicates
        final long expectedQuadCount = 2;
        Assert.assertEquals(expectedQuadCount, aggregationResult.size());
        double newVersionQuality = Double.NaN;
        double sameNamedGraphQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(sameNamedGraphQuad.getGraphName().getURI())) {
                sameNamedGraphQuadQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getGraphName().getURI())) {
                newVersionQuality = crQuad.getQuality();
            }
        }
        Assert.assertEquals(newVersionQuality, sameNamedGraphQuadQuality, EPSILON);
    }
}
