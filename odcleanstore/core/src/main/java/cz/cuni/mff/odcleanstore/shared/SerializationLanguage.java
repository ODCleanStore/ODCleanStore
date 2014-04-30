package cz.cuni.mff.odcleanstore.shared;

/**
 * Type of serialization of input data.
 */
public enum SerializationLanguage {
    RDFXML, N3;
    
    @Override
    public String toString() {
    	switch (this) {
    		case RDFXML: return "RDF/XML";
    		case N3: return "N3";
    	}
    	return "";
    }
}