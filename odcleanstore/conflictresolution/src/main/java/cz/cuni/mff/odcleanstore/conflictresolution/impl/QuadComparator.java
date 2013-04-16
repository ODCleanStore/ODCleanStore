package cz.cuni.mff.odcleanstore.conflictresolution.impl;


import java.util.Comparator;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 * Comparator of two {@link Statement Statements}.
 * Compares in this order of importance: subject, property, object, namedGraph
 *
 * @author Jan Michelfeit
 */
/*package*/class StatementComparator implements Comparator<Statement> {
    private static final Comparator<Value> VALUE_COMPARATOR = ValueComparator.getInstance();
    
    @Override
    public int compare(Statement quad1, Statement quad2) {
        int comparison = VALUE_COMPARATOR.compare(quad1.getSubject(), quad2.getSubject());
        if (comparison != 0) {
            return comparison;
        }
        comparison = VALUE_COMPARATOR.compare(quad1.getPredicate(), quad2.getPredicate());
        if (comparison != 0) {
            return comparison;
        }
        comparison = VALUE_COMPARATOR.compare(quad1.getObject(), quad2.getObject());
        if (comparison != 0) {
            return comparison;
        }

        
        Resource context1 = quad1.getContext();
        Resource context2 = quad2.getContext();
        if (context1 != null && context2 != null) {
            return VALUE_COMPARATOR.compare(context1, context2);
        } else if (context1 != null) {
            return 1;
        } else if (context2 != null) {
            return -1;
        } else {
            return 0;
        }
        
    }
}