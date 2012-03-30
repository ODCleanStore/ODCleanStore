package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.data.QuadCollection;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import com.hp.hpl.jena.graph.Node_URI;

import de.fuberlin.wiwiss.ng4j.Quad;

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
/*package*/final class NamedGraphMetadataReader {
    private static final Logger LOG = LoggerFactory.getLogger(NamedGraphMetadataReader.class);

    /** Disable constructor for a utility class. */
    private NamedGraphMetadataReader() {
    }

    /**
     * Reads named graph metadata from RDF passed as {@link QuadCollection}.
     *
     * @param data a collection of quads describing named graph metadata
     * @return map of metadata for named graphs described in data
     * @throws ODCleanStoreException thrown when named graph metadata contained in the input data
     *         are not correctly formated
     */
    public static NamedGraphMetadataMap readFromRDF(QuadCollection data)
            throws ODCleanStoreException {
        NamedGraphMetadataMap result = new NamedGraphMetadataMap();
        Map<String, Double> publisherScores = new TreeMap<String, Double>();

        for (Quad quad : data) {
            String predicateURI = quad.getPredicate().getURI();
            if (!predicateURI.startsWith(ODCS.getURI())) {
                continue;
            } else if (!quad.getSubject().isURI()) {
                // All recognized ODCS properties relate to an URI
                continue;
            }
            Node_URI subject = (Node_URI) quad.getSubject();

            if (predicateURI.equals(ODCS.publisher)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                String publisher = quad.getObject().getURI();
                metadata.setPublisher(publisher);
            } else if (predicateURI.equals(ODCS.stored)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                String storedValue = quad.getObject().getLiteralLexicalForm();
                try {
                    Date stored = DateFormat.getDateInstance().parse(storedValue);
                    metadata.setStored(stored);
                } catch (ParseException e) {
                    LOG.warn("Named graph stored date must be a valid date string, {} given",
                            storedValue);
                    throw new ODCleanStoreException(e);
                }
            } else if (predicateURI.equals(ODCS.dataSource)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                String dataSourceString = quad.getObject().getURI();
                metadata.setDataSource(dataSourceString);
            } else if (predicateURI.equals(ODCS.score)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                String scoreValue = quad.getObject().getLiteralLexicalForm();
                try {
                    Double score = Double.parseDouble(scoreValue);
                    metadata.setScore(score);
                } catch (NumberFormatException e) {
                    LOG.warn("Named graph score must be a number, {} given", scoreValue);
                    throw new ODCleanStoreException(e);
                }
            } else if (predicateURI.equals(ODCS.publisherScore)) {
                assert quad.getObject().isLiteral();
                String scoreValue = quad.getObject().getLiteralLexicalForm();
                try {
                    Double score = Double.parseDouble(scoreValue);
                    publisherScores.put(subject.getURI(), score);
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
     * @param graphName URI of the named graph
     * @param metadataMap searched map of named graph metadata; may be modified
     *        by a call to this method
     * @return NamedGraphMetadata object for the specified named graph URI
     *         contained in metadataMap
     */
    private static NamedGraphMetadata getMetadataObject(
            Node_URI graphName, NamedGraphMetadataMap metadataMap) {

        NamedGraphMetadata metadata = metadataMap.getMetadata(graphName);
        if (metadata == null) {
            metadata = new NamedGraphMetadata(graphName.getURI());
            metadataMap.addMetadata(metadata);
        }
        return metadata;
    }
}
