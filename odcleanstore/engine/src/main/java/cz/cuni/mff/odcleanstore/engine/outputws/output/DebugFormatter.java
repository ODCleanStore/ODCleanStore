package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;

import org.openrdf.model.Statement;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

/**
 * Returns a simple representation of the query result for debugging purposes.
 * 
 * @author Jan Michelfeit
 */
public class DebugFormatter extends ResultFormatterBase {
    @Override
    public Representation format(final BasicQueryResult result, Reference requestReference) {
        WriterRepresentation representation = new WriterRepresentation(MediaType.TEXT_PLAIN) {
            @Override
            public void write(Writer writer) throws IOException {
                writer.write("Query executed in ");
                writer.write(formatExecutionTime(result.getExecutionTime()));
                writer.write("\n\n===============================\n== Query results ==\n");
                for (CRQuad crQuad : result.getResultQuads()) {
                    writer.write(crQuad.getQuad().toString());
                    writer.write("\n\tQuality: ");
                    writer.write(formatScore(crQuad.getQuality()));
                    writer.write("; Sources: ");
                    boolean first = true;
                    for (String sourceURI : crQuad.getSourceNamedGraphURIs()) {
                        if (!first) {
                            writer.write(", ");
                        }
                        first = false;
                        writer.write(sourceURI);
                    }
                    writer.write('\n');
                }

                writer.write("\n===============================\n== Metadata ==\n");
                for (NamedGraphMetadata metadata : result.getMetadata().listMetadata()) {
                    writer.write(metadata.getNamedGraphURI());
                    writer.write('\n');
                    if (metadata.getSources() != null) {
                        writer.write("\tSource: ");
                        writer.write(formatCollection(metadata.getSources()));
                        writer.write('\n');
                    }
                    if (metadata.getInsertedAt() != null) {
                        writer.write("\tInserted at: ");
                        writer.write(formatDate(metadata.getInsertedAt()));
                        writer.write('\n');
                    }
                    if (metadata.getScore() != null) {
                        writer.write("\tGraph score: ");
                        writer.write(metadata.getScore().toString());
                        writer.write('\n');
                    }
                    if (metadata.getUpdateTag() != null) {
                        writer.write("\tUpdate tage: ");
                        writer.write(metadata.getUpdateTag().toString());
                        writer.write('\n');
                    }
                }
            }
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }

    @Override
    public Representation format(final MetadataQueryResult metadataResult,
            final GraphScoreWithTrace qaResult, final long totalTime, Reference requestReference) {

        WriterRepresentation representation = new WriterRepresentation(MediaType.TEXT_PLAIN) {
            @Override
            public void write(Writer writer) throws IOException {
                writer.write("Query executed in ");
                writer.write(formatExecutionTime(totalTime));

                writer.write("\n\n===============================");
                if (qaResult != null) {
                    writer.write("\n== Graph score ==\n");
                    writer.write(qaResult.getScore().toString());
                    writer.write("\n\n== Matched QA rules ==\n");
                    for (QualityAssessmentRule matchedRule : qaResult.getTrace()) {
                        writer.write(matchedRule.getCoefficient().toString());
                        writer.write('\t');
                        writer.write(matchedRule.getDescription());
                        writer.write('\n');
                    }
                }

                writer.write("\n\n===============================\n== Metadata ==\n");
                for (NamedGraphMetadata metadata : metadataResult.getMetadata().listMetadata()) {
                    writer.write(metadata.getNamedGraphURI());
                    writer.write('\n');
                    if (metadata.getSources() != null) {
                        writer.write("\tSource: ");
                        writer.write(formatCollection(metadata.getSources()));
                        writer.write('\n');
                    }
                    if (metadata.getInsertedAt() != null) {
                        writer.write("\tInserted at: ");
                        writer.write(formatDate(metadata.getInsertedAt()));
                        writer.write('\n');
                    }
                    if (metadata.getScore() != null) {
                        writer.write("\tGraph score: ");
                        writer.write(metadata.getScore().toString());
                        writer.write('\n');
                    }
                    if (metadata.getUpdateTag() != null) {
                        writer.write("\tUpdate tag: ");
                        writer.write(metadata.getUpdateTag().toString());
                        writer.write('\n');
                    }
                }
                writer.write("\n\n===============================\n== Provenance metadata ==\n");
                for (Statement quad : metadataResult.getProvenanceMetadata()) {
                    writer.write(quad.toString());
                    writer.write('\n');
                }

            }
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }
    
    private <T> String formatCollection(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        } else if (collection.size() == 1) {
            return collection.iterator().next().toString();
        } else {
            final String separator = ", ";
            StringBuilder result = new StringBuilder();
            for (T value : collection) {
                result.append(value.toString());
                result.append(separator);
            }
            return result.substring(0, result.length() - separator.length());
        }
    }
}
