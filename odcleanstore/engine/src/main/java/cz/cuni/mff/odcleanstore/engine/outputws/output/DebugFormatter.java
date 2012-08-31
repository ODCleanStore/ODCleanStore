package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

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
                        writer.write(formatList(metadata.getSources()));
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
                        writer.write(formatList(metadata.getSources()));
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
                }
                writer.write("\n\n===============================\n== Provenance metadata ==\n");
                for (Quad quad : metadataResult.getProvenanceMetadata()) {
                    writer.write(quad.toString());
                    writer.write('\n');
                }

            }
        };
        representation.setCharacterSet(OUTPUT_CHARSET);
        return representation;
    }
    
    private <T> String formatList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return "";
        } else if (list.size() == 1) {
            return list.get(0).toString();
        } else {
            final String separator = ", ";
            StringBuilder result = new StringBuilder();
            for (T value : list) {
                result.append(value.toString());
                result.append(separator);
            }
            return result.substring(0, result.length() - separator.length());
        }
    }
}
