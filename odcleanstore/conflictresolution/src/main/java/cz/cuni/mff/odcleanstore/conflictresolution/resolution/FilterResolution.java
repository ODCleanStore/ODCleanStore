/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedLiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import org.openrdf.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Resolution function returning quads whose object is in the given range of minimum and maximum values.
 * The minimum and maximum is given in optional
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameters} 
 * {@value #MAX_PARAM_NAME} and {@value #MIN_PARAM_NAME}.
 * @author Jan Michelfeit
 */
public class FilterResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "FILTER";
    
    /**
     * Returns a string identifier of this resolution function ({@value #FUNCTION_NAME}) - can be used to 
     * retrieve the resolution function from the default initialized 
     * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry}.
     * @see cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory#createInitializedResolutionFunctionRegistry()
     * @return string identifier of this resolution function
     */
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final String MAX_PARAM_NAME = "max";
    private static final String MIN_PARAM_NAME = "min";
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public FilterResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) { 
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        ValueFactory valueFactory = crContext.getResolvedStatementFactory().getValueFactory();

        String maxParam = crContext.getResolutionStrategy().getParams().get(MAX_PARAM_NAME);
        Value max = maxParam != null ? valueFactory.createLiteral(maxParam) : null;
        String minParam = crContext.getResolutionStrategy().getParams().get(MIN_PARAM_NAME);
        Value min = minParam != null ? valueFactory.createLiteral(minParam) : null;

        Collection<Statement> sortedStatements = statements;
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            if (!(statement.getObject() instanceof Literal)) {
                handleNonAggregableStatement(statement, statements, crContext, result);
                continue;
            }

            EnumLiteralType literalType = ResolutionFunctionUtils.getLiteralType((Literal) statement.getObject());
            BestSelectedLiteralComparator comparator = LiteralComparatorFactory.getComparator(literalType);
            if (min != null && comparator.compare(statement.getObject(), min, crContext) < 0) {
                continue; // less than minimum, filter out
            }
            if (max != null && comparator.compare(statement.getObject(), max, crContext) > 0) {
                continue; // more than maximum, filter out
            }
            Collection<Resource> sources = it.peekSources();
            addResolvedStatement(statement, sources, statements, crContext, result);
        }

        return result;
    }

    private void addResolvedStatement(Statement statement, Collection<Resource> sources, Collection<Statement> statements,
            CRContext crContext, Collection<ResolvedStatement> result) {
        result.add(crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject(),
                getFQuality(statement.getObject(), sources, crContext),
                sources));
    }
}
