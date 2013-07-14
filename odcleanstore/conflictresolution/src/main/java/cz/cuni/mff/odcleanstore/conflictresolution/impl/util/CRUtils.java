/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionStrategyImpl;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

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
     * Comparison of node equality with regard to conflict resolution.
     * Behaves like {@link Value#equals(Object)} except that languages for plain string literal are not distinguished.
     * @param value1 first compared value
     * @param value2 second compared value
     * @return true if the two values are to be considered equal for conflict resolution
     */
    public static boolean sameValues(Value value1, Value value2) {
        if (IGNORE_LANGUAGE_TAG) {
            if (value1 == value2) {
                return true;
            } else if (value1 == null || value2 == null) {
                return false;
            } else if (value1 instanceof Literal && value2 instanceof Literal && isPlainStringLiteral((Literal) value1)
                    && isPlainStringLiteral((Literal) value2)) {
                String lex1 = value1.stringValue();
                String lex2 = value2.stringValue();
                return lex1.equals(lex2); // intentionally not comparing language
            } else {
                return value1.equals(value2);
            }
        } else {
            return value1.equals(value2);
        }
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
    private static boolean isPlainStringLiteral(Literal value) {
        if (!(value instanceof Literal)) {
            return false;
        }
        Literal literal = (Literal) value;
        return literal.getDatatype() == null || literal.getDatatype().stringValue().equals(XMLSchema.stringType);
    }
    
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
        return new ResolutionStrategyImpl(
                resolutionFunctionName,
                cardinality,
                aggregationErrorStrategy,
                params);
    }
    
    private CRUtils() {
    }
}
