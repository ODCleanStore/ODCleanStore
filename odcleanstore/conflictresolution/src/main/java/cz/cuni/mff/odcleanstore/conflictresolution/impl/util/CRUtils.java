/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionStrategyImpl;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.Map;

/**
 * Utility class for the Conflict Resolution component.
 * @author Jan Michelfeit
 */
public final class CRUtils {
    /**
     * If set to true, language tag is ignored when comparing literals.
     */
    private static final boolean IGNORE_LANGUAGE_TAG = true;
    
    /**
     * If set to true, literal datatype is ignored when comparing literals.
     */
    private static final boolean IGNORE_DATATPYE = true;
    
    /**
     * Comparison of node equality with regard to conflict resolution.
     * Behaves like {@link Value#equals(Object)} except that languages for plain string literal are not distinguished.
     * @param value1 first compared value
     * @param value2 second compared value
     * @return true if the two values are to be considered equal for conflict resolution
     */
    public static boolean sameValues(Value value1, Value value2) {
        return compareValues(value1, value2) == 0;
    }
    
    /**
     * Compares two datatype URIs for purposes of Conflict Resolution implementation.
     * @param datatype1 the first object to be compared.
     * @param datatype2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    public static int compareDatatypes(URI datatype1, URI datatype2) {
        if (datatype1 == datatype2) {
            return 0;
        } else if (datatype1 == null) {
            return -1;
        } else if (datatype2 == null) {
            return 1;
        } else {
            return datatype1.stringValue().compareTo(datatype2.stringValue());
        }
    }
    
    /**
     * Compares two {@link Value Values} for purposes of Conflict Resolution implementation.
     * @param value1 the first object to be compared.
     * @param value2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    public static int compareValues(Value value1, Value value2) {
        if (value1 == value2) {
            return 0;
        } else if (value1 == null) {
            return -1;
        } else if (value2 == null) {
            return 1;
        }

        // URIs
        boolean isURI1 = value1 instanceof URI;
        boolean isURI2 = value2 instanceof URI;
        if (isURI1 && isURI2) {
            return value1.stringValue().compareTo(value2.stringValue());
        } else if (isURI1) {
            return -1;
        } else if (isURI2) {
            return 1;
        }
        
        // Literals
        boolean isLiteral1 = value1 instanceof Literal;
        boolean isLiteral2 = value2 instanceof Literal;
        if (isLiteral1 && isLiteral2) {
           return compareLiteralsNonNull((Literal) value1, (Literal) value2);
        } else if (isLiteral1) {
            return -1;
        } else if (isLiteral2) {
            return 1;
        } 
        
        // BNodes
        return value1.stringValue().compareTo(value2.stringValue());
    }
    
    private static int compareLiteralsNonNull(Literal l1, Literal l2) {
        int lexicalComparison = l1.stringValue().compareTo(l2.stringValue());
        if (lexicalComparison != 0) {
            return lexicalComparison;
        }
        if (!IGNORE_DATATPYE) {
            int datatypeComparison = CRUtils.compareDatatypes(l1.getDatatype(), l2.getDatatype());
            if (datatypeComparison != 0) {
                return datatypeComparison;
            }
        }
        if (!IGNORE_LANGUAGE_TAG) {
            String language1 = l1.getLanguage();
            String language2 = l2.getLanguage();
            if (language1 == language2) {
                // skip
            } else if (language1 == null) {
                return -1;
            } else if (language2 == null) {
                return 1;
            } else {
                int languageComparison = language1.compareToIgnoreCase(language2);
                if (languageComparison != 0) {
                    return languageComparison;
                }
            }
        }
        return 0;
    }
    
    /**
     * Compares two quads (statements) for equality including named graphs (contexts).
     * @param statement1 first quad to compare
     * @param statement2 second quad to compare
     * @return true iff subjects, predicates, objects and contexts equal, respectively
     */
    public static boolean statementsEqual(Statement statement1, Statement statement2) {
        return sameValues(statement1.getObject(), statement2.getObject())
                && statement1.getSubject().equals(statement2.getSubject())
                && statement1.getPredicate().equals(statement2.getPredicate())
                && ODCSUtils.nullProofEquals(statement1.getContext(), statement2.getContext());
    }

    /**
     * Returns true if the given value is an untyped literal or xsd:string literal.
     * @param value value
     * @return true if the given value is an untyped literal or xsd:string literal
     */
    // private static boolean isPlainStringLiteral(Literal value) {
    // if (!(value instanceof Literal)) {
    // return false;
    // }
    // Literal literal = (Literal) value;
    // return literal.getDatatype() == null || literal.getDatatype().stringValue().equals(XMLSchema.stringType);
    // }
    
    /**
     * Returns a resolution strategy identical to <code>strategy</code> except that any missing values
     * in <code>strategy</code> are filled in from <code>defaultStrategy</code>.
     * @param strategy base resolution strategy
     * @param defaultStrategy default resolution strategy
     * @return conflict resolution strategy
     */
    public static ResolutionStrategy fillResolutionStrategyDefaults(ResolutionStrategy strategy,
            ResolutionStrategy defaultStrategy) {
        
        if (strategy == null) {
            return defaultStrategy;
        } else if (defaultStrategy == null) {
            return strategy;
        }

        String resolutionFunctionName;
        EnumCardinality cardinality;
        EnumAggregationErrorStrategy aggregationErrorStrategy;
        Map<String, String> params;
        URI dependsOn;

        if (strategy.getResolutionFunctionName() == null) {
            resolutionFunctionName = defaultStrategy.getResolutionFunctionName();
            params = defaultStrategy.getParams();
        } else {
            resolutionFunctionName = strategy.getResolutionFunctionName();
            params = strategy.getParams();
        }

        if (strategy.getCardinality() == null) {
            cardinality = defaultStrategy.getCardinality();
        } else {
            cardinality = strategy.getCardinality();
        }

        if (strategy.getAggregationErrorStrategy() == null) {
            aggregationErrorStrategy = defaultStrategy.getAggregationErrorStrategy();
        } else {
            aggregationErrorStrategy = strategy.getAggregationErrorStrategy();
        }

        if (strategy.getDependsOn() == null) {
            dependsOn = defaultStrategy.getDependsOn();
        } else {
            dependsOn = strategy.getDependsOn();
        }

        return new ResolutionStrategyImpl(
                resolutionFunctionName,
                cardinality,
                aggregationErrorStrategy,
                params,
                dependsOn);
    }
    
    private CRUtils() {
    }
}
