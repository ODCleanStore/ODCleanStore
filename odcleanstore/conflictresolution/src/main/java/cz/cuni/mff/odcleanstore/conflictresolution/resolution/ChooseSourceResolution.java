/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ObjectClusterIterator;

/**
 * @author Jan Michelfeit
 */
public class ChooseSourceResolution extends DecidingResolutionFunction {
    private static final Logger LOG = LoggerFactory.getLogger(ChooseSourceResolution.class);
    
    public static final String SOURCE_PARAM_NAME = "source";
    private static final String FUNCTION_NAME = "CHOOSE_SOURCE";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public ChooseSourceResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        URI preferredSource = getPreferredSourceURI(crContext);
        if (preferredSource == null) {
            return Collections.emptySet();
        }

        Collection<Statement> sortedStatements = statements;
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());
        ObjectClusterIterator it = new ObjectClusterIterator(sortedStatements);
        while (it.hasNext()) {
            Statement statement = it.next();
            Collection<Resource> sources = it.peekSources();
            if (sources.contains(preferredSource)) {
                addResolvedStatement(statement, sources, statements, crContext, result);
            }
        }

        return result;
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

    private URI getPreferredSourceURI(CRContext crContext) {
        String source = crContext.getResolutionStrategy().getParams().get(SOURCE_PARAM_NAME);
        if (source == null) {
            LOG.warn("Parameter '{}' missing for resolution function {}, conflicts cannot be resolved",
                    SOURCE_PARAM_NAME, getClass().getSimpleName());
            return null;
        }

        try {
            return crContext.getResolvedStatementFactory().getValueFactory().createURI(source);
        } catch (Exception e) {
            LOG.warn("Value '{}' of parameter '{}' is not a valid URI, conflicts cannot be resolved",
                    source, SOURCE_PARAM_NAME);
            return null;
        }
    }
}