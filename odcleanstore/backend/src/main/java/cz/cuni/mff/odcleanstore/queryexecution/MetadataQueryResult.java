package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Query result holder.
 * Provides access to all important information about query result such as the result quads, metadata or query settings.
 * @author Jan Michelfeit
 */
public class MetadataQueryResult extends QueryResultBase {
    /** The provenance metadata given to the input webservice as value of provenance parameter. */
    private Collection<Quad> provenanceMetadata;

    /** ODCS metadata. */
    private NamedGraphMetadataMap metadata;

    /**
     * Initializes a new instance.
     * @param provenanceMetadata metadata given to the input webservice as value of provenance parameter
     * @param metadata ODCS metadata
     * @param query the query string
     * @param queryType type of the query
     */
    public MetadataQueryResult(
            Collection<Quad> provenanceMetadata,
            NamedGraphMetadataMap metadata,
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
    public Collection<Quad> getProvenanceMetadata() {
        return provenanceMetadata;
    }

    /**
     * Returns ODCS metadata.
     * @return  metadata
     */
    public NamedGraphMetadataMap getMetadata() {
        return metadata;
    }
}
