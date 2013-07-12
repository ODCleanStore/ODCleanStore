package cz.cuni.mff.odcleanstore.conflictresolution;

import org.openrdf.model.Value;

/**
 * A distance measure for calculation of differenct between two {@link Value RDF nodes} as a number from
 * the interval [0,1].
 *
 * @author Jan Michelfeit
 */
public interface DistanceMeasure { 
    /**
     * Computes distance between two {@link Value RDF nodes} as a number in [0,1].
     * Value 1 means maximum distance, value 0 means identity.
     * @param value1 first compared RDF node
     * @param value2 second compared RDF node
     * @return distance of nodes as a number from interval [0,1]
     */
    double distance(Value value1, Value value2);
}
