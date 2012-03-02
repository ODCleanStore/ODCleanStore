package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class providing static method for loading ODCleanStore named graph metadata
 * directly from the format they are stored in the RDF database.
 * 
 * @author Jan Michelfeit
 */
final class NamedGraphMetadataReader {
    private static final Logger LOG = LoggerFactory.getLogger(NamedGraphMetadataReader.class);

    /** Disable constructor for a utility class. */
    private NamedGraphMetadataReader() {
    }

    /**
     * Reads named graph metadata from RDF passed as a QuadGraph.
     * The method relies on the fact that objects for recognized properties from {@link ODCS} are
     * URITripleItems or LiteralTripleItems depending on
     * the property.
     * @param graph a graph containing RDF triples describing named graph metadata
     * @return map of metadata for named graphs described in graph
     * @throws ODCleanStoreException thrown when named graph metadata contained
     *         in the input graph are not correctly formated
     */
    public static NamedGraphMetadataMap readFromRDF(QuadGraph graph) throws ODCleanStoreException {
        NamedGraphMetadataMap result = new NamedGraphMetadataMap();
        Map<String, Double> publisherScores = new TreeMap<String, Double>();

        for (Quad quad : graph) {
            String predicateURI = quad.getPredicate().getURI();
            if (!predicateURI.startsWith(ODCS.getURI())) {
                continue;
            } else if (!(quad.getSubject() instanceof URITripleItem)) {
                // All ODCS properties relate to an URI
                continue;
            }
            String subjectURI = quad.getSubject().getURI();

            if (predicateURI.equals(ODCS.publisher)) {
                NamedGraphMetadata metadata = getMetadataObject(subjectURI, result);
                assert quad.getObject() instanceof URITripleItem;
                String publisher = quad.getObject().getURI();
                metadata.setPublisher(publisher);
            } else if (predicateURI.equals(ODCS.stored)) {
                NamedGraphMetadata metadata = getMetadataObject(subjectURI, result);
                assert quad.getObject() instanceof LiteralTripleItem;
                String storedValue = ((LiteralTripleItem) quad.getObject()).getValue();
                try {
                    Date stored = DateFormat.getDateInstance().parse(storedValue);
                    metadata.setStored(stored);
                } catch (ParseException e) {
                    // TODO
                    throw new ODCleanStoreException(e);
                }
            } else if (predicateURI.equals(ODCS.dataSource)) {
                NamedGraphMetadata metadata = getMetadataObject(subjectURI, result);
                assert quad.getObject() instanceof URITripleItem;
                String dataSourceString = quad.getObject().getURI();
                metadata.setDataSource(dataSourceString);
            } else if (predicateURI.equals(ODCS.score)) {
                NamedGraphMetadata metadata = getMetadataObject(subjectURI, result);
                assert quad.getObject() instanceof LiteralTripleItem;
                String scoreValue = ((LiteralTripleItem) quad.getObject()).getValue();
                try {
                    Double score = Double.parseDouble(scoreValue);
                    metadata.setScore(score);
                } catch (NumberFormatException e) {
                    LOG.warn("Named graph score must be a number, {} given", scoreValue);
                    throw new ODCleanStoreException(e);
                }
            } else if (predicateURI.equals(ODCS.publisherScore)) {
                assert quad.getObject() instanceof LiteralTripleItem;
                String scoreValue = ((LiteralTripleItem) quad.getObject()).getValue();
                try {
                    Double score = Double.parseDouble(scoreValue);
                    publisherScores.put(subjectURI, score);
                } catch (NumberFormatException e) {
                    LOG.warn("Publisher score must be a number, {} given", scoreValue);
                    throw new ODCleanStoreException(e);
                }
            }
        }

        if (!publisherScores.isEmpty()) {
            for (NamedGraphMetadata metadata : result.listMetadata()) {
                String publisherURI = metadata.getPublisher();
                if (publisherScores.containsKey(publisherURI)) {
                    metadata.setPublisherScore(publisherScores.get(publisherURI));
                }
            }
        }

        return result;
    }

    /**
     * Returns the respective NamedGraphMetadata object for the selected named
     * graph in metadataMap or creates a new one and adds it to metadataMap.
     * @param namedGraphURI URI of the named graph
     * @param metadataMap searched map of named graph metadata; may be modified
     *        by a call to this method
     * @return NamedGraphMetadata object for the specified named graph URI
     *         contained in metadataMap
     */
    private static NamedGraphMetadata getMetadataObject(
            String namedGraphURI, NamedGraphMetadataMap metadataMap) {

        NamedGraphMetadata metadata = metadataMap.getMetadata(namedGraphURI);
        if (metadata == null) {
            metadata = new NamedGraphMetadata(namedGraphURI);
            metadataMap.addMetadata(metadata);
        }
        return metadata;
    }
}
