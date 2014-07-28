/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.SmallContextsSet;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Returns empty result if multiple distinct objects are present in data to be resolved,
 * otherwise returns the single quad that all sources agree on.
 * @author Jan Michelfeit
 */
public class CertainResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "CERTAIN";
    
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
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public CertainResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
     
        Iterator<Statement> statementIt = statements.iterator();
        Statement firstStatement = statementIt.next();
        Value firstObject = firstStatement.getObject();
        int statementCount = 1;
        while (statementIt.hasNext()) {
            Statement statement = statementIt.next();
            if (!CRUtils.sameValues(statement.getObject(), firstObject)) {
                return Collections.emptySet();
            }
            statementCount++;
        }
        
        Set<Resource> sources = SmallContextsSet.fromIterator(statements.iterator());
        
        double fQuality = getFQuality(firstObject, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                firstStatement.getSubject(),
                firstStatement.getPredicate(),
                firstStatement.getObject(),
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
