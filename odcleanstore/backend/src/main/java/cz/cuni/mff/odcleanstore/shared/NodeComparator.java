package cz.cuni.mff.odcleanstore.shared;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeVisitor;
import com.hp.hpl.jena.graph.Node_ANY;
import com.hp.hpl.jena.graph.Node_Blank;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.graph.Node_Variable;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.AnonId;

/**
 * Comparator of two {@link Node Nodes}.
 * The comparison can be used to sort Triples; equal triples are guaranteed to return 0,
 * however the implementation gives no promises about the exact order of Nodes.
 *
 * Note: in order to be consistent with equals(), we use {@link Node#equals(Object)} rather than
 * more suitable {@link Node#sameValueAs(Object)}.
 *
 * @author Jan Michelfeit
 */
public final class NodeComparator {
    /**
     * Implementation of the Visitor pattern that compares a node given in the constructor to the
     * visited node.
     */
    @SuppressWarnings("unused")
    private static class ComparisonVisitor implements NodeVisitor {
        /** A node being compared to the visited node. */
        private Node comparedNode;

        /**
         * Converts null string to "".
         * @param s string
         * @return s or the empty string
         */
        private static String nullStringAsEmpty(String s) {
            return (s == null) ? "" : s;
        }

        /**
         * Initializes ComparisonVisitor with a compared node.
         * @param comparedNode a node being compared to the visited node
         */
        public ComparisonVisitor(Node comparedNode) {
            this.comparedNode = comparedNode;
        }

        @Override
        public Object visitAny(Node_ANY other) {
            assert comparedNode.getClass() == Node_ANY.class;
            return 0;
        }

        @Override
        public Object visitBlank(Node_Blank other, AnonId id) {
            assert comparedNode.getClass() == Node_Blank.class;
            return comparedNode.getBlankNodeId().toString().compareTo(id.toString());
        }

        @Override
        public Object visitLiteral(Node_Literal other, LiteralLabel otherLiteral) {
            assert comparedNode.getClass() == Node_Literal.class;
            LiteralLabel comparedLiteral = comparedNode.getLiteral();
            int lexicalComparison = comparedLiteral.getLexicalForm().compareTo(
                    otherLiteral.getLexicalForm());
            if (lexicalComparison != 0) {
                return lexicalComparison;
            }

            String comparedDataType = nullStringAsEmpty(comparedLiteral.getDatatypeURI());
            String otherDataType = nullStringAsEmpty(otherLiteral.getDatatypeURI());
            int dataTypeComparison = comparedDataType.compareTo(otherDataType);
            if (dataTypeComparison != 0) {
                return dataTypeComparison;
            }

            // language() is guaranteed not to be null
            return comparedLiteral.language().compareToIgnoreCase(otherLiteral.language());
        }

        @Override
        public Object visitURI(Node_URI it, String uri) {
            assert comparedNode.getClass() == Node_URI.class;
            return comparedNode.getURI().compareTo(uri);
        }

        @Override
        public Object visitVariable(Node_Variable it, String name) {
            assert comparedNode.getClass() == Node_Variable.class;
            return comparedNode.getName().compareTo(name);
        }
    }

    /**
     * Compares two {@link Node} instances.
     * The comparison can be used to sort {@link com.hp.hpl.jena.graph.Triple triples}, equal nodes
     * are guaranteed to return 0. No guarantees about the order of other nodes are given.
     *
     * @param n1 the first object to be compared.
     * @param n2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the second.
     */
    public static int compare(Node n1, Node n2) {
        assert n1 != null;
        assert n2 != null;

        if (n1.equals(n2)) {
            return 0;
        } else if (n1.getClass() != n2.getClass()) {
            // compare by classes somehow, e.g. by class names
            return n1.getClass().getName().compareTo(n2.getClass().getName());
        } else {
            // A little optimization: for all but literal nodes comparison using toString() method
            // behaves exactly like using ComparisonVisitor; literals are compared by their lexical
            // form in the first place. Removing the following if-else block doesn't change the
            // behavior of the algorithm but we can avoid creating unnecessary ComparisonVisitor
            // instances.
            if (n1 instanceof Node_Literal) {
                LiteralLabel literal1 = n1.getLiteral();
                LiteralLabel literal2 = n2.getLiteral();
                int lexicalComparison = literal1.getLexicalForm().compareTo(literal2.getLexicalForm());
                if (lexicalComparison != 0) {
                    return lexicalComparison;
                }
                String dataType1 = literal1.getDatatypeURI();
                String dataType2 = literal2.getDatatypeURI();
                if (dataType1 == null) {
                    dataType1 = "";
                }
                if (dataType2 == null) {
                    dataType2 = "";
                }
                int dataTypeComparison = dataType1.compareTo(dataType2);
                if (dataTypeComparison != 0) {
                    return dataTypeComparison;
                }
                // language() is guaranteed not to be null
                return literal1.language().compareToIgnoreCase(literal2.language());
            } else {
                return n1.toString().compareTo(n2.toString());
            }

            /*ComparisonVisitor comparisonVisitor = new ComparisonVisitor(n1);
            return (Integer) n2.visitWith(comparisonVisitor);*/
        }
    }

    /** Disable constructor for a utility class. */
    private NodeComparator() {
    }
}
