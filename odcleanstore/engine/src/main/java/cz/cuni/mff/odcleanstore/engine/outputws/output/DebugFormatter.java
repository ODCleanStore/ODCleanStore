package cz.cuni.mff.odcleanstore.engine.outputws.output;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.QualityAssessmentRule;
import cz.cuni.mff.odcleanstore.queryexecution.BasicQueryResult;
import cz.cuni.mff.odcleanstore.queryexecution.MetadataQueryResult;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.WriterRepresentation;

import java.io.IOException;
import java.io.Writer;

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
                for (ResolvedStatement crQuad : result.getResultQuads()) {
                    writer.write(crQuad.getStatement().toString());
                    writer.write("\n\tQuality: ");
                    writer.write(formatScore(crQuad.getConfidence()));
                    writer.write("; Sources: ");
                    boolean first = true;
                    for (Resource sourceURI : crQuad.getSourceGraphNames()) {
                        if (!first) {
                            writer.write(", ");
                        }
                        first = false;
                        writer.write(sourceURI.stringValue());
                    }
                    writer.write('\n');
                }

                writer.write("\n===============================\n== Metadata ==\n");
                writeMetadata(writer, result.getMetadata());
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
                writeMetadata(writer, metadataResult.getMetadata());

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
    
    private void writeMetadata(Writer writer, Model metadata) throws IOException {
        for (Resource namedGraph : metadata.subjects()) {
            Model sources = metadata.filter(namedGraph, METADATA_SCORE_PROPERTY, null);
            if (!sources.isEmpty()) {
                writer.write("\tSource: ");
                writer.write(formatObjects(sources));
                writer.write('\n');
            }
            Model insertedAt = metadata.filter(namedGraph, METADATA_INSERTED_AT_PROPERTY, null);
            if (!insertedAt.isEmpty()) {
                writer.write("\tInserted at: ");
                writer.write(formatDate(insertedAt.iterator().next().getObject()));
                writer.write('\n');
            }
            Model score = metadata.filter(namedGraph, METADATA_SCORE_PROPERTY, null);
            if (!score.isEmpty()) {
                writer.write("\tGraph score: ");
                writer.write(formatScore(score.iterator().next().getObject()));
                writer.write('\n');
            }
            Model updateTag = metadata.filter(namedGraph, METADATA_UPDATE_TAG_PROPERTY, null);
            if (!updateTag.isEmpty()) {
                writer.write("\tUpdate tags: ");
                writer.write(updateTag.iterator().next().getObject().stringValue());
                writer.write('\n');
            }
            Model licences = metadata.filter(namedGraph, METADATA_LICENCES_PROPERTY, null);
            if (!licences.isEmpty()) {
                writer.write("\tLicences: ");
                writer.write(formatObjects(licences));
                writer.write('\n');
            }
        }
    }
    
    private String formatObjects(Model model) {
        if (model == null || model.isEmpty()) {
            return "";
        } else if (model.size() == 1) {
            return model.iterator().next().getObject().toString();
        } else {
            final String separator = ", ";
            StringBuilder result = new StringBuilder();
            for (Statement value : model) {
                result.append(value.getObject().toString());
                result.append(separator);
            }
            return result.substring(0, result.length() - separator.length());
        }
    }
}
