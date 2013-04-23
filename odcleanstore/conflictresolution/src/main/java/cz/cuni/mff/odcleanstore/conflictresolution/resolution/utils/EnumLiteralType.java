package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

/**
 * Type of an RDF literal.
 *
 * @author Jan Michelfeit
 */
public enum EnumLiteralType {
    /** String type. */
    STRING,

    /** Numeric type. */
    NUMERIC,

    /** Date type. */
    DATE,

    /** Time type. */
    TIME,

    /** Boolean type. */
    BOOLEAN,

    /** Unrecognized type. */
    OTHER
}
