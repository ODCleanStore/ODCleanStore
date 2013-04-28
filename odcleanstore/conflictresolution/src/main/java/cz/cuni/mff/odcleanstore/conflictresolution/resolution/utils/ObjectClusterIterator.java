/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.impl.util.CRUtils;

/**
 * @author Jan Michelfeit
 */
public class ObjectClusterIterator implements Iterator<Statement> {
    private Iterator<Statement> clusterStartIt;
    private Iterator<Statement> statementIt;
    private int clusterSize = 0;
    private Statement lastStatement = null;
    private Collection<Resource> lastSources = null;
    
    public ObjectClusterIterator(Iterable<Statement> sortedStatements) {
        this.statementIt = sortedStatements.iterator();
        this.clusterStartIt = sortedStatements.iterator();
    }
    
    public Collection<Resource> peekSources() {
        return lastSources; // TODO: make lazy?
    }
    
    @Override
    public boolean hasNext() {
        return clusterSize > 0 || statementIt.hasNext(); 
    }
    
    @Override
    public Statement next() {
        while (statementIt.hasNext()) {
            Statement statement = statementIt.next();
            if (clusterSize > 0 && !CRUtils.sameValues(statement.getObject(), lastStatement.getObject())) {
                // We've reached beginning of the next cluster
                lastSources = getSources(clusterStartIt, clusterSize);
                Statement result = lastStatement;
                lastStatement = statement;
                clusterSize = 1;
                return result;
            } else {
                clusterSize++;
                lastStatement = statement;
            }
        }
        if (clusterSize > 0) {
            // Last cluster
            lastSources = getSources(clusterStartIt, clusterSize);
            clusterSize = 0; // end iteration
            return lastStatement;
        }
        return null;
    }
    
    @Override
    public void remove() {
        new UnsupportedOperationException(); 
    }
    
    private static Collection<Resource> getSources(Iterator<Statement> clusterIt, int clusterSize) {
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
}
