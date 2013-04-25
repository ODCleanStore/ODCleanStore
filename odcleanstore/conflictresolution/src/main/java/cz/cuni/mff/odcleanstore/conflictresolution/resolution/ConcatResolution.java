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
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;

/**
 * @author Jan Michelfeit
 */
public class ConcatResolution extends MediatingResolutionFunction {
    public static final String SEPARATOR_PARAM_NAME = "separator";
    public static final String DEFAULT_SEPARATOR = "; ";
    
    
    protected ConcatResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
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
        double confidence = getConfidence(object, statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                firstStatement.getSubject(),
                firstStatement.getPredicate(),
                object,
                confidence,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
