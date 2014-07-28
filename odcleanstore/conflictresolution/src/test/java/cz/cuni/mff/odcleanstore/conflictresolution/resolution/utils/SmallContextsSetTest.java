package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class SmallContextsSetTest {

    private static final ValueFactoryImpl VF = ValueFactoryImpl.getInstance();
    private static final URI SAMPLE_URI = VF.createURI("http://a");

    @Test
    public void createsEmptySet() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Collections.<Statement>emptyIterator());
        assertThat(result, is(Collections.<Resource>emptySet()));
        assertThat(result, not(instanceOf(HashSet.class)));
    }

    @Test
    public void createsEmptySetFromNullContexts() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Arrays.asList(
                createStatement(null),
                createStatement(null)
        ).iterator());
        assertThat(result, is(Collections.<Resource>emptySet()));
        assertThat(result, not(instanceOf(HashSet.class)));
    }

    @Test
        public void createsSingletonSet() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Arrays.asList(
                createStatement("a"),
                createStatement("a")
        ).iterator());
        Set<Resource> expectedResult = new HashSet<Resource>(Arrays.asList(createContext("a")));
        assertThat(result, is(expectedResult));
        assertThat(result, not(instanceOf(HashSet.class)));
    }

    @Test
    public void createsSingletonSetWithNullContexts() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Arrays.asList(
                createStatement("a"),
                createStatement(null),
                createStatement("a"),
                createStatement(null)
        ).iterator());
        Set<Resource> expectedResult = new HashSet<Resource>(Arrays.asList(createContext("a")));
        assertThat(result, is(expectedResult));
        assertThat(result, not(instanceOf(HashSet.class)));
    }

    @Test
    public void createsLargerSet() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Arrays.asList(
                createStatement("a"),
                createStatement("a"),
                createStatement("b"),
                createStatement("c"),
                createStatement("b")
        ).iterator());
        Set<Resource> expectedResult = new HashSet<Resource>(Arrays.asList(
                createContext("a"),
                createContext("b"),
                createContext("c")));
        assertThat(result, is(expectedResult));
    }

    @Test
    public void createsLargerSetWithNullContexts() throws Exception {
        Set<Resource> result = SmallContextsSet.fromIterator(Arrays.asList(
                createStatement("a"),
                createStatement(null),
                createStatement("b"),
                createStatement("c"),
                createStatement("b")
        ).iterator());
        Set<Resource> expectedResult = new HashSet<Resource>(Arrays.asList(
                createContext("a"),
                createContext("b"),
                createContext("c")));
        assertThat(result, is(expectedResult));
    }

    private Statement createStatement(String contextPart) {
        return VF.createStatement(SAMPLE_URI, SAMPLE_URI, SAMPLE_URI, createContext(contextPart));
    }

    private Resource createContext(String contextPart) {
        return contextPart == null ? null : VF.createURI("http://", contextPart);
    }
}
