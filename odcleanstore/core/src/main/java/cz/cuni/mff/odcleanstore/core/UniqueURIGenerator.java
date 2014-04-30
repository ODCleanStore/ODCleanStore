package cz.cuni.mff.odcleanstore.core;


/**
 * Generator of URIs unique at least within each instance.
 *
 * @author Jan Michelfeit
 */
public interface UniqueURIGenerator {
    /**
     * Return a next named graph unique at least within calls on each instance.
     * @return a named graph URI
     * @todo return URI
     */
    String nextURI();
}
