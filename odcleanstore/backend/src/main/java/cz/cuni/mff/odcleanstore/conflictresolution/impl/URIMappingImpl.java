package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.UnexpectedPredicateException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
/*package*/class URIMappingImpl implements URIMapping {
    private static final Logger LOG = LoggerFactory.getLogger(URIMappingImpl.class);

    /** Set of URIs preferred as canonical URIs. */
    private Set<String> preferredURIs = null;

    /**
     * Map representing the DFU data structure.
     * The key->value pairs are resource URI -> parent resource URI.
     * If the value (parent URI) is null, the key is the root of the respective
     * subtree. If uriDFUParent doesn't contain an URI, the meaning is the same
     * as if the parent is null.
     *
     * Ther root of each subtree is the canonical URI for the particular
     * component.
     *
     * Invariant: If an URI present in uriDFUParent is in {@link #preferredURIs},
     * it is eighter the root of a DFU subtree (and thus a canonical URI) or its
     * respective root is from preferredURIs.
     */
    private Map<String, String> uriDFUParent;

    /**
     * Initializes DFU data structure.
     */
    {
        uriDFUParent = new TreeMap<String, String>();
    }

    /**
     * Creates an URIMappingImpl instance with no preferred URIs.
     */
    public URIMappingImpl() {
        this.preferredURIs = Collections.emptySet();
    }

    /**
     * Creates an URIMappingImpl instance with the selected preferred URIs.
     * @param preferredURIs set of URIs preferred as canonical URIs or null
     */
    public URIMappingImpl(Set<String> preferredURIs) {
        this.preferredURIs = (preferredURIs != null)
                ? preferredURIs
                : Collections.<String>emptySet();
    }

    /**
     * Adds owl:sameAs mappings as RDF triples.
     * @param sameAsLinks iterator over triples with owl:sameAs as a predicate
     * @throws UnexpectedPredicateException thrown if any of the triples has
     *         a predicate different from owl:sameAs
     */
    public void addLinks(Iterator<Triple> sameAsLinks) throws UnexpectedPredicateException {
        while (sameAsLinks.hasNext()) {
            Triple triple = sameAsLinks.next();
            if (!triple.getPredicate().hasURI(OWL.sameAs)) {
                LOG.error("A triple with predicate {} passed as a sameAs link",
                        triple.getPredicate().getURI());
                throw new UnexpectedPredicateException(triple.getPredicate().getURI(), OWL.sameAs);
            }
            if (!triple.getSubject().isURI() || !triple.getObject().isURI()) {
                // Ignore sameAs links between everything but URI resources; see owl:sameAs syntax
                // at see http://www.w3.org/TR/2004/REC-owl-semantics-20040210/syntax.html
                continue;
            }

            String subjectURI = triple.getSubject().getURI();
            String objectURI = triple.getObject().getURI();
            dfuUnion(subjectURI, objectURI);
        }
    }

    /**
     * {@inheritDoc}
     * @param uriNode {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Node mapURI(Node uriNode) {
        assert uriNode.isURI();
        String uri = uriNode.getURI();
        if (!uriDFUParent.containsKey(uri)) {
            return null;
        }

        String canonicalURI = dfuRoot(uri);
        return canonicalURI.equals(uri) ? null : Node.createURI(canonicalURI);
    }

    /**
     * Returns the URI at the root of a subtree in DFU for the argument, i.e.
     * the respective canonical URI.
     * For URI not contained in DFU returns the URI itself.
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

}
