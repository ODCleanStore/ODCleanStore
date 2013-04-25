/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;

/**
 * @author Jan Michelfeit
 */
public class TopNResolution extends DecidingResolutionFunction {
    private static final Logger LOG = LoggerFactory.getLogger(TopNResolution.class);
    public static final String COUNT_PARAM_NAME = "n";
    public static final int DEFAULT_COUNT = 1;
    private static final Comparator<ResolvedStatement> CONFIDENCE_COMPARATOR = new Comparator<ResolvedStatement>() {
        @Override
        public int compare(ResolvedStatement o1, ResolvedStatement o2) {
            return Double.compare(o1.getConfidence(), o2.getConfidence());
        }
    };

    public TopNResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        int count = getCountParam(crContext.getResolutionStrategy().getParams());
        Collection<Statement> sortedStatements = statements;

        PriorityQueue<ResolvedStatement> bestStatements = new PriorityQueue<ResolvedStatement>(count, CONFIDENCE_COMPARATOR);

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double confidence = getConfidence(statement.getObject(), statements, sources, crContext);
            if (bestStatements.size() < count) {
                bestStatements.add(createResolvedStatement(statement, confidence, sources, crContext));
            } else if (confidence > bestStatements.peek().getConfidence()) {
                bestStatements.poll();
                bestStatements.add(createResolvedStatement(statement, confidence, sources, crContext));
            }
        }

        return bestStatements;
    }

    private ResolvedStatement createResolvedStatement(Statement statement, double confidence, Collection<Resource> sources,
            CRContext crContext) {
        return crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getPredicate(),
                confidence,
                sources);
    }

    private int getCountParam(Map<String, String> params) {
        String countParam = params.get(COUNT_PARAM_NAME);
        if (countParam == null) {
            return DEFAULT_COUNT;
        }
        int count;
        try {
            count = Integer.parseInt(countParam);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid value of parameter '{}': {}", COUNT_PARAM_NAME, countParam);
            return DEFAULT_COUNT;
        }
        if (count < 1) {
            LOG.warn("Value of parameter '{}' must be positive, {} given", COUNT_PARAM_NAME, count);
            return DEFAULT_COUNT;
        }
        return count;
    }
}
