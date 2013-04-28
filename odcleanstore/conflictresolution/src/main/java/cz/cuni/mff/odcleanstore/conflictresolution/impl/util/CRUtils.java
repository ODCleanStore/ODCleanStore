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
    
    public static ResolutionStrategy mergeresolutionStrategies(ResolutionStrategy baseStrategy, ResolutionStrategy mergedStrategy) {
        if (baseStrategy == null) {
            return mergedStrategy;
        } else if (mergedStrategy == null) {
            return baseStrategy;
        }
        String resolutionFunctionName = baseStrategy.getResolutionFunctionName() != null
                ? baseStrategy.getResolutionFunctionName()
                : mergedStrategy.getResolutionFunctionName();
        EnumCardinality cardinality = baseStrategy.getCardinality() != null
                ? baseStrategy.getCardinality()
                : mergedStrategy.getCardinality();
        EnumAggregationErrorStrategy errorStrategy = baseStrategy.getAggregationErrorStrategy() != null
                ? baseStrategy.getAggregationErrorStrategy()
                : mergedStrategy.getAggregationErrorStrategy();
        Map<String, String> params = baseStrategy.getParams() != null || baseStrategy.getParams().isEmpty()
                ? baseStrategy.getParams()
                : mergedStrategy.getParams();
        return new ResolutionStrategyImpl(resolutionFunctionName, cardinality, errorStrategy, params);
    }

    private CRUtils() {
    }
}
