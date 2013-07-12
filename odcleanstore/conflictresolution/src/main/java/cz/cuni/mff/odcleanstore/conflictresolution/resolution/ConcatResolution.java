/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;


/**
 * Returns a statement whose object is a concatenation of all object values present in the quads to be resolved.
 * The separator is given in optional
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
 * {@value #SEPARATOR_PARAM_NAME}.
 * @author Jan Michelfeit
 */
public class ConcatResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "CONCAT";
    
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
     * Name of the {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
     * specifying the separator of concatenated values.
     */
    public static final String SEPARATOR_PARAM_NAME = "separator";
    
    /** Default separator of concatenated values. */
    public static final String DEFAULT_SEPARATOR = "; ";
    
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public ConcatResolution(MediatingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        String separator = crContext.getResolutionStrategy().getParams().get(SEPARATOR_PARAM_NAME);
        if (separator == null) {
            separator = DEFAULT_SEPARATOR;
        }
        
        StringBuilder resultValue = new StringBuilder();
        boolean first = true;
        for (Statement statement : statements) {
            if (!first) {
                resultValue.append(separator);
            }
            first = false;
            resultValue.append(statement.getObject().stringValue());
        }
        
        Value object = crContext.getResolvedStatementFactory().getValueFactory().createLiteral(resultValue.toString());
        Statement firstStatement = statements.iterator().next();
        Set<Resource> sources = getAllSources(statements);
        double fQuality = getFQuality(object, statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                firstStatement.getSubject(),
                firstStatement.getPredicate(),
                object,
                fQuality,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
