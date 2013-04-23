package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * Data structure that handles mapping of URI resources linked with a owl:sameAs link
 * (even transitively).
 * Maintains a mapping of all subjects and objects URIs in triples passed by addLinks()
 * to a "canonical URI". A canonical URI is single URI selected for each (weakly)
 * connected component of the owl:sameAs links graph. owl:sameAs links for
 * Nodes other then Node_URI are ignored.
 * This is the fundamental component of implicit conflict resolution.
 *
 * The implementation is based on DFU (disjoint find and union) data structure
 * with path compression.
 *
 * @author Jan Michelfeit
 */
public class URIMappingImpl implements URIMapping {
    private static final Logger LOG = LoggerFactory.getLogger(URIMappingImpl.class);
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    /** Set of URIs preferred as canonical URIs. */
    private Set<String> preferredURIs = null;

    /**
     * Map representing the DFU data structure.
     * @see #getUriDFUParent()
     */
    private final Map<String, String> uriDFUParent;

    /**
     * Creates an URIMappingImpl instance with no preferred URIs.
     */
    public URIMappingImpl() {
        this(Collections.<String>emptySet());
    }

    /**
     * Creates an URIMappingImpl instance with the selected preferred URIs.
     * @param preferredURIs set of URIs preferred as canonical URIs; can be null
     */
    public URIMappingImpl(Set<String> preferredURIs) {
        this.preferredURIs = (preferredURIs != null)
                ? preferredURIs
                : Collections.<String>emptySet();
        
        // Initialize DFU data structure.
        uriDFUParent = createUriMap();
    }
    
    /**
     * Factory method for the map used to keep URI mappings.
     * @return map used to keep URI mappings
     */
    protected Map<String, String> createUriMap() {
        return new HashMap<String, String>();
    }

    /**
     * Adds owl:sameAs mappings as RDF triples.
     * @param sameAsLinks iterator over triples with owl:sameAs as a predicate
     */
    public void addLinks(Iterator<Statement> sameAsLinks) {
        while (sameAsLinks.hasNext()) {
            Statement triple = sameAsLinks.next();
            if (!triple.getPredicate().stringValue().equals(OWL.sameAs)) {
                LOG.warn("A triple with predicate {} passed as an owl:sameAs link",
                        triple.getPredicate().stringValue());
                continue;
            }
            if (!(triple.getSubject() instanceof URI) || !(triple.getObject() instanceof URI)) {
                // Ignore sameAs links between everything but URI resources; see owl:sameAs syntax
                // at see http://www.w3.org/TR/2004/REC-owl-semantics-20040210/syntax.html
                continue;
            }

            String subjectURI = triple.getSubject().stringValue();
            String objectURI = triple.getObject().stringValue();
            dfuUnion(subjectURI, objectURI);
        }
    }

    /**
     * Add an owl:sameAs mapping for two URIs given as strings.
     * @see #addLinks(Iterator)
     * @param subjectURI subject of a triple with the owl:sameAs predicate
     * @param objectURI object of a triple with the owl:sameAs predicate
     */
    public void addLink(String subjectURI, String objectURI) {
        dfuUnion(subjectURI, objectURI);
    }

    @Override
    public String getCanonicalURI(String uri) {
        if (!uriDFUParent.containsKey(uri)) {
            return uri;
        }
        return dfuRoot(uri);
    }

    @Override
    public URI mapURI(URI uriNode) {
        String uri = uriNode.stringValue();
        if (!uriDFUParent.containsKey(uri)) {
            return uriNode;
        }

        String canonicalURI = dfuRoot(uri);
        return canonicalURI.equals(uri) ? null : VALUE_FACTORY.createURI(canonicalURI);
    } 

    /**
     * Returns the URI at the root of a subtree in DFU for the argument, i.e.
     * the respective canonical URI.
     * For URI not contained in DFU returns the URI itself.
     * Note that this method may modify values in the DFU map, however,
     * it doesn't perform any additions or deletions and thus opened iterators should stay valid.
     * @param uri an RDF resource URI
     * @return a canonical URI
     */
    private String dfuRoot(String uri) {
        String parent = uriDFUParent.get(uri);
        if (parent == null) {
            // We are already at the root or no mapping is defined
            return uri;
        } else {
            String root = dfuRoot(parent);
            // Path compression optimization:
            if (!root.equals(parent)) {
                uriDFUParent.put(uri, root);
            }
            return root;
        }
    }

    /**
     * Adds a sameAs mapping between URIs by joining the respective subtrees
     * in DFU.
     * @param uri1 a resource URI
     * @param uri2 a resource URI
     */
    private void dfuUnion(String uri1, String uri2) {
        String root1 = dfuRoot(uri1);
        String root2 = dfuRoot(uri2);
        if (!root1.equals(root2)) {
            if (preferredURIs.contains(root1)) {
                uriDFUParent.put(root2, root1);
            } else {
                uriDFUParent.put(root1, root2);
            }
        }
    }

    /**
     * Returns the set of URIs preferred as canonical URIs.
     * @return URIs preferred as canonical URIs
     */
    protected Set<String> getPreferredURIs() {
        return preferredURIs;
    }

    /**
     * Map representing the DFU data structure.
     * The key->value pairs are resource URI -> parent resource URI.
     * If the value (parent URI) is null, the key is the root of the respective
     * subtree. If uriDFUParent doesn't contain an URI, the meaning is the same
     * as if the parent is null.
     *
     * The root of each subtree is the canonical URI for the particular
     * component.
     *
     * Invariant: If an URI present in uriDFUParent is in {@link #preferredURIs},
     * it is either the root of a DFU subtree (and thus a canonical URI) or its
     * respective root is from preferredURIs.
     * @return map representing the DFU data structure
     */
    protected Map<String, String> getUriDFUParent() {
        return uriDFUParent;
    }

}
