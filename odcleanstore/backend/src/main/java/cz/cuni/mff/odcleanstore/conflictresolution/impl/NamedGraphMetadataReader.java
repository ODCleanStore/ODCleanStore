package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

import com.hp.hpl.jena.graph.Node_URI;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class providing static method for loading ODCleanStore named graph metadata
 * directly from the format they are stored in the RDF database.
 *
 * @author Jan Michelfeit
 */
public final class NamedGraphMetadataReader {
    private static final Logger LOG = LoggerFactory.getLogger(NamedGraphMetadataReader.class);

    /** Disable constructor for a utility class. */
    private NamedGraphMetadataReader() {
    }

    /**
     * Reads named graph metadata from RDF passed as a collection of {@link Quad Quads}.
     *
     * @param data quads describing named graph metadata
     * @return map of metadata for named graphs described in data
     */
    public static NamedGraphMetadataMap readFromRDF(Iterator<Quad> data) {
        NamedGraphMetadataMap result = new NamedGraphMetadataMap();
        Map<String, Double> publisherScores = new TreeMap<String, Double>();

        while (data.hasNext()) {
            Quad quad = data.next();
            String predicateURI = quad.getPredicate().getURI();
            if (!quad.getSubject().isURI()) {
                continue;
            }
            Node_URI subject = (Node_URI) quad.getSubject();

            if (predicateURI.equals(W3P.publishedBy)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                if (quad.getObject().isURI()) {
                    metadata.setPublisher(quad.getObject().getURI());
                } else {
                    LOG.warn("Invalid provenance metadata - unexpected value '{}' of <{}>",
                            quad.getObject(), W3P.publishedBy);
                }
            } else if (predicateURI.equals(W3P.insertedAt)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);

                try {
                    String storedValue = quad.getObject().getLiteralLexicalForm();
                    Date stored = DateFormat.getDateInstance().parse(storedValue);
                    metadata.setInsertedAt(stored);
                } catch (Exception e) {
                    LOG.warn("Named graph stored date must be a valid date string, {} given", quad.getObject());
                }
            } else if (predicateURI.equals(W3P.source)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                if (quad.getObject().isURI()) {
                    metadata.setSource(quad.getObject().getURI());
                } else {
                    LOG.warn("Invalid provenance metadata - unexpected value '{}' of <{}>",
                            quad.getObject(), W3P.source);
                }
            } else if (predicateURI.equals(ODCS.score)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                try {
                    String scoreValue = quad.getObject().getLiteralLexicalForm();
                    Double score = Double.parseDouble(scoreValue);
                    metadata.setScore(score);
                } catch (Exception e) {
                    LOG.warn("Invalid provenance metadata - Named graph score must be a number, {} given",
                            quad.getObject());
                }
            } else if (predicateURI.equals(ODCS.publisherScore)) {
                assert quad.getObject().isLiteral();
                try {
                    String scoreValue = quad.getObject().getLiteralLexicalForm();
                    Double score = Double.parseDouble(scoreValue);
                    publisherScores.put(subject.getURI(), score);
                } catch (Exception e) {
                    LOG.warn("Invalid provenance metadata - Publisher score must be a number, {} given",
                            quad.getObject());
                }
            } else if (predicateURI.equals(DC.license)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                metadata.setLicence(quad.getObject().toString());
            } else if (predicateURI.equals(W3P.insertedBy)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                metadata.setInsertedBy(quad.getObject().toString());
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
