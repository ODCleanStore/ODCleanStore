package cz.cuni.mff.odcleanstore.core;

/**
 * Type of serialization of input data.
 * @author Jan Michelfeit
 */
public enum SerializationLanguage {
    /** RDF/XML. */
    RDFXML,

    /** Notation3. */
    N3;

    @Override
    public String toString() {
        switch (this) {
        case RDFXML:
            return "RDF/XML";
        case N3:
            return "N3";
        default:
            return "";
        }
    }
}
