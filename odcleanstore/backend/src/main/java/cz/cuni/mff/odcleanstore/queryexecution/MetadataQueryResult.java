package cz.cuni.mff.odcleanstore.queryexecution;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import java.util.Collection;

/**
 * Query result holder.
 * Provides access to all important information about query result such as the result quads, metadata or query settings.
 * @author Jan Michelfeit
 */
public class MetadataQueryResult extends QueryResultBase {
    /** The provenance metadata given to the input webservice as value of provenance parameter. */
    private Collection<Statement> provenanceMetadata;

    /** ODCS metadata. */
    private Model metadata;

    /**
     * Initializes a new instance.
     * @param provenanceMetadata metadata given to the input webservice as value of provenance parameter
     * @param metadata ODCS metadata
     * @param query the query string
     * @param queryType type of the query
     */
    public MetadataQueryResult(
            Collection<Statement> provenanceMetadata,
            Model metadata,
            String query,
            EnumQueryType queryType) {

        super(query, queryType);
        this.provenanceMetadata = provenanceMetadata;
        this.metadata = metadata;
    }

    /**
     * Returns the provenance metadata given to the input webservice as value of provenance parameter.
     * @return provenance metadata
     */
    public Collection<Statement> getProvenanceMetadata() {
        return provenanceMetadata;
    }

    /**
     * Returns ODCS metadata.
     * @return  metadata
     */
    public Model getMetadata() {
        return metadata;
    }
}
