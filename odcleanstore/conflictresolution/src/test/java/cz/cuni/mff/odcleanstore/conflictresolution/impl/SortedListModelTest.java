/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.SpogComparator;

public class SortedListModelTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    private URI[] subjects;
    private URI[] predicates;
    private URI[] objects;
    private ArrayList<Statement> statements;

    @Before
    public void setUpStatements() {
        statements = new ArrayList<Statement>();
        subjects = new URI[] { CRTestUtils.getUniqueURI(), CRTestUtils.getUniqueURI() };
        predicates = new URI[] { CRTestUtils.getUniqueURI(), CRTestUtils.getUniqueURI() };
        objects = new URI[] { CRTestUtils.getUniqueURI(), CRTestUtils.getUniqueURI() };

        for (int i = 0; i < subjects.length; i++) {
            for (int j = 0; j < predicates.length; j++) {
                for (int k = 0; k < objects.length; k++) {
                    statements.add(CRTestUtils.createStatement(
                            subjects[i].stringValue(),
                            predicates[j].stringValue(),
                            objects[k].stringValue()));
                }
            }
        }
        Collections.sort(statements, new SpogComparator());
    }

    // TODO: test contexts

    @Test
    public void testElements() {
        Model model = new SortedListModel(statements);

        Set<Resource> modelSubjects = model.subjects();
        Assert.assertEquals(subjects.length, modelSubjects.size());
        for (int i = 0; i < subjects.length; i++) {
            Assert.assertTrue(modelSubjects.contains(subjects[i]));
        }

        Set<URI> modelPredicates = model.predicates();
        Assert.assertEquals(predicates.length, modelPredicates.size());
        for (int i = 0; i < predicates.length; i++) {
            Assert.assertTrue(modelPredicates.contains(predicates[i]));
        }

        Set<Value> modelObjects = model.objects();
        Assert.assertEquals(objects.length, modelObjects.size());
        for (int i = 0; i < objects.length; i++) {
            Assert.assertTrue(modelObjects.contains(objects[i]));
        }
    }

    @Test
    public void testContains() {
        Model model = new SortedListModel(statements);

        URI otherSubject = CRTestUtils.getUniqueURI();
        URI otherPredicate = CRTestUtils.getUniqueURI();
        URI otherObject = CRTestUtils.getUniqueURI();

        Assert.assertTrue(model.contains(subjects[0], null, null));
        Assert.assertTrue(model.contains(null, predicates[0], null));
        Assert.assertTrue(model.contains(null, null, objects[0]));
        Assert.assertTrue(model.contains(subjects[0], predicates[0], objects[0]));

        Assert.assertFalse(model.contains(otherSubject, null, null));
        Assert.assertFalse(model.contains(null, otherPredicate, null));
        Assert.assertFalse(model.contains(null, null, otherObject));
        Assert.assertFalse(model.contains(subjects[0], otherPredicate, null));
        Assert.assertFalse(model.contains(otherSubject, predicates[0], objects[0]));
        Assert.assertFalse(model.contains(subjects[0], otherPredicate, objects[0]));
    }

    @Test
    public void testFilter() {
        Model model = new SortedListModel(statements);

        URI otherSubject = CRTestUtils.getUniqueURI();
        URI otherPredicate = CRTestUtils.getUniqueURI();
        URI otherObject = CRTestUtils.getUniqueURI();

        Model filteredModel;
        Set<Statement> expectedStatements;

        filteredModel = model.filter(subjects[0], null, null);
        Assert.assertEquals(objects.length * predicates.length, filteredModel.size());
        expectedStatements = new HashSet<Statement>(filteredModel);
        for (int i = 0; i < predicates.length; i++) {
            for (int j = 0; j < objects.length; j++) {
                Assert.assertTrue(expectedStatements.contains(
                        CRTestUtils.createStatement(subjects[0], predicates[i], objects[i])));
            }
        }

        filteredModel = model.filter(null, predicates[0], null);
        Assert.assertEquals(subjects.length * objects.length, filteredModel.size());
        expectedStatements = new HashSet<Statement>(filteredModel);
        for (int i = 0; i < subjects.length; i++) {
            for (int j = 0; j < objects.length; j++) {
                Assert.assertTrue(expectedStatements.contains(
                        CRTestUtils.createStatement(subjects[i], predicates[0], objects[j])));
            }
        }

        filteredModel = model.filter(null, null, objects[0]);
        Assert.assertEquals(subjects.length * predicates.length, filteredModel.size());
        expectedStatements = new HashSet<Statement>(filteredModel);
        for (int i = 0; i < subjects.length; i++) {
            for (int j = 0; j < predicates.length; j++) {
                Assert.assertTrue(expectedStatements.contains(
                        CRTestUtils.createStatement(subjects[i], predicates[j], objects[0])));
            }
        }

        filteredModel = model.filter(subjects[0], predicates[0], objects[0]);
        Assert.assertEquals(1, filteredModel.size());
        expectedStatements = new HashSet<Statement>(filteredModel);
        Assert.assertTrue(expectedStatements.contains(CRTestUtils.createStatement(subjects[0], predicates[0], objects[0])));

        filteredModel = model.filter(otherSubject, null, null);
        Assert.assertTrue(filteredModel.isEmpty());
        
        filteredModel = model.filter(null, otherPredicate, null);
        Assert.assertTrue(filteredModel.isEmpty());
        
        filteredModel = model.filter(null, null, otherObject);
        Assert.assertTrue(filteredModel.isEmpty());
        
        filteredModel = model.filter(subjects[0], otherPredicate, null);
        Assert.assertTrue(filteredModel.isEmpty());
        
        filteredModel = model.filter(otherSubject, predicates[0], objects[0]);
        Assert.assertTrue(filteredModel.isEmpty());
        
        filteredModel = model.filter(subjects[0], otherPredicate, objects[0]);
        Assert.assertTrue(filteredModel.isEmpty());
    }
}
