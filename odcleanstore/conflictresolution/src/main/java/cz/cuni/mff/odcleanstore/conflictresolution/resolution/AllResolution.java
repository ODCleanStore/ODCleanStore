/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;

/**
 * @author Jan Michelfeit
 */
public class AllResolution extends DecidingResolutionFunction {
    protected AllResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) { 
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<Statement> sortedStatements = statements;
        // Shouldn't be necessary since statements are guaranteed to be ordered by spog:
        // if (!ResolutionFunctionUtils.isSorted(sortedStatements, OBJECT_COMPARATOR)) {
        // Statement[] statementArr = sortedStatements.toArray(new Statement[0]);
        // Arrays.sort(statementArr, OBJECT_COMPARATOR);
        // sortedStatements = Arrays.asList(statementArr);
        // }
        
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size() / 2);

        // cluster means a sequence of statements with the same object
        Iterator<Statement> clusterStart = sortedStatements.iterator();
        Statement lastStatement = null;
        int clusterSize = 0;
        for (Statement statement : sortedStatements) {
            if (clusterSize > 0 && !CRUtils.sameValues(statement.getObject(), lastStatement.getObject())) {
                // beginning of new cluster, add the previous to the result
                Collection<Resource> sources = getSources(clusterStart, clusterSize);
                addResolvedStatement(lastStatement, sources, statements, crContext, result);
                clusterSize = 0;
            }
            
            clusterSize++;
            lastStatement = statement;
        }
        if (clusterSize > 0) {
            // don't forget last cluster
            Collection<Resource> sources = getSources(clusterStart, clusterSize);
            addResolvedStatement(lastStatement, sources, statements, crContext, result);
        }
        
        return result;
    }

    private Collection<Resource> getSources(Iterator<Statement> clusterIt, int clusterSize) {
        if (clusterSize == 1) {
            return Collections.singleton(clusterIt.next().getContext());
        } else {
            Collection<Resource> sources = new ArrayList<Resource>(clusterSize);
            for (int i = 0; i < clusterSize; i++) {
                sources.add(clusterIt.next().getContext());
            }
            return sources;
        }
    }

    private void addResolvedStatement(Statement statement, Collection<Resource> sources, Collection<Statement> statements,
            CRContext crContext, Collection<ResolvedStatement> result) {
        result.add(crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getObject(),
                getConfidence(statement.getObject(), statements, sources, crContext),
                sources));
    }

}
