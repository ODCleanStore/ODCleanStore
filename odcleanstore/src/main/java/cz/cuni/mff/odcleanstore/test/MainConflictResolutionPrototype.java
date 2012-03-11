package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author Jan Michelfeit
 */
public class MainConflictResolutionPrototype {
    private static long uriCounter = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ODCleanStoreException {
        test1(EnumAggregationType.CONCAT, true);
        test1(EnumAggregationType.ALL, true);
    }

    private static void test1(EnumAggregationType aggregationType, boolean print)
            throws ODCleanStoreException {
        String predicate_1 = createURI("predicate_1");
        String predicate_2 = createURI("predicate_2");

        String subject_1_1 = createURI("subject_1_1");
        String subject_1_1_alias = createURI("subject_1_1_alias");
        String object_1_1_a = createURI("object_1_1_a");
        String object_1_1_a_alias = createURI("object_1_1_a_alias");
        String object_1_1_b = createURI("object_1_1_b");
        String object_1_2 = createURI("object_1_2");

        String subject_2 = createURI("subject_2");
        String object_2 = createURI("object_2");
        String object_2_alias = createURI("object_2_alias");

        ConflictResolverSpec CRSpec = new ConflictResolverSpec("http://example.com/ng-generated/");
        CRSpec.setDefaultAggregation(aggregationType);

        // SameAs links
        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(createSameAsTriple(subject_1_1, subject_1_1_alias));
        sameAsLinks.add(createSameAsTriple(object_1_1_a, object_1_1_a_alias));
        sameAsLinks.add(createSameAsTriple(object_2, object_2_alias));
        CRSpec.setSameAsLinks(sameAsLinks.iterator());

        // Preferred URIs
        Set<String> preferredURIs = new HashSet<String>();
        preferredURIs.add(subject_1_1_alias);
        preferredURIs.add(object_2);
        CRSpec.setPreferredURIs(preferredURIs);

        // Data
        QuadCollection quads = new QuadCollection();
        quads.add(createQuad(subject_1_1, predicate_1, object_1_1_a, createURI("ng_1_1_a")));
        quads.add(createQuad(subject_1_1_alias, predicate_1, object_1_1_b, createURI("ng_1_1_b")));
        quads.add(createQuad(subject_1_1, predicate_1, object_1_1_a_alias,
                createURI("ng_1_1_a_alias")));
        quads.add(createQuad(subject_1_1, predicate_2, object_1_2, createURI("ng_1_2")));
        quads.add(createQuad(subject_2, predicate_2, object_2, createURI("ng_2")));
        quads.add(createQuad(subject_2, predicate_2, object_2_alias, createURI("ng_2")));

        // Resolve conflicts
        ConflictResolver cr = ConflictResolverFactory.createResolver(CRSpec);
        Collection<CRQuad> resolved = cr.resolveConflicts(quads);

        // Print result
        if (print) {
            for (CRQuad crQuad : resolved) {
                printCRQuad(crQuad);
            }
        }
    }

    private static void printCRQuad(CRQuad crQuad) {
        System.out.printf("%s   %s   %s\n",
                crQuad.getQuad().getTriple().getSubject(),
                crQuad.getQuad().getTriple().getPredicate(),
                crQuad.getQuad().getTriple().getObject());
        // System.out.println("  NG: " + crQuad.getQuad().getNamedGraph());
        System.out.printf("  quality: %.3f\n", crQuad.getQuality());
        System.out.print("  sources: ");
        for (String source : crQuad.getSourceNamedGraphURIs()) {
            System.out.printf("<%s>, ", source);
        }
        System.out.println();
    }

    private static String createURI(String localPart) {
        return "http://example.com/" + localPart;
    }

    private static String readableURI(String uri) {
        return uri.replaceAll("^<http://example.com/(.+)>$", "ex:$1");
    }

    private static String getUniqueURI() {
        uriCounter++;
        return createURI(Long.toString(uriCounter));
    }

    private static void resetURICounter() {
        uriCounter = 0;
    }

    public static Quad createQuad() {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()),
                Node.createURI(getUniqueURI()));
    }

    public static Quad createQuad(String subjectURI, String predicateURI, String objectURI) {
        return new Quad(
                Node.createURI(getUniqueURI()),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    public static Quad createQuad(String subjectURI, String predicateURI,
            String objectURI, String namedGraphURI) {
        return new Quad(
                Node.createURI(namedGraphURI),
                Node.createURI(subjectURI),
                Node.createURI(predicateURI),
                Node.createURI(objectURI));
    }

    private static Triple createSameAsTriple(String uri1, String uri2) {
        return Triple.create(
                Node.createURI(uri1),
                Node.createURI(OWL.sameAs),
                Node.createURI(uri2));
    }
}
