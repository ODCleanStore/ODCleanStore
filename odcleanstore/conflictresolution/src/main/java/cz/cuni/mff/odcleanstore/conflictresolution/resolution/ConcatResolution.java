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
 * @author Jan Michelfeit
 */
public class ConcatResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "CONCAT";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public static final String SEPARATOR_PARAM_NAME = "separator";
    public static final String DEFAULT_SEPARATOR = "; ";
    
    
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
