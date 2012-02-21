package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

/**
 * @author Jan Michelfeit
 * @todo test various aggregations
 */
public class ConflictResolverImplTest {
    private static final double EPSILON = 0.0;
    private static final int CONFLICTING_QUAD_COUNT = 3;
    
    private static Quad oldVersionQuad;
    private static Quad newVersionQuad;
    private static Quad otherQuad;
    private static String updatedQuadDataSource;
    private ConflictResolverSpec spec;
    private NamedGraphMetadataMap metadata;
    
    @BeforeClass
    public static void beforeClass() {
        String subject = TestUtils.getUniqueURI();
        String predicate = TestUtils.getUniqueURI();
        String updatedQuadObject = TestUtils.getUniqueURI();
        oldVersionQuad = TestUtils.createQuad(
                subject,
                predicate,
                updatedQuadObject,
                TestUtils.getUniqueURI());
        newVersionQuad = TestUtils.createQuad(
                subject,
                predicate,
                updatedQuadObject,
                TestUtils.getUniqueURI());
        otherQuad = TestUtils.createQuad(
                subject,
                predicate,
                TestUtils.getUniqueURI(),
                TestUtils.getUniqueURI());
    }
    
    @Before
    public void beforeTest() {
        metadata = new NamedGraphMetadataMap();
        updatedQuadDataSource = TestUtils.getUniqueURI();

        Calendar date = Calendar.getInstance();
        NamedGraphMetadata otherQuadMetadata =
                new NamedGraphMetadata(otherQuad.getNamedGraph());
        //otherQuadMetadata.setScore();
        otherQuadMetadata.setDataSource(TestUtils.getUniqueURI());
        otherQuadMetadata.setStored(date.getTime());
        metadata.addMetadata(otherQuadMetadata);
        
        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata oldVersionMetadata =
                new NamedGraphMetadata(oldVersionQuad.getNamedGraph());
        //oldVersionMetadata.setScore();
        oldVersionMetadata.setDataSource(updatedQuadDataSource);
        oldVersionMetadata.setStored(date.getTime());
        metadata.addMetadata(oldVersionMetadata);
        
        date.add(Calendar.YEAR, 1);
        NamedGraphMetadata newVersionMetadata =
                new NamedGraphMetadata(newVersionQuad.getNamedGraph());
        //newVersionMetadata.setScore();
        newVersionMetadata.setDataSource(updatedQuadDataSource);
        newVersionMetadata.setStored(date.getTime());
        metadata.addMetadata(newVersionMetadata);
        
        spec = new ConflictResolverSpec(TestUtils.getUniqueURI());
        spec.setDefaultAggregation(AggregationType.NONE);
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
        ConflictResolverImpl instance = new ConflictResolverImpl(spec);
        
        // Test results
        Collection<CRQuad> aggregationResult = 
                instance.resolveConflicts(new QuadGraph(conflictingQuads));
        // Only newVersionQuad and otherQuad, oldVersionQuad is filtered out
        Assert.assertEquals(2, aggregationResult.size()); 
        double newVersionQuality = Double.NaN;
        double oldVersionQuality = Double.NaN;
        double otherQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(oldVersionQuad.getNamedGraph())) {
                oldVersionQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getNamedGraph())) {
                newVersionQuality = crQuad.getQuality();
                // TODO sources
            }
            if (crQuad.getSourceNamedGraphURIs().contains(otherQuad.getNamedGraph())) {
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
        Quad similarQuad = TestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                newVersionQuad.getObject().getURI(),
                otherQuad.getNamedGraph());
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(similarQuad);
        
        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec);
        
        // Test results
        Collection<CRQuad> aggregationResult = 
                instance.resolveConflicts(new QuadGraph(conflictingQuads));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size()); 
    }
    
    @Test
    public void testFilterOldVersionsDifferentObjects() throws ODCleanStoreException {
        // A triple that would be filtered out if it had the same object
        // as newVersionQuad, but a different object makes it stay
        Quad oldVersionDiferentObjectQuad = TestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                TestUtils.getUniqueURI(),
                oldVersionQuad.getNamedGraph());
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(oldVersionDiferentObjectQuad);
        
        // Create class instance
        ConflictResolverImpl instance = new ConflictResolverImpl(spec);
        
        // Test results
        Collection<CRQuad> aggregationResult = 
                instance.resolveConflicts(new QuadGraph(conflictingQuads));
        // Neither quad was filtered out
        Assert.assertEquals(2, aggregationResult.size()); 
    }
    
    @Test
    public void testFilterOldVersionsSameNamedGraphs() throws ODCleanStoreException {
        // Prepare test data
        Quad sameNamedGraphQuad = TestUtils.createQuad(
                newVersionQuad.getSubject().getURI(),
                newVersionQuad.getPredicate().getURI(),
                TestUtils.getUniqueURI(),
                newVersionQuad.getNamedGraph());
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        conflictingQuads.add(newVersionQuad);
        conflictingQuads.add(otherQuad);
        conflictingQuads.add(sameNamedGraphQuad);
        
        // Create class instance
        Collection<Triple> sameAsLinks = Collections.singleton(TestUtils.createTriple(
                newVersionQuad.getObject().getURI(),
                OWL.sameAs,
                sameNamedGraphQuad.getObject().getURI()));
        spec.setSameAsLinks(sameAsLinks.iterator());
        ConflictResolverImpl instance = new ConflictResolverImpl(spec);
        
        // Test results
        Collection<CRQuad> aggregationResult = 
                instance.resolveConflicts(new QuadGraph(conflictingQuads));
        // Nothing filtered out
        final long expectedQuadCount = 3;
        Assert.assertEquals(expectedQuadCount, aggregationResult.size());
        double newVersionQuality = Double.NaN;
        double sameNamedGraphQuadQuality = Double.NaN;
        double otherQuadQuality = Double.NaN;
        for (CRQuad crQuad : aggregationResult) {
            if (crQuad.getSourceNamedGraphURIs().contains(sameNamedGraphQuad.getNamedGraph())) {
                sameNamedGraphQuadQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(newVersionQuad.getNamedGraph())) {
                newVersionQuality = crQuad.getQuality();
            }
            if (crQuad.getSourceNamedGraphURIs().contains(otherQuad.getNamedGraph())) {
                otherQuadQuality = crQuad.getQuality();
            }
        }
        Assert.assertEquals(newVersionQuality, sameNamedGraphQuadQuality, EPSILON);
        // newVersionQuad and sameNamedGraphQuad agree on the same value thus
        // they should have higher quality
        Assert.assertTrue(otherQuadQuality < newVersionQuality); 
    }
}
