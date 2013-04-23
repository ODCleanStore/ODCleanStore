/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * @author Jan Michelfeit
 */
public interface SortedStatementCollection extends Set<Statement> {
    Iterator<Statement> listStatements(final Resource subject, final URI predicate);

    Iterator<Statement> listStatements(final Resource subject, final URI predicate, final Value object);
}
