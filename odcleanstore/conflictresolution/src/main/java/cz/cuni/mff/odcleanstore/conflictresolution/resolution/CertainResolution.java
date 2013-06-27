/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;

/**
 * @author Jan Michelfeit
 */
public class CertainResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "CERTAIN";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public CertainResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
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
        
        HashSet<Resource> sources = new HashSet<Resource>(statementCount); 
        for (Statement statement : statements) {
            sources.add(statement.getContext());
        }
        
        double confidence = getConfidence(firstObject, statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                firstStatement.getSubject(),
                firstStatement.getPredicate(),
                firstStatement.getObject(),
                confidence,
                sources);
        return Collections.singleton(resolvedStatement);
    }

}
