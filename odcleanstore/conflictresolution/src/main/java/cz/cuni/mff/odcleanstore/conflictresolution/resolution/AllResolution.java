/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Implementation of the ALL resolution function - returns all input values.
 * Only quads differing only in their named graph are grouped together.
 * @author Jan Michelfeit
 */
public class AllResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "ALL";
    
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
    public AllResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<Statement> sortedStatements = statements;
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
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
