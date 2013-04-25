/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class MaxSourceMetadataValueResolution extends DecidingResolutionFunction {
    private static final Logger LOG = LoggerFactory.getLogger(MaxSourceMetadataValueResolution.class);
    public static final String PREDICATE_PARAM_NAME = "predicate";
    private static final int INITIAL_RESULT_CAPACITY = 5; // expect few non-aggregable statements
    
    public MaxSourceMetadataValueResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
    
    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        if (statements.isEmpty()) {
            return Collections.emptySet();
        }
        
        URI predicateURI = getPredicateURI(statements, crContext);
        Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(INITIAL_RESULT_CAPACITY);
        Value lastObject = null;
        Statement bestStatement = null; // the best quad so far
        for (Statement statement : statements) {
            if (!accept(statement, predicateURI, crContext) 
                    // check for same objects so that non-aggregable statements are only once in the result;
                    // relies on statements being spog-sorted
                    && !CRUtils.sameValues(statement.getObject(), lastObject)) { 
                handleNonAggregableStatement(statement, statements, crContext, result);
            } else if (bestStatement == null)  {
                bestStatement = statement;
            } else if (compare(statement, bestStatement, predicateURI, crContext) > 0) {
                bestStatement = statement;
            }
            lastObject = statement.getObject();
        }
        
        if (bestStatement == null) {
            // no accepted statement
            return result;
        }
        
        Set<Resource> sources = filterSources(bestStatement, statements);
        double confidence = getConfidence(bestStatement.getObject(), statements, sources, crContext);
        ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                bestStatement.getSubject(),
                bestStatement.getPredicate(),
                bestStatement.getObject(),
                confidence,
                sources);
        result.add(resolvedStatement);
        return result;
    }
    
    protected boolean accept(Statement statement, URI predicateURI, CRContext crContext) {
        if (predicateURI == null || statement.getContext() == null) {
            return false;
        }
        
        Value metadataValue = getMetadataValue(statement, predicateURI, crContext.getMetadata());
        return metadataValue != null && metadataValue instanceof Literal;
    }

    protected int compare(Statement statement1, Statement statement2, URI predicateURI, CRContext crContext) {
        Value metadataValue1 = getMetadataValue(statement1, predicateURI, crContext.getMetadata());
        Value metadataValue2 = getMetadataValue(statement2, predicateURI, crContext.getMetadata());
        
        // Check if the metadata is present
        if (metadataValue1 == metadataValue2) {
            return 0;
        } else if (metadataValue1 == null) {
            return -1;
        } else if (metadataValue2 == null) {
            return 1;
        } else if (!(metadataValue1 instanceof Literal)) {
            return 0; // undefined
        }
        
        // Get proper literal comparator
        EnumLiteralType comparisonType = ResolutionFunctionUtils.getLiteralType((Literal) metadataValue1);
        LiteralComparator comparator = LiteralComparatorFactory.getComparator(comparisonType);
        
        // Use literal comparator to compare the metadata statements
        boolean accept1 = comparator.accept(metadataValue1, crContext);
        boolean accept2 = comparator.accept(metadataValue2, crContext);
        if (accept1 && accept2) {
            return comparator.compare(metadataValue1, metadataValue2);
        } else {
            return Boolean.compare(accept1, accept2);
        }
    }
    
    private Value getMetadataValue(Statement statement, URI predicateURI, Model metadata) {
        Iterator<Statement> metadataIt = metadata.filter(statement.getContext(), predicateURI, null).iterator();
        if (metadataIt.hasNext()) {
            return metadataIt.next().getObject();
        } else {
            return null;
        }
    }

    private URI getPredicateURI(Model statements, CRContext crContext) {
        String predicate = crContext.getResolutionStrategy().getParams().get(PREDICATE_PARAM_NAME);
        if (predicate == null) {
            LOG.warn("Parameter '{}' missing for resolution function {}, conflicts cannot be resolved", 
                    PREDICATE_PARAM_NAME, getClass().getSimpleName());
        }
        
        URI predicateURI = null;
        try {
            predicateURI = crContext.getResolvedStatementFactory().getValueFactory().createURI(predicate);
        } catch (Exception e) {
            LOG.warn("Value '{}' of parameter '{}' is not a valid URI, conflicts cannot be resolved", 
                    predicate, PREDICATE_PARAM_NAME);
        }
        return predicateURI;
    }
}
