package cz.cuni.mff.odcleanstore.shared.util;

/**
 * RDF prefix representation.
 * Immutable.
 *
 * @author Tomas Soukup
 */
public class RDFprefix {
    /** Prefix id (XML namespace prefix). */
    private String prefixId;

    /** Namespace the prefix expands to. */
    private String namespace;

    /**
     * Creates a new instance.
     * @param prefixId prefix id
     * @param namespace namespace
     */
    public RDFprefix(String prefixId, String namespace) {
        this.prefixId = prefixId;
        this.namespace = namespace;
    }

    /**
     * Returns the prefix id.
     * @return prefix ID (XML namespace prefix)
     */
    public String getPrefixId() {
        return prefixId;
    }

    /**
     * Returns the represented namespace.
     * @return namespace the prefix expands to
     */
    public String getNamespace() {
        return namespace;
    }
}
