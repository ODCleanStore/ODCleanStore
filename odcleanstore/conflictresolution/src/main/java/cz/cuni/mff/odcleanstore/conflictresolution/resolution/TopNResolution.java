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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Returns N best statements (see {@link BestResolution}).
 * The preferred sources is given in optional
 * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
 * {@value #SOURCE_PARAM_NAME} (by default returns {@value #DEFAULT_COUNT} value).
 * @author Jan Michelfeit
 */
public class TopNResolution extends DecidingResolutionFunction {
    private  static final String FUNCTION_NAME = "TOPN";
    
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
    
    private static final Logger LOG = LoggerFactory.getLogger(TopNResolution.class);
    
    /** 
     * Name of the {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy#getParams() parameter} 
     * specifying the number of quads to return.
     */
    public static final String COUNT_PARAM_NAME = "n";
    
    /** Default number of quads to return. */
    public static final int DEFAULT_COUNT = 1;
    private static final Comparator<ResolvedStatement> FQUALITY_COMPARATOR = new Comparator<ResolvedStatement>() {
        @Override
        public int compare(ResolvedStatement o1, ResolvedStatement o2) {
            return Double.compare(o1.getQuality(), o2.getQuality());
        }
    };

    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public TopNResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }

        int count = getCountParam(crContext.getResolutionStrategy().getParams());
        Collection<Statement> sortedStatements = statements;

        PriorityQueue<ResolvedStatement> bestStatements = new PriorityQueue<ResolvedStatement>(count, FQUALITY_COMPARATOR);

        // cluster means a sequence of statements with the same object
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            double fQuality = getFQuality(statement.getObject(), sources, crContext);
            if (bestStatements.size() < count) {
                bestStatements.add(createResolvedStatement(statement, fQuality, sources, crContext));
            } else if (fQuality > bestStatements.peek().getQuality()) {
                bestStatements.poll();
                bestStatements.add(createResolvedStatement(statement, fQuality, sources, crContext));
            }
        }

        return bestStatements;
    }

    private ResolvedStatement createResolvedStatement(Statement statement, double fQuality, Collection<Resource> sources,
            CRContext crContext) {
        return crContext.getResolvedStatementFactory().create(
                statement.getSubject(),
                statement.getPredicate(),
                statement.getPredicate(),
                fQuality,
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
