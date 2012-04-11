package cz.cuni.mff.odcleanstore.transformer;

/**
 * Type of strategy to use when an aggregation cannot be applied to a value.
 * 
 * @author Jan Michelfeit
 */
public enum EnumTransformationType {
    /** Transformation of a graph in the dirty database. */
    NEW,

    /** Transformation of an existing graph in the clean database. */
    EXISTING
}
