package cz.cuni.mff.odcleanstore.conflictresolution.impl;


import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Comparator;

/**
 * Comparator of two {@link Quad Quads}.
 * Compares in this order of importance: subject, property, object, namedGraph
 *
 * @author Jan Michelfeit
 */
/*package*/class QuadComparator implements Comparator<Quad> {
    @Override
    public int compare(Quad quad1, Quad quad2) {
        int comparison = NodeComparator.compare(quad1.getSubject(), quad2.getSubject());
        if (comparison != 0) {
            return comparison;
        }
        comparison = NodeComparator.compare(quad1.getPredicate(), quad2.getPredicate());
        if (comparison != 0) {
            return comparison;
        }
        comparison = NodeComparator.compare(quad1.getObject(), quad2.getObject());
        if (comparison != 0) {
            return comparison;
        }
        return NodeComparator.compare(quad1.getGraphName(), quad2.getGraphName());
    }
}