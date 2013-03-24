package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

import com.hp.hpl.jena.graph.Node_URI;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

            if (predicateURI.equals(ODCS.publishedBy)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                if (quad.getObject().isURI()) {
                    metadata.setPublishers(addToListNullProof(quad.getObject().getURI(), metadata.getPublishers()));
                } else {
                    LOG.warn("Invalid provenance metadata - unexpected value '{}' of <{}>",
                            quad.getObject(), ODCS.publishedBy);
                }
            } else if (predicateURI.equals(ODCS.insertedAt)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);

                try {
                    String storedValue = quad.getObject().getLiteralLexicalForm();
                    Date stored = DateFormat.getDateInstance().parse(storedValue);
                    metadata.setInsertedAt(stored);
                } catch (Exception e) {
                    LOG.warn("Named graph stored date must be a valid date string, {} given", quad.getObject());
                }
            } else if (predicateURI.equals(ODCS.source)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                if (quad.getObject().isURI()) {
                    metadata.setSources(addToSetNullProof(quad.getObject().getURI(), metadata.getSources()));
                } else {
                    LOG.warn("Invalid provenance metadata - unexpected value '{}' of <{}>",
                            quad.getObject(), ODCS.source);
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
            } else if (predicateURI.equals(ODCS.license)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                metadata.setLicences(addToListNullProof(quad.getObject().toString(), metadata.getLicences()));
            } else if (predicateURI.equals(ODCS.insertedBy)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                metadata.setInsertedBy(quad.getObject().toString());
            } else if (predicateURI.equals(ODCS.updateTag)) {
                NamedGraphMetadata metadata = getMetadataObject(subject, result);
                metadata.setUpdateTag(quad.getObject().toString());
            }
        }

        if (!publisherScores.isEmpty()) {
            for (NamedGraphMetadata metadata : result.listMetadata()) {
                Double publisherScore = calculatePublisherScore(metadata, publisherScores);
                metadata.setTotalPublishersScore(publisherScore);
            }
        }

        return result;
    }

    /**
     * Calculates effective average publisher score - returns average of publisher scores or
     * null if there is none.
     * @param metadata named graph metadata; must not be null
     * @param publisherScores map of publisher scores
     * @return effective publisher score or null if unknown
     */
    private static Double calculatePublisherScore(final NamedGraphMetadata metadata, final Map<String, Double> publisherScores) {
        List<String> publishers = metadata.getPublishers();
        if (publishers == null) {
            return null;
        }
        double result = 0;
        int count = 0;
        for (String publisher : publishers) {
            Double score = publisherScores.get(publisher);
            if (score != null) {
                result += score;
                count++;
            }
        }
        return (count > 0) ? result / count : null;
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

    /**
     * Add a value to the set given in parameter and return modified set; if set is null, create new instance.
     * @param value value to add to the set
     * @param set set to add to or null
     * @return set containing the given value
     */
    private static <T> Set<T> addToSetNullProof(T value, Set<T> set) {
        Set<T> result = set;
        if (result == null) {
            result = new TreeSet<T>();
        }
        result.add(value);
        return result;
    }

    /**
     * Add a value to the list given in parameter and return modified list; if list is null, create new instance.
     * @param value value to add to the list
     * @param list list to add to or null
     * @return list containing the given value
     */
    private static <T> List<T> addToListNullProof(T value, List<T> list) {
        final int defaultListSize = 1;
        List<T> result = list;
        if (result == null) {
            result = new ArrayList<T>(defaultListSize);
        }
        result.add(value);
        return result;
    }
}