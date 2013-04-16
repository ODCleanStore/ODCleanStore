package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Comparator;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Comparator of two {@link Value Values}.
 * The comparison can be used to sort {@link Statement Statements}; equal statements are guaranteed to return 0,
 * however the implementation gives no promises about the exact order of Nodes.
 *
 * @author Jan Michelfeit
 */
public final class ValueComparator implements Comparator<Value> {
    private static final ValueComparator INSTANCE = new ValueComparator();
    
    /**
     * Compares two {@link Value} instances.
     * The comparison can be used to sort {@link Statement triples}, equal nodes
     * are guaranteed to return 0. No guarantees about the order of other nodes are given.
     *
     * @param v1 the first object to be compared.
     * @param v2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the second.
     *  
     */
    @Override
    public int compare(Value v1, Value v2) {
        // TODO: check correctness of conversion to Sesame
        assert v1 != null;
        assert v2 != null;

        if (v1.equals(v2)) {
            return 0;
        } else if (v1.getClass() != v2.getClass()) {
            // compare by classes somehow, e.g. by class names
            return v1.getClass().getName().compareTo(v2.getClass().getName());
        } else {
            // A little optimization: for all but literal nodes comparison using toString() method
            // behaves exactly like using ComparisonVisitor; literals are compared by their lexical
            // form in the first place. Removing the following if-else block doesn't change the
            // behavior of the algorithm but we can avoid creating unnecessary ComparisonVisitor
            // instances.
            if (v1 instanceof Literal) {
                Literal literal1 = (Literal) v1;
                Literal literal2 = (Literal) v2;
                int lexicalComparison = v1.stringValue().compareTo(v2.stringValue());
                if (lexicalComparison != 0) {
                    return lexicalComparison;
                }
                String dataType1 = ODCSUtils.valueToString(literal1.getDatatype());
                String dataType2 = ODCSUtils.valueToString(literal2.getDatatype());
                int dataTypeComparison = ODCSUtils.nullProofCompare(dataType1, dataType2);
                if (dataTypeComparison != 0) {
                    return dataTypeComparison;
                }
                // language() is guaranteed not to be null
                return ODCSUtils.nullProofCompareIgnoreCase(literal1.getLanguage(), literal2.getLanguage());
            } else {
                return v1.toString().compareTo(v2.toString());
            }

            /*ComparisonVisitor comparisonVisitor = new ComparisonVisitor(n1);
            return (Integer) n2.visitWith(comparisonVisitor);*/
        }
    }

    /** Disable constructor for a utility class. */
    private ValueComparator() {
    }
    
    /**
     * Returns instance of the comparator
     * @return {@link ValueComparator} instance
     */
    public static Comparator<Value> getInstance() {
        return INSTANCE;
    }
}
