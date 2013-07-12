package cz.cuni.mff.odcleanstore.conflictresolution;

/**
 * Cardinality of a property affecting the quality calculation for quads with the property.
 * @author Jan Michelfeit
 */
public enum EnumCardinality {
    /** It is valid for the respective property to have multiple values. */
    MANYVALUED,

    /** The respective property should have only one value. */
    SINGLEVALUED
}
