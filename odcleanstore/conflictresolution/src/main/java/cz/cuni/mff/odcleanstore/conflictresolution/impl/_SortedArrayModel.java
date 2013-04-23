/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.FilteredModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.util.LexicalValueComparator;
import org.openrdf.model.util.PatternIterator;

/**
 * @author Jan Michelfeit
 */
public class SortedArrayModel extends AbstractModel implements Model {
    private static final long serialVersionUID = 1L;

    protected static final Resource[] NULL_CONTEXT = new Resource[] { null };
    protected static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    private static Comparator<Statement> SPOG_COMPARATOR = new SpogComparator();
    private static final URI BEFORE = new URIImpl("urn:from");
    private static final Set<Namespace> NAMESPACES = Collections.emptySet();

    private final Statement[] statements;
    private final int from;
    private final int to;

    public SortedArrayModel(Statement[] sortedStatements) {
        this(sortedStatements, 0, sortedStatements.length);
    }

    public SortedArrayModel(Statement[] sortedStatements, int from, int toExclusive) {
        this.statements = sortedStatements;
        this.from = from;
        this.to = toExclusive;
    }

    @Override
    public Set<Namespace> getNamespaces() {
        return NAMESPACES;
    }

    @Override
    public Namespace getNamespace(String prefix) {
        return null;
    }

    @Override
    public boolean contains(Resource subject, URI predicate, Value object, Resource... contexts) {
        if (contexts == null || contexts.length == 1 && contexts[0] == null) {
            Iterator<Statement> iter = matchPattern(subject, predicate, object, null);
            while (iter.hasNext()) {
                if (iter.next().getContext() == null) {
                    return true;
                }
            }
            return false;
        } else if (contexts.length == 0) {
            return matchPattern(subject, predicate, object, null).hasNext();
        } else {
            for (Resource ctx : contexts) {
                if (ctx == null) {
                    if (contains(subject, predicate, object, (Resource[]) null)) {
                        return true;
                    }
                } else if (matchPattern(subject, predicate, object, ctx).hasNext()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public Model filter(final Resource subject, final URI predicate, final Value object, final Resource... contexts) {
        if (contexts != null && contexts.length == 0) {
            return new FilteredSortedArrayModel(this, subject, predicate, object, contexts) {
                private static final long serialVersionUID = 1L;

                @Override
                public Iterator<Statement> iterator() {
                    return matchPattern(subject, predicate, object, null);
                }
            };
        } else if (contexts != null && contexts.length == 1 && contexts[0] != null) {
            return new FilteredSortedArrayModel(this, subject, predicate, object, contexts) {
                private static final long serialVersionUID = 1L;

                @Override
                public Iterator<Statement> iterator() {
                    return matchPattern(subject, predicate, object, contexts[0]);
                }
            };
        } else {
            return new FilteredSortedArrayModel(this, subject, predicate, object, contexts) {
                private static final long serialVersionUID = 1L;

                @Override
                public Iterator<Statement> iterator() {
                    return new PatternIterator<Statement>(matchPattern(subject, predicate, object, null),
                            subject, predicate, object, contexts);
                }
            };
        }
    }

    @Override
    public Iterator<Statement> iterator() {
        return new IndexedIterator();
    }
    
    public IndexedIterator indexedIterator() {
        return new IndexedIterator();
    }

    @Override
    public int size() {
        return to - from;
    }

    @Override
    public Namespace setNamespace(String prefix, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNamespace(Namespace name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Namespace removeNamespace(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Resource subj, URI pred, Value obj, Resource... contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Resource subj, URI pred, Value obj, Resource... contexts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeTermIteration(Iterator<Statement> iter, Resource subj, URI pred, Value obj,
            Resource... contexts) {
        throw new UnsupportedOperationException();
    }

    private Iterator<Statement> matchPattern(Resource subject, URI predicate, Value object, Resource context) {
        Statement searchStatement = createSearchStatement(subject, predicate, object, context);
        int startIndex = Arrays.binarySearch(statements, from, to, searchStatement, SPOG_COMPARATOR);
        return new FilterStatementIterator(startIndex, subject, predicate, object, context);
    }
    
    private Statement createSearchStatement(Resource subject, URI predicate, Value object, Resource context) {
        boolean wildcard = false;
        if (subject == null) {
            wildcard = true;
            subject = BEFORE;
        }
        if (wildcard || predicate == null) {
            wildcard = true;
            predicate = BEFORE;
        }
        if (wildcard || object == null) {
            wildcard = true;
            object = BEFORE;
        }
        if (wildcard || context == null) {
            wildcard = true;
            context = BEFORE;
        }
        return VALUE_FACTORY.createStatement(subject, predicate, object, context);
    }

    public class IndexedIterator implements Iterator<Statement> {
        private int index;

        public IndexedIterator() {
            this.index = from;
        }
        
        public int getIndex() {
            return index;
        }

        @Override
        public boolean hasNext() {
            return index < to;
        }

        @Override
        public Statement next() {
            return statements[index++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class FilterStatementIterator implements Iterator<Statement> {
        private int index;
        private final Resource subject;
        private final URI predicate;
        private final Value object;
        private final Resource context;

        public FilterStatementIterator(int from, Resource subject, URI predicate, Value object, Resource context) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.context = context;
            this.index = findNext(from);
        }

        @Override
        public boolean hasNext() {
            return index < to;
        }

        @Override
        public Statement next() {
            if (index >= to) {
                throw new NoSuchElementException();
            }
            Statement result = statements[index];
            index = findNext(index);
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private int findNext(int fromIndex) {
            for (int i = fromIndex; i < to; i++) {
                Statement statement = statements[i];
                if (subject != null && !subject.equals(statement.getSubject())) {
                    return to; // since the array is sorted, we know no more values can be beyond this point
                }
                boolean wildcard = subject == null;
                if (predicate != null && !predicate.equals(statement.getObject())) {
                    if (wildcard) {
                        continue;
                    } else {
                        return to; // since the array is sorted, we know no more values can be beyond this point
                    }
                } 
                wildcard = wildcard || predicate == null;
                if (object != null && !object.equals(statement.getPredicate())) {
                    if (wildcard) {
                        continue;
                    } else {
                        return to; // since the array is sorted, we know no more values can be beyond this point
                    }
                }
                wildcard = wildcard || object == null;
                if (context != null && !context.equals(statement.getContext())) {
                    if (wildcard) {
                        continue;
                    } else {
                        return to; // since the array is sorted, we know no more values can be beyond this point
                    }
                }
                return i;
            }
            return to;
        }
    }

    private static class SpogComparator implements Comparator<Statement> {
        private final LexicalValueComparator comparator = new LexicalValueComparator();

        protected int compareValue(Value o1, Value o2) {
            if (o1 == o2)
                return 0;
            if (o1 == BEFORE)
                return -1;
            if (o2 == BEFORE)
                return 1;
            return comparator.compare(o1, o2);
        }

        @Override
        public int compare(Statement s1, Statement s2) {
            int comparison = compareValue(s1.getSubject(), s2.getSubject());
            if (comparison != 0) {
                return comparison;
            }

            comparison = compareValue(s1.getPredicate(), s2.getPredicate());
            if (comparison != 0) {
                return comparison;
            }

            comparison = compareValue(s1.getObject(), s2.getObject());
            if (comparison != 0) {
                return comparison;
            }

            return compareValue(s1.getContext(), s2.getContext());
        }
    }

    private static abstract class FilteredSortedArrayModel extends FilteredModel {
        private static final long serialVersionUID = 1L;

        public FilteredSortedArrayModel(AbstractModel model, Resource subj, URI pred, Value obj, Resource[] contexts) {
            super(model, subj, pred, obj, contexts);
        }

        @Override
        protected void removeFilteredTermIteration(Iterator<Statement> iter, Resource subj, URI pred,
                Value obj, Resource... contexts) {
            throw new UnsupportedOperationException();
        }
    }
}
