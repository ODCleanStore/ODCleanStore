/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.SpogComparator;

/**
 * Provides view on the given statements as a collection of conflict clusters or as an RDF model.
 * Conflict cluster is a set of quads sharing the same subject and predicate.
 * Each conflict cluster is represented as a list of {@link Statement quads}.
 * 
 * URI mapping is applied to the statements wrapped by this object, they are sorted, and duplicities removed
 * in order to identify conflict clusters.
 * @author Jan Michelfeit
 */
public class ConflictClustersCollection extends AbstractCollection<List<Statement>> implements Collection<List<Statement>> {
    private static final Comparator<Statement> ORDER_COMPARATOR = new SpogComparator();

    private final Statement[] statements;
    private final URIMapping uriMapping;
    private final ValueFactory valueFactory;
    private int size;
    private boolean isInitialized = false;
    private Model model;

    /**
     * Creates a new instance wrapping the given statements.
     * NOTE that the statements array will be modified!
     * @param statements array of RDF quads; this array will be modified when methods are called on this object!
     *        (URI mapping is applied, statements sorted and duplicities removed)
     * @param uriMapping mapping of URIs to their canonical URI
     * @param valueFactory a {@link ValueFactory} instance
     */
    public ConflictClustersCollection(Statement[] statements, URIMapping uriMapping, ValueFactory valueFactory) {
        this.statements = statements;
        this.valueFactory = valueFactory;
        this.uriMapping = uriMapping;
        this.size = statements.length;
    }

    /**
     * Returns number of unique wrapped quads after application of URI mapping.
     * @return number of unique wrapped quads after application of URI mapping
     */
    @Override
    public int size() {
        if (!isInitialized) {
            initialize();
        }
        return size;
    }

    /**
     * Returns an iterator over conflict clusters.
     * Each conflict cluster is represented as a list of {@link Statement quads}; quads
     * in the list are guaranteed to be spog-sorted.
     * @return iterator over conflict clusters
     */
    @Override
    public Iterator<List<Statement>> iterator() {
        if (!isInitialized) {
            initialize();
        }
        return new ConflictClusterIterator();
    }

    /**
     * Provides a view on the wrapped statements as a {@link Model}.
     * @return an RDF model backed by the statements wrapped by this object
     */
    public Model asModel() {
        if (!isInitialized) {
            initialize();
        }
        if (model == null) {
            model = new SortedListModel(new SubList(0, size));
        }
        return model;
    }

    private void initialize() {
        if (!isInitialized) {
            applyUriMapping();
            sort();
            makeUnique();
            isInitialized = true;
        }
    }

    /** Apply URI mapping - translates all URIs occurring in the wrapped statements to their canonical equivalent. */
    protected void applyUriMapping() {
        int statementCount = statements.length;
        for (int i = 0; i < statementCount; i++) {
            Statement statement = statements[i];

            Resource subject = statement.getSubject();
            Resource subjectMapping = (Resource) mapURINode(subject, uriMapping);
            URI predicate = statement.getPredicate();
            URI predicateMapping = (URI) mapURINode(predicate, uriMapping);
            Value object = statement.getObject();
            Value objectMapping = mapURINode(object, uriMapping);

            // Intentionally !=
            if (subject != subjectMapping
                    || predicate != predicateMapping
                    || object != objectMapping) {
                Statement newStatement = valueFactory.createStatement(
                        subjectMapping,
                        predicateMapping,
                        objectMapping,
                        statement.getContext());
                statements[i] = newStatement;
            }
        }
    }

    /**
     * If mapping contains an URI to map for the passed {@link URI} returns a {@link URI} with the mapped URI, otherwise returns
     * <code>value</code>.
     * @param value a {@link Value} to apply mapping to
     * @param uriMapping an URI mapping to apply
     * @return node with applied URI mapping
     */
    protected final Value mapURINode(Value value, URIMapping uriMapping) {
        if (value instanceof URI) {
            return uriMapping.mapURI((URI) value);
        }
        return value;
    }

    private void sort() {
        Arrays.sort(statements, ORDER_COMPARATOR);
    }

    /** Removes identical quads. Expects the quads to be spog-sorted in advance. */
    protected void makeUnique() {
        if (statements.length == 0) {
            // Must be handled as a special case, otherwise this.size gets initialized to 1
            this.size = 0;
            return;
        }

        int lastIdx = 0;
        for (int currIdx = 1; currIdx < statements.length; currIdx++) { // intentionally start from 1
            Statement previous = statements[lastIdx];
            Statement current = statements[currIdx];
            if (!CRUtils.statementsEqual(current, previous)) {
                lastIdx++;
                statements[lastIdx] = statements[currIdx];
            }
        }

        // Update size - we may use less of the underlying array now
        this.size = lastIdx + 1;

        for (int i = this.size; i < statements.length; i++) {
            statements[i] = null; // release for GC
        }
    }

    /**
     * Sublist of wrapped statements determined by start and end index into {@link ConflictClustersCollection#statements}
     * representing one conflict cluster.
     */
    protected class SubList extends AbstractList<Statement> implements RandomAccess {
        private int from;
        private int to;

        /**
         * @param from index of the first item represented by this sublist into {@link ConflictClustersCollection#statements}.
         * @param toExclusive index past the last item represented by this sublist into 
         *      {@link ConflictClustersCollection#statements}.
         */
        SubList(int from, int toExclusive) {
            this.from = from;
            this.to = toExclusive;
        }

        @Override
        public Statement get(int index) {
            // if (from + index >= to) {
            // throw new IndexOutOfBoundsException();
            // }
            return statements[from + index];
        }

        @Override
        public int size() {
            return to - from;
        }

        @Override
        public int indexOf(Object obj) {
            if (obj == null) {
                for (int i = from; i < to; i++) {
                    if (statements[i] == null) {
                        return i;
                    }
                }
            } else {
                for (int i = from; i < to; i++) {
                    if (obj.equals(statements[i])) {
                        return i;
                    }
                }
            }
            return -1;
        }

        @Override
        public boolean contains(Object obj) {
            return indexOf(obj) != -1;
        }
    }

    /**
     * Iterator over conflict clusters backed by {@link ConflictClustersCollection#statements}.
     * Expects {@link ConflictClustersCollection#statements} to be sp-sorted.
     */
    protected class ConflictClusterIterator implements Iterator<List<Statement>> {
        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return hasNextStatement();
        }

        @Override
        public List<Statement> next() {
            int fromIndex = cursor;
            Statement first = nextStatement();
            while (hasNextStatement()) {
                Statement next = nextStatement();
                if (!first.getPredicate().equals(next.getPredicate()) || !first.getSubject().equals(next.getSubject())) {
                    // We reached the next cluster of conflicting quads
                    // -> move back so that cursor points to the first statement from the next cluster
                    moveBack();
                    break;
                }
            }
            return new SubList(fromIndex, cursor);
        }

        private boolean hasNextStatement() {
            return cursor < size;
        }

        private Statement nextStatement() {
            return statements[cursor++];
        }

        private void moveBack() {
            cursor--;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Call to remove() is not supported.");
        }
    }

}
