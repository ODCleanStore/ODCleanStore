/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.MockURIMapping;

public class ConflictClustersCollectionTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    @Before
    public void resetURICounter() {
        CRTestUtils.resetURICounter();
    }

    @Test
    public void testURIMapping() {
        Statement statement1 = CRTestUtils.createStatement();
        Statement statement2 = CRTestUtils.createStatement();
        Statement mappedStatement = CRTestUtils.createStatement();

        Map<URI, URI> mapping = new HashMap<URI, URI>();
        mapping.put((URI) statement1.getSubject(), (URI) mappedStatement.getSubject());
        mapping.put((URI) statement1.getPredicate(), (URI) mappedStatement.getPredicate());
        mapping.put((URI) statement1.getObject(), (URI) mappedStatement.getObject());
        mapping.put((URI) statement2.getSubject(), (URI) mappedStatement.getSubject());
        mapping.put((URI) statement2.getPredicate(), (URI) mappedStatement.getPredicate());
        mapping.put((URI) statement2.getObject(), (URI) mappedStatement.getObject());

        Statement[] statements = new Statement[] { statement1, statement2 };
        ConflictClustersCollection ccCollection = new ConflictClustersCollection(
                statements, new MockURIMapping(mapping), VALUE_FACTORY);

        // Size should be two because object differ in named graphs
        Assert.assertEquals(2, ccCollection.size());

        // Only one conflict cluster
        Assert.assertEquals(1, CRTestUtils.countIteratorItems(ccCollection.iterator()));

        // Both statements mapped to mappedStatement
        List<Statement> cluster = ccCollection.iterator().next();
        Assert.assertEquals(mappedStatement, cluster.get(0));
        Assert.assertEquals(mappedStatement, cluster.get(1));
    }

    @Test
    public void testDeduplication() {
        Statement statement1 = CRTestUtils.createStatement();
        Statement statement2 = CRTestUtils.createStatement(
                CRTestUtils.getUniqueURIString(),
                CRTestUtils.getUniqueURIString(),
                CRTestUtils.getUniqueURIString(),
                statement1.getContext().stringValue());
        Statement mappedStatement = CRTestUtils.createStatement();

        Map<URI, URI> mapping = new HashMap<URI, URI>();
        mapping.put((URI) statement1.getSubject(), (URI) statement2.getSubject());
        mapping.put((URI) statement1.getPredicate(), (URI) statement2.getPredicate());
        mapping.put((URI) statement1.getObject(), (URI) statement2.getObject());

        Statement[] statements = new Statement[] { statement1, statement2 };
        ConflictClustersCollection ccCollection = new ConflictClustersCollection(
                statements, new MockURIMapping(mapping), VALUE_FACTORY);

        // One statement should be filtered out because they map to same URIs and share the same context.
        Assert.assertEquals(1, ccCollection.size());
    }

    @Test
    public void testClusters() {
        String subject1 = CRTestUtils.getUniqueURIString();
        String subject2 = CRTestUtils.getUniqueURIString();
        String predicate1 = CRTestUtils.getUniqueURIString();
        String predicate2 = CRTestUtils.getUniqueURIString();
        String object = CRTestUtils.getUniqueURIString();

        Statement statement11a = CRTestUtils.createStatement(subject1, predicate1, object);
        Statement statement11b = CRTestUtils.createStatement(subject1, predicate1, object);
        Statement statement12a = CRTestUtils.createStatement(subject1, predicate2, object);
        Statement statement12b = CRTestUtils.createStatement(subject1, predicate2, object);
        Statement statement21a = CRTestUtils.createStatement(subject2, predicate1, object);
        Statement statement21b = CRTestUtils.createStatement(subject2, predicate1, object);
        // Clusters: 11a, 11b; 12a, 12b; 21a, 21b

        ArrayList<Statement> statements = new ArrayList<Statement>();
        statements.add(statement11a);
        statements.add(statement12a);
        statements.add(statement21b);
        statements.add(statement12b);
        statements.add(statement21a);
        statements.add(statement11b);

        ConflictClustersCollection ccCollection = new ConflictClustersCollection(
                statements.toArray(new Statement[0]), new EmptyURIMapping(), VALUE_FACTORY);

        // Number of unique triples
        Assert.assertEquals(6, ccCollection.size());

        // Number of conflict clusters
        Assert.assertEquals(3, CRTestUtils.countIteratorItems(ccCollection.iterator()));

        Iterator<List<Statement>> it = ccCollection.iterator();
        while (it.hasNext()) {
            List<Statement> cluster = it.next();
            Assert.assertEquals(2, cluster.size());
            Statement st1 = cluster.get(0);
            Statement st2 = cluster.get(1);
            // Verify we indeed have a conflict cluster
            Assert.assertEquals(st1.getSubject(), st2.getSubject());
            Assert.assertEquals(st1.getPredicate(), st2.getPredicate());
            Assert.assertEquals(st1.getObject(), st2.getObject());
            Assert.assertFalse(st1.getContext().equals(st2.getContext()));
        }
    }
    
    @Test
    public void testModelView() {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        statements.add(CRTestUtils.createStatement());
        statements.add(CRTestUtils.createStatement());
        statements.add(CRTestUtils.createStatement());
        
        ConflictClustersCollection ccCollection = new ConflictClustersCollection(
                statements.toArray(new Statement[0]), new EmptyURIMapping(), VALUE_FACTORY);
        Model model = ccCollection.asModel();
        
        Assert.assertEquals(statements.size(), model.size());
        
        for (Statement st : statements) {
            Assert.assertEquals(1, model.filter(st.getSubject(), null, null).size());
            Assert.assertEquals(1, model.filter(null, st.getPredicate(), null).size());
            Assert.assertEquals(1, model.filter(null, null, st.getObject()).size());
            
            Assert.assertTrue(model.filter(st.getSubject(), null, null).contains(st));
            Assert.assertEquals(st, model.filter(st.getSubject(), null, null).iterator().next());
        }
        
        
    }

}
